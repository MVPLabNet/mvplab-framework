package app.web;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WebOptions {
    @XmlElement(name = "theme")
    public String theme;
    @XmlElement(name = "httpsEnabled")
    public Boolean httpsEnabled;

    @XmlElement(name = "cdnBaseURLs")
    public List<String> cdnBaseURLs = Lists.newArrayList();
    @XmlElement(name = "roots")
    public List<String> roots = Lists.newArrayList();
    @XmlElement(name = "session")
    public SessionOptions session = new SessionOptions();

    @XmlElement(name = "cookie")
    public CookieOptions cookie = new CookieOptions();
    @XmlElement(name = "cacheEnabled")
    public Boolean cacheEnabled = true;
    @XmlElement(name = "inlineResourceEnabled")
    public Boolean inlineResourceEnabled = false;

    @XmlElement(name = "errorPages")
    public Map<Integer, String> errorPages = ImmutableMap.<Integer, String>builder()
        .put(400, "template/404.html")
        .put(401, "/login")
        .put(403, "template/403.html")
        .put(404, "template/404.html")
        .put(500, "template/500.html")
        .build();

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CookieOptions {
        @XmlElement(name = "sessionId")
        public String sessionId = "sessionId";
        @XmlElement(name = "clientId")
        public String clientId = "clientId";
        @XmlElement(name = "language")
        public String language = "language";
        @XmlElement(name = "baseDomain")
        public String baseDomain;
        @XmlElement(name = "domains")
        public Map<String, Boolean> domains;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SessionOptions {
        @XmlElement(name = "expire")
        public Duration expire = Duration.ofMinutes(30);
        @XmlElement(name = "redis")
        public RedisOptions redis;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RedisOptions {
        @XmlElement(name = "host")
        public String host;
        @XmlElement(name = "port")
        public Integer port;
    }
}
