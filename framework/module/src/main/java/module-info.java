/**
 * @author chi
 */
module app.module {
    requires transitive com.google.common;
    requires transitive java.xml.bind;
    requires transitive org.aopalliance;
    requires transitive jakarta.annotation;
    requires transitive jakarta.inject;
    requires transitive org.slf4j;
    requires transitive jakarta.validation;
    requires transitive jakarta.ws.rs;
    requires javassist;

    requires jul.to.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.module.jaxb;

    requires org.objectweb.asm;
    requires org.hibernate.validator;

    exports app;
    exports app.resource;
    exports app.util;
    exports app.util.collection;
    exports app.util.i18n;
    exports app.util.type;
    exports app.util.exception;
    exports app.util.date;
}
