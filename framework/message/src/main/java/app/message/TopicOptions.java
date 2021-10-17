package app.message;


import jakarta.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TopicOptions {
    @NotNull
    @XmlElement(name = "durable")
    public Boolean durable = false;

    @XmlElement(name = "broadcast")
    public Boolean broadcast = false;

    @XmlElement(name = "batchSize")
    public Integer batchSize = 100;

    @XmlElement(name = "prefetchSize")
    public Integer prefetchSize = 2;
}
