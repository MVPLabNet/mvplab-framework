package app.util.type;


import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class Types {
    public static Type generic(Class<?> rawClass, Type... arguments) {
        return new ParameterizedTypeImpl(rawClass, arguments, rawClass.getEnclosingClass());
    }

    public static String className(Type type) {
        return rawClass(type).getCanonicalName();
    }

    public static Class<?> rawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new RuntimeException("unsupported type, type=" + type);
        }
    }

    public static String enumVariableLiteral(Enum value) {
        return value.getDeclaringClass().getCanonicalName() + "." + value.name();
    }

    public static String typeVariableLiteral(Type type) {
        StringBuilder b = new StringBuilder();
        if (Types.isGeneric(type)) {
            b.append(Types.class.getCanonicalName())
                .append(".generic(");
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class ownerType = (Class) parameterizedType.getRawType();
            if (Types.isMap(ownerType)) {
                b.append(Map.class.getCanonicalName());
            } else if (Types.isList(ownerType)) {
                b.append(List.class.getCanonicalName());
            } else {
                b.append(ownerType.getCanonicalName());
            }
            b.append(".class, new java.lang.reflect.Type[]{");

            Type[] arguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < arguments.length; i++) {
                if (i != 0) {
                    b.append(',');
                }
                b.append(typeVariableLiteral(arguments[i]));
            }
            b.append("})");
        } else {
            b.append(((Class) type).getCanonicalName());
            b.append(".class");
        }
        return b.toString();
    }

    public static boolean isList(Type type) {
        return List.class.isAssignableFrom(rawClass(type));
    }

    public static boolean isArray(Type type) {
        return type instanceof Class && ((Class) type).getComponentType() != null;
    }

    public static boolean isIterable(Type type) {
        return Iterable.class.isAssignableFrom(rawClass(type));
    }

    public static boolean isGeneric(Type type) {
        return type instanceof ParameterizedType;
    }

    public static boolean isOptional(Type type) {
        return Optional.class.isAssignableFrom(rawClass(type));
    }

    public static boolean isMap(Type type) {
        return Map.class.isAssignableFrom(rawClass(type));
    }

    public static boolean isEnum(Type type) {
        return Enum.class.isAssignableFrom(rawClass(type));
    }

    public static boolean isClass(Type type) {
        return type instanceof Class;
    }

    public static boolean isTypeVariable(Type type) {
        return type instanceof TypeVariable;
    }

    public static Optional<Type> actualType(Type genericType, Type variableType) {
        String name = rawClass(genericType).toGenericString();
        boolean found = false;
        StringBuilder b = new StringBuilder();
        List<String> variables = Lists.newArrayList();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '<') {
                found = true;
            } else if (c == ',' && found) {
                variables.add(b.toString());
                b.delete(0, b.length());
            } else if (c == '>') {
                variables.add(b.toString());
                break;
            } else if (found) {
                b.append(c);
            }
        }

        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).equals(variableType.toString())) {
                return Optional.of(arguments(genericType)[i]);
            }
        }
        return Optional.empty();
    }


    public static boolean isPrimitive(Type type) {
        Class<?> rawClass = Types.rawClass(type);
        return Primitives.allPrimitiveTypes().contains(rawClass) || Primitives.allWrapperTypes().contains(rawClass);
    }

    public static boolean hasSuperClass(Type type) {
        return isClass(type) && !Object.class.equals(((Class) type).getSuperclass()) && ((Class) type).getSuperclass() != null;
    }

    public static boolean hasInterface(Type type) {
        return isClass(type) && ((Class) type).getInterfaces().length > 0;
    }

    public static Type[] arguments(Type type) {
        if (isGeneric(type)) {
            return ((ParameterizedType) type).getActualTypeArguments();
        } else {
            return new Type[0];
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Type rawType;
        private final Type[] arguments;
        private final Type ownerType;

        ParameterizedTypeImpl(Class<?> rawType, Type[] arguments, Type ownerType) {
            this.rawType = rawType;
            this.arguments = arguments;
            this.ownerType = ownerType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return arguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public int hashCode() {
            return (ownerType == null ? 0 : ownerType.hashCode())
                ^ Arrays.hashCode(arguments)
                ^ rawType.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType that = (ParameterizedType) other;
            return rawType.equals(that.getRawType())
                && java.util.Objects.equals(ownerType, that.getOwnerType())
                && Arrays.equals(arguments, that.getActualTypeArguments());
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (ownerType != null) {
                builder.append(ownerType.getTypeName()).append('.');
            }
            builder.append(rawType.getTypeName())
                .append('<');

            int i = 0;
            for (Type argument : arguments) {
                if (i > 0) builder.append(", ");
                builder.append(argument.getTypeName());
                i++;
            }

            builder.append('>');
            return builder.toString();
        }
    }
}
