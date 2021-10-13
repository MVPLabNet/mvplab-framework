/**
 * @author chi
 */
module app.app {
    requires app.module;
    requires jersey.server;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires jersey.hk2;
    requires hk2.api;
    requires hk2.utils;
    requires jersey.bean.validation;
    requires javassist;

    requires jul.to.slf4j;
    requires jackson.annotations;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports app.app;
}