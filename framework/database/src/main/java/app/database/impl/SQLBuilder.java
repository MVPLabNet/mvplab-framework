package app.database.impl;

import app.util.exception.Errors;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Locale;

/**
 * @author chi
 */
public class SQLBuilder {
    private final String entityName;
    private final String idFieldName;
    private final StringBuilder sql;
    private List<SortingField> sortingFields;
    private int batchSize = 1000;

    public SQLBuilder(String entityName, String idFieldName, String sql) {
        this.entityName = entityName;
        this.idFieldName = idFieldName;
        this.sql = new StringBuilder(sql);
    }

    public SQLBuilder setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public SQLBuilder append(String conditions) {
        this.sql.append(' ').append(conditions);
        return this;
    }

    public SQLBuilder sort(String field, Boolean desc) {
        if (field == null) {
            return this;
        }
        if (sortingFields == null) {
            sortingFields = Lists.newArrayList();
        }
        sortingFields.add(new SortingField(field, desc != null && desc));
        return this;
    }

    public String selectSQL() {
        StringBuilder b = new StringBuilder(sql);
        if (sortingFields != null) {
            b.append(" ORDER BY ");
            boolean first = true;
            for (SortingField sortingField : sortingFields) {
                if (first) {
                    first = false;
                } else {
                    b.append(',');
                }
                b.append(sortingField.field);
                if (sortingField.desc) {
                    b.append(" DESC");
                }
            }
        }
        return b.toString();
    }

    public String countSQL() {
        int p = sql.toString().toLowerCase(Locale.ENGLISH).indexOf("from");
        if (p > 0) {
            return "SELECT count(0) " + sql.substring(p);
        } else {
            throw Errors.internalError("not valid select statement, statement={}", sql.toString());
        }
    }

    public String deleteByIdSQL() {
        return "DELETE FROM " + entityName + " t WHERE t." + idFieldName + "=?0";
    }

    public String deleteByIdsSQL(int size) {
        StringBuilder b = new StringBuilder();
        b.append("DELETE FROM ").append(entityName).append(" t WHERE t.").append(idFieldName).append(" in (");

        for (int i = 0; i < size; i++) {
            int index = i % batchSize;
            if (i != 0 && index == 0) {
                b.append(") OR t.").append(idFieldName).append(" in (");
            } else if (index != 0) {
                b.append(',');
            }
            b.append('?').append(i);
        }
        b.append(')');
        return b.toString();
    }

    public String findByIdsSQL(int size) {
        StringBuilder b = new StringBuilder();
        b.append("SELECT t FROM ").append(entityName).append(" t WHERE t.").append(idFieldName).append(" in (");

        for (int i = 0; i < size; i++) {
            int index = i % batchSize;
            if (i != 0 && index == 0) {
                b.append(") OR t.").append(idFieldName).append(" in (");
            } else if (index != 0) {
                b.append(',');
            }
            b.append('?').append(i);
        }
        b.append(')');
        return b.toString();
    }

    public String sql() {
        return sql.toString();
    }

    private class SortingField {
        final String field;
        final boolean desc;

        SortingField(String field, boolean desc) {
            this.field = field;
            this.desc = desc;
        }
    }
}
