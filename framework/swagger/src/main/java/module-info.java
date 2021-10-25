/**
 * @author chi
 */
module app.swagger {
    requires transitive app.module;
    requires transitive app.web;

    requires io.swagger.v3.core;
    requires io.swagger.v3.oas.integration;
    requires io.swagger.v3.oas.annotations;
    requires io.swagger.v3.oas.models;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;
    requires io.github.classgraph;
    requires java.xml.bind;

    exports app.swagger;
}
