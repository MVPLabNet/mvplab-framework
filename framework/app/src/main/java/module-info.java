/**
 * @author chi
 */
module app.app {
    requires app.module;
    requires jersey.server;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires jersey.hk2;
    requires org.glassfish.hk2.api;
    requires org.glassfish.hk2.utilities;
    requires jakarta.ws.rs;
    requires jersey.bean.validation;
    requires javassist;

    requires jul.to.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports app.app;
}
