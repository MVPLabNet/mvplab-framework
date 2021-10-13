package app.util.type;

import app.ApplicationException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chi
 */
public class ClassCompiler<T> {
    private static final AtomicInteger INDEX = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(ClassCompiler.class);
    private final CtClass classBuilder;
    private final ClassPool classPool;
    private Class[] constructorParamClasses;

    public ClassCompiler(Class<?> interfaceClass, String modifier) {
        if (!interfaceClass.isInterface())
            throw new ApplicationException("interface class must be interface, interfaceClass={}", interfaceClass);

        classPool = ClassPool.getDefault();
        classBuilder = classPool.makeClass(interfaceClass.getName() + "$" + modifier + INDEX.getAndIncrement());

        try {
            classBuilder.addInterface(classPool.get(interfaceClass.getName()));
            CtConstructor constructor = new CtConstructor(null, classBuilder);
            constructor.setBody(";");
            classBuilder.addConstructor(constructor);
        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassCompiler<T> constructor(Class[] constructorParamClasses, String body, Object... args) {
        if (this.constructorParamClasses != null)
            throw new Error("dynamic class must have no more than one custom constructor");

        try {
            this.constructorParamClasses = constructorParamClasses;
            CtClass[] params = new CtClass[constructorParamClasses.length];
            for (int i = 0; i < constructorParamClasses.length; i++) {
                Class<?> paramClass = constructorParamClasses[i];
                params[i] = classPool.getCtClass(paramClass.getCanonicalName());
            }
            CtConstructor constructor = new CtConstructor(params, classBuilder);
            constructor.setBody(format(body, args));
            classBuilder.addConstructor(constructor);
            return this;
        } catch (CannotCompileException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassCompiler<T> addMethod(String method, Object... args) {
        try {
            classBuilder.addMethod(CtMethod.make(format(method, args), classBuilder));
            return this;
        } catch (CannotCompileException e) {
            logger.error("method failed to compile:\n{}", method);
            throw new RuntimeException(e);
        }
    }

    public ClassCompiler<T> addField(String field, Object... args) {
        try {
            classBuilder.addField(CtField.make(format(field, args), classBuilder));
            return this;
        } catch (CannotCompileException e) {
            logger.error("field failed to compile:\n{}", field);
            throw new RuntimeException(e);
        }
    }

    private String format(String field, Object... args) {
        return MessageFormatter.arrayFormat(field, args).getMessage();
    }

    public Class<T> compile() {
        try {
            @SuppressWarnings("unchecked")
            Class<T> targetClass = (Class<T>) classBuilder.toClass();
            classBuilder.detach();
            return targetClass;
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }
}
