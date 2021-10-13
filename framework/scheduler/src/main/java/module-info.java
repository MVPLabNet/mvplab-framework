/**
 * @author chi
 */
module app.scheduler {
    requires transitive app.module;
    requires quartz;
    exports app.scheduler;
}