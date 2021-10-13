package app.web.impl;


import app.web.ClientInfo;

/**
 * @author chi
 */
public class ClientInfoImpl implements ClientInfo {
    private final String id;
    private final String language;
    private final String ip;

    public ClientInfoImpl(String id, String language, String ip) {
        this.id = id;
        this.language = language;
        this.ip = ip;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String ip() {
        return ip;
    }

    @Override
    public String city() {
        return "";
    }

    @Override
    public String country() {
        return "";
    }

    @Override
    public String language() {
        return language;
    }

    @Override
    public String os() {
        return "";
    }

    @Override
    public String osVersion() {
        return "";
    }

    @Override
    public String browser() {
        return "";
    }

    @Override
    public String browserVersion() {
        return "";
    }
}
