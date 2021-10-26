package app.web.impl;

import app.web.UserInfo;
import com.google.common.collect.ImmutableList;

import java.security.Principal;
import java.util.List;

/**
 * @author chi
 */
public class UserInfoImpl implements UserInfo {
    private final String ip;

    public UserInfoImpl(String ip) {
        this.ip = ip;
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String tenantId() {
        return null;
    }

    @Override
    public String username() {
        return ip;
    }

    @Override
    public String nickname() {
        return ip;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String email() {
        return null;
    }

    @Override
    public String phone() {
        return null;
    }

    @Override
    public List<String> roles() {
        return ImmutableList.of("*");
    }

    @Override
    public String imageURL() {
        return null;
    }

    @Override
    public boolean hasRole(String roleName) {
        return true;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return FORM_AUTH;
    }
}
