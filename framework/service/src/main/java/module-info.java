/**
 * @author chi
 */
module app.service {
    requires transitive app.module;

    requires jersey.client;
    requires jersey.proxy.client;
    requires javassist;
    requires jersey.bean.validation;
    requires jersey.media.json.jackson;

    exports app.service;
}