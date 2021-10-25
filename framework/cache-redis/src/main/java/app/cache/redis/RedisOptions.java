package app.cache.redis;


import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RedisOptions {
    @XmlElement(name = "enabled")
    public Boolean enabled;

    @NotNull
    @XmlElement(name = "host")
    public String host;

    @NotNull
    @XmlElement(name = "port")
    public Integer port;
}
