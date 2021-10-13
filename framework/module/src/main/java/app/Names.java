package app;

import javax.inject.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Names {
    private Names() {
    }

    public static Named named(String name) {
        return new NamedImpl(name);
    }

    static class NamedImpl implements Named, Serializable {
        private final String value;

        NamedImpl(String value) {
            this.value = checkNotNull(value, "name");
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public int hashCode() {
            // This is specified in java.lang.Annotation.
            return (127 * "value".hashCode()) ^ value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Named)) {
                return false;
            }
            Named other = (Named) o;
            return value.equals(other.value());
        }

        @Override
        public String toString() {
            return "@" + Named.class.getName() + "(value=" + value + ")";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Named.class;
        }

        private static final long serialVersionUID = 0;
    }
}