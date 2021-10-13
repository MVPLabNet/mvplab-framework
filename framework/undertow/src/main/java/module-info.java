/**
 * @author chi
 */
module app.undertow {
    requires transitive app.module;
    requires transitive app.app;
    requires app.message;
    requires undertow.core;
    requires xnio.api;
    requires jersey.server;
    requires jersey.common;
    requires jersey.media.json.jackson;
    requires jersey.hk2;
    requires jersey.bean.validation;

    requires javassist;
    requires hk2.api;
    requires hk2.utils;
    requires hk2.locator;

    exports app.undertow;
    exports app.undertow.websocket;
}