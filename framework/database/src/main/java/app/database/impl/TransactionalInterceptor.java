package app.database.impl;

import jakarta.transaction.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author chi
 */
public class TransactionalInterceptor implements MethodInterceptor {
    private final DatabaseImpl database;

    public TransactionalInterceptor(DatabaseImpl database) {
        this.database = database;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Transaction transaction = database.transaction();

        if (transaction.isActive()) {
            return invocation.proceed();
        }

        Transactional transactional = invocation.getMethod().getDeclaredAnnotation(Transactional.class);
        try {
            transaction.begin();
            Object result = invocation.proceed();
            transaction.commit();
            return result;
        } catch (Exception e) {
            handleRollBack(transactional, e, transaction);
            throw e;
        } finally {
            transaction.end();
        }
    }

    private void handleRollBack(Transactional transactional, Exception e, Transaction transaction) {
        for (Class<?> dontRollbackOn : transactional.dontRollbackOn()) {
            if (dontRollbackOn.isInstance(e)) {
                transaction.commit();
                return;
            }
        }

        if (transactional.rollbackOn().length == 0) {
            transaction.rollback();
        } else {
            for (Class<?> rollBackOn : transactional.rollbackOn()) {
                if (rollBackOn.isInstance(e)) {
                    transaction.rollback();
                    return;
                }
            }
            transaction.commit();
        }
    }
}
