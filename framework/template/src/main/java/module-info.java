/**
 * @author chi
 */
module app.template {
    requires transitive app.module;
    requires jericho.html;
    requires commons.jexl3;
    exports app.template;
}