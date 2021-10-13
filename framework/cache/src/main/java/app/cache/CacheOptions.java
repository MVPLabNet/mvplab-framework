package app.cache;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.Duration;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CacheOptions {
    @XmlElement(name = "expireTime")
    public Duration expireTime = Duration.ofDays(1);
    @XmlElement(name = "maxElements")
    public Integer maxElements = Integer.MAX_VALUE;
}
