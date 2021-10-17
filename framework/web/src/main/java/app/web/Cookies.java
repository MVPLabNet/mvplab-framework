package app.web;

import jakarta.ws.rs.core.NewCookie;

/**
 * @author chi
 */
public interface Cookies {
    static NewCookie newCookie(String name, String value, int maxAge, String domain) {
        return new NewCookie(name, value, "/", domain, null, maxAge, false);
    }

    static NewCookie newCookie(String name, String value, String domain) {
        return new NewCookie(name, value, "/", domain, null, Integer.MAX_VALUE, false);
    }

    static NewCookie newSessionCookie(String name, String value, String domain) {
        return new NewCookie(name, value, "/", domain, null, -1, false);
    }

    static NewCookie removeCookie(String name, String domain) {
        return new NewCookie(name, null, "/", domain, null, 0, false);
    }
}
