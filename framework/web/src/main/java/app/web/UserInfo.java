package app.web;

import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * @author chi
 */
public interface UserInfo extends SecurityContext {
    String id();

    String username();

    String nickname();

    String email();

    String phone();

    String description();

    List<String> roles();

    String imageURL();

    boolean hasRole(String roleName);

    boolean isAuthenticated();
}
