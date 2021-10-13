package app.message.redis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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

