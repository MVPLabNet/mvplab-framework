package app.service;

/**
 * @author chi
 */
public interface ServiceConfig {
    <T> ServiceConfig service(Class<T> clientClass, String remoteServerURL);
}
