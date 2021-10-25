package app.log;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.List;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LogbackOptions {
    @XmlElement(name = "level")
    public String level = "INFO";
    @XmlElement(name = "dir")
    public String dir;
    @XmlElement(name = "pattern")
    public String pattern = "%d [%thread] %-5level %logger{5} - %msg %n";
    @XmlElement(name = "excludePackages")
    public List<String> excludePackages = Arrays.asList("org.elasticsearch", "org.mongodb", "org.apache", "org.xnio", "org.hibernate", "org.quartz");
    @XmlElement(name = "rollingPolicy")
    public RollingPolicy rollingPolicy = RollingPolicy.DAILY;

    public enum RollingPolicy {
        NONE, DAILY, HOURLY
    }
}
