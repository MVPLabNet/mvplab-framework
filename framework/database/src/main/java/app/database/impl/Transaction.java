package app.database.impl;

import javax.persistence.EntityTransaction;
import java.sql.SQLException;


/**
 * @author chi
 */
public class Transaction {
    private final DatabaseImpl database;
    private final TransactionalEntityManager em;

    public Transaction(DatabaseImpl database) {
        this.database = database;
        this.em = new TransactionalEntityManager(database.em());
    }

    public void begin() {
        EntityTransaction transaction = transaction();
        transaction.begin();
    }

    public void commit() {
        EntityTransaction transaction = transaction();
        transaction.commit();
    }

    public void rollback() {
        EntityTransaction transaction = transaction();
        transaction.rollback();
    }

    public boolean isActive() {
        EntityTransaction transaction = transaction();
        return transaction.isActive();
    }

    public void end() throws SQLException {
        TransactionalEntityManager em = em();
        if (em == null) {
            return;
        }
        try {
            em.raw().close();
        } finally {
            database.resetTransaction();
        }
    }

    private EntityTransaction transaction() {
        return em().getTransaction();
    }

    TransactionalEntityManager em() {
        return em;
    }
}
