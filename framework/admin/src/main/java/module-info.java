/**
 * @author chi
 */
module app.admin {
    requires transitive app.module;
    requires transitive app.web;

    exports app.admin;
}