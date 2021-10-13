import app.AbstractModule;

/**
 * @author chi
 */
module app.test {
    requires transitive app.module;
    requires transitive app.app;
    requires transitive app.database;
    requires transitive app.logback;
    requires transitive org.mockito;
    requires transitive org.junit.jupiter.api;

    requires jersey.server;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires jersey.hk2;
    requires jersey.bean.validation;

    exports app.test;
    uses AbstractModule;
}