package app;

import app.util.exception.Errors;
import app.util.type.CodeBuilder;
import app.util.type.Types;
import com.google.common.collect.Sets;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chi
 */
public class ServiceDelegateBuilder<T> {
    private static final AtomicInteger INDEX = new AtomicInteger();
    private static final Set<String> EXCLUDE_ANNOTATION_METHODS = Sets.newHashSet();

    static {
        for (Method method : java.lang.annotation.Annotation.class.getDeclaredMethods()) {
            EXCLUDE_ANNOTATION_METHODS.add(method.getName());
        }
    }

    private final Class<T> serviceClass;
    private final CtClass classBuilder;
    private final ConstPool constPool;

    public ServiceDelegateBuilder(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        ClassPool classPool = ClassPool.getDefault();
        classBuilder = classPool.makeClass(serviceClass.getCanonicalName() + "$Delegate" + INDEX.incrementAndGet());
        try {
            classBuilder.addInterface(classPool.get(serviceClass.getName()));
            constPool = classBuilder.getClassFile().getConstPool();

            java.lang.annotation.Annotation[] annotations = serviceClass.getDeclaredAnnotations();
            if (annotations.length > 0) {
                AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                for (java.lang.annotation.Annotation annotation : annotations) {
                    attr.addAnnotation(annotation(annotation));
                }
                classBuilder.getClassFile().addAttribute(attr);
            }
        } catch (Exception e) {
            throw Errors.internalError(e);
        }

    }

    @SuppressWarnings("unchecked")
    public Class<? extends T> build() {
        try {
            addDelegateField(classBuilder);
            for (Method method : serviceClass.getMethods()) {
                addMethod(method, classBuilder);
            }
            return (Class<? extends T>) classBuilder.toClass();
        } catch (CannotCompileException e) {
            throw Errors.internalError("failed to generate delegate, type={}", serviceClass, e);
        }
    }

    private void addDelegateField(CtClass classBuilder) throws CannotCompileException {
        CtField field = CtField.make(String.format("%s service;", serviceClass.getCanonicalName()), classBuilder);
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        attr.addAnnotation(annotation(new InjectImpl()));
        field.getFieldInfo().addAttribute(attr);
        classBuilder.addField(field);
    }

    private void addMethod(Method method, CtClass classBuilder) throws CannotCompileException {
        CtMethod ctMethod = CtMethod.make(method(method), classBuilder);
        classBuilder.addMethod(ctMethod);
        java.lang.annotation.Annotation[] annotations = method.getDeclaredAnnotations();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        if (annotations.length > 0) {
            for (java.lang.annotation.Annotation annotation : annotations) {
                attr.addAnnotation(annotation(annotation));
            }
        }
        if (!method.isAnnotationPresent(Produces.class)) {
            attr.addAnnotation(annotation(new ProducesImpl()));
        }
        ctMethod.getMethodInfo().addAttribute(attr);

        ParameterAnnotationsAttribute parameterAnnotationsAttribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
        java.lang.annotation.Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Annotation[][] paramArrays = new Annotation[parameterAnnotations.length][];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            java.lang.annotation.Annotation[] parameterAnnotation = parameterAnnotations[i];
            Annotation[] ctAnnotations = new Annotation[parameterAnnotation.length];
            for (int j = 0; j < parameterAnnotation.length; j++) {
                ctAnnotations[j] = annotation(parameterAnnotation[j]);
            }
            paramArrays[i] = ctAnnotations;
        }
        parameterAnnotationsAttribute.setAnnotations(paramArrays);
        ctMethod.getMethodInfo().addAttribute(parameterAnnotationsAttribute);
    }

    private String method(Method method) {
        CodeBuilder b = new CodeBuilder();
        b.append("public {} {}(", method.getReturnType().getCanonicalName(), method.getName());

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < genericParameterTypes.length; i++) {
            Type type = genericParameterTypes[i];
            if (i != 0) {
                b.append(",");
            }
            b.append(Types.rawClass(type).getCanonicalName());
            b.append(" ").append("param").append(String.valueOf(i));
        }

        b.append("){");
        if (!method.getReturnType().equals(void.class)) {
            b.append("return ");
        }
        b.append("service.{}(", method.getName());
        for (int i = 0; i < genericParameterTypes.length; i++) {
            if (i != 0) {
                b.append(",");
            }
            b.append("param").append(String.valueOf(i));
        }
        b.append(");");
        b.append("}");
        return b.build();
    }

    private Annotation annotation(java.lang.annotation.Annotation annotation) {
        Annotation ctAnnotation = new Annotation(annotation.annotationType().getCanonicalName(), constPool);
        try {
            for (Method method : annotation.annotationType().getMethods()) {
                if (!isMethodExclude(method)) {
                    Object value = method.invoke(annotation);
                    ctAnnotation.addMemberValue(method.getName(), memberValue(method.getReturnType(), value));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw Errors.internalError("failed to create ct annotation", e);
        }
        return ctAnnotation;
    }

    private MemberValue memberValue(Class<?> type, Object value) {
        if (type.isArray()) {
            Object[] array = (Object[]) value;
            MemberValue[] memberValues = new MemberValue[array.length];

            Class<?> componentType = type.getComponentType();
            for (int i = 0; i < array.length; i++) {
                memberValues[i] = memberValue(componentType, array[i]);
            }
            ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
            arrayMemberValue.setValue(memberValues);
            return arrayMemberValue;
        } else {
            if (type == int.class) {
                return new IntegerMemberValue(constPool, (int) value);
            } else if (long.class.equals(type)) {
                return new LongMemberValue((long) value, constPool);
            } else if (short.class.equals(type)) {
                return new ShortMemberValue((short) value, constPool);
            } else if (byte.class.equals(type)) {
                return new ByteMemberValue((byte) value, constPool);
            } else if (char.class.equals(type)) {
                return new CharMemberValue((char) value, constPool);
            } else if (float.class.equals(type)) {
                return new FloatMemberValue((float) value, constPool);
            } else if (double.class.equals(type)) {
                return new DoubleMemberValue((double) value, constPool);
            } else if (String.class.equals(type)) {
                return new StringMemberValue((String) value, constPool);
            } else if (boolean.class.equals(type)) {
                return new BooleanMemberValue((boolean) value, constPool);
            } else if (Class.class.equals(type)) {
                return new ClassMemberValue(((Class) value).getName(), constPool);
            } else if (Enum.class.isAssignableFrom(type)) {
                EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
                enumMemberValue.setType(type.getName());
                enumMemberValue.setValue(((Enum) value).name());
                return enumMemberValue;
            } else {
                throw Errors.internalError("unsupported annotation method type, type={}", type);
            }
        }
    }

    private boolean isMethodExclude(Method method) {
        return EXCLUDE_ANNOTATION_METHODS.contains(method.getName());
    }

    private static class InjectImpl implements Inject {
        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return Inject.class;
        }
    }

    private static class ProducesImpl implements Produces {
        @Override
        public String[] value() {
            return new String[]{MediaType.APPLICATION_JSON};
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return Produces.class;
        }
    }
}
