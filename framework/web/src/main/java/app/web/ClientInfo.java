package app.web;

/**
 * @author chi
 */
public interface ClientInfo {
    String id();

    String ip();

    String city();

    String country();

    String language();

    String os();

    String osVersion();

    String browser();

    String browserVersion();
}
