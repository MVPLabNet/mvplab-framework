package app.message.redis;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RedisMessageOptions {
    @XmlElement(name = "host")
    public String host;

    @XmlElement(name = "port")
    public Integer port;
}

