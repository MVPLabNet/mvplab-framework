package app.app.impl.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationExceptionResponse {
    @XmlElement(name = "path")
    public String path;

    @XmlElement(name = "errorMessage")
    public String errorMessage;
}
