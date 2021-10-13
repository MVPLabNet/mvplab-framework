/**
 * @author chi
 */
module app.database {
    requires transitive java.transaction;
    requires transitive java.persistence;
    requires transitive java.sql;
    requires transitive java.management;
    requires transitive java.xml.bind;
    requires transitive app.module;

    requires org.hibernate.orm.core;
    requires commons.dbcp2;

    exports app.database;
}