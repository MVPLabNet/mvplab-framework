/**
 * @author chi
 */
module app.database {
    requires transitive jakarta.transaction;
    requires transitive jakarta.persistence;
    requires transitive java.sql;
    requires transitive java.management;
    requires transitive jakarta.xml.bind;
    requires transitive app.module;

    requires org.hibernate.orm.core;
    requires commons.dbcp2;

    exports app.database;
}
