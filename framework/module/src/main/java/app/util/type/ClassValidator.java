package app.util.type;

import app.ApplicationException;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author chi
 */
public class ClassValidator {
    private final Type type;
    private final Set<Type> allowedTypes = Sets.newHashSet();
    private final Set<Type> visited = Sets.newHashSet();
    private boolean arrayAllowed = false;
    private boolean genericAllowed = false;
    private boolean interfaceAllowed = false;
    private boolean superClassAllowed = false;
    private boolean enumAllowed = false;
    private boolean methodAllowed = false;

    public ClassValidator(Type type) {
        this.type = type;
    }

    public ClassValidator allowArray() {
        arrayAllowed = true;
        return this;
    }

    public ClassValidator allowGeneric() {
        genericAllowed = true;
        return this;
    }

    public ClassValidator allowInterface() {
        interfaceAllowed = true;
        return this;
    }

    public ClassValidator allowSuperClass() {
        superClassAllowed = true;
        return this;
    }

    public ClassValidator allowMethod() {
        methodAllowed = true;
        return this;
    }

    public ClassValidator allowEnum() {
        enumAllowed = true;
        return this;
    }

    public ClassValidator allow(Type type) {
        allowedTypes.add(type);
        return this;
    }

    public boolean validate() {
        validate("", type);
        return true;
    }

    private void validate(String fieldName, Type fieldType) {
        if (visited.contains(fieldType) || fieldName.indexOf("$jacocoData") > 0 || isTypeAllowed(fieldType)) {
            return;
        }
        visited.add(fieldType);

        if (Types.isArray(fieldType)) {
            if (!arrayAllowed) {
                throw new ApplicationException(fieldName, "array not allowed, type={}", type);
            } else {
                Class<?> componentType = Types.rawClass(fieldType).getComponentType();
                validate(fieldName, componentType);
            }
        } else if (Types.isGeneric(fieldType)) {
            if (!genericAllowed) {
                throw new ApplicationException(fieldName, "generic not allowed, fieldName={}, type={}", fieldName, type);
            }

            Class<?> rawClass = Types.rawClass(fieldType);
            if (!isTypeAllowed(rawClass)) {
                throw new ApplicationException("generic raw class not allowed, fieldName={}, type={}", fieldName, type);
            }

            Type[] argumentTypes = Types.arguments(fieldType);
            for (Type argumentType : argumentTypes) {
                validate(fieldName, argumentType);
            }
        } else if (Types.isEnum(fieldType)) {
            if (!enumAllowed) {
                throw new ApplicationException("enum not allowed, fieldName={}, type={}, enumType={}", fieldName, type, fieldType);
            }
        } else {
            Class fieldClass = Types.rawClass(fieldType);

            if (!interfaceAllowed && hasInterface(fieldClass)) {
                throw new ApplicationException("interface not allowed, fieldName={}, type={}", fieldName, type);
            }

            if (!superClassAllowed && hasSuperClass(fieldClass)) {
                throw new ApplicationException("super class not allowed, fieldName={}, type={}", fieldName, type);
            }

            for (Method method : fieldClass.getDeclaredMethods()) {
                if (method.getName().contains("$")) {
                    continue;
                }
                if (!methodAllowed) {
                    throw new ApplicationException("method not allowed, method={}, type={}, method={}", fieldName + '.' + method.getName() + "()", this.type, method.getName());
                }
            }

            for (Field field : fieldClass.getDeclaredFields()) {
                if (field.getName().contains("$")) {
                    continue;
                }
                String currentFieldName = fieldName + '.' + field.getName();
                if (!isValidField(field)) {
                    throw new ApplicationException("field must be public without static/volatile/final, fieldName={}, type={}", currentFieldName, type);
                }
                validate(currentFieldName, field.getGenericType());
            }
        }
    }

    private boolean hasInterface(Class fieldClass) {
        return fieldClass.getInterfaces().length > 0;
    }

    private boolean hasSuperClass(Class fieldClass) {
        Class superclass = fieldClass.getSuperclass();
        return superclass != null && !Object.class.equals(superclass);
    }

    private boolean isTypeAllowed(Type type) {
        return allowedTypes.contains(type);
    }

    private boolean isValidField(Field field) {
        return Modifier.isPublic(field.getModifiers())
            && !Modifier.isStatic(field.getModifiers())
            && !Modifier.isVolatile(field.getModifiers())
            && !Modifier.isFinal(field.getModifiers());
    }

}
