package app.cache;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
