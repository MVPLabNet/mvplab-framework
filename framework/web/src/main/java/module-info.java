import app.AbstractModule;
import app.web.WebModule;

/**
 * @author chi
 */
module app.web {
    requires transitive app.template;
    requires jedis;
    requires jersey.common;

    exports app.web;
    provides AbstractModule with WebModule;
}