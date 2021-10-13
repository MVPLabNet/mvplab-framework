module app.logback {
    requires transitive app.module;

    requires logback.core;
    requires logback.classic;

    exports app.log;
}