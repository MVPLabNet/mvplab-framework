/**
 * @author chi
 */
module app.module {
    requires transitive com.google.common;
    requires transitive java.xml.bind;
    requires transitive aopalliance.repackaged;
    requires transitive javax.annotation.api;
    requires transitive javax.inject;
    requires transitive org.slf4j;
    requires transitive java.validation;
    requires transitive java.ws.rs;
    requires javassist;

    requires jul.to.slf4j;
    requires jackson.annotations;
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