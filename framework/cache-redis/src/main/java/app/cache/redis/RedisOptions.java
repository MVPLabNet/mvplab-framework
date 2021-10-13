package app.cache.redis;


import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
