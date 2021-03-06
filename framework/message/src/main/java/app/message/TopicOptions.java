package app.message;


import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

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
