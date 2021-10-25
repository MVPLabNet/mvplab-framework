package app.web.impl.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ExceptionResponse {
    @XmlElement(name = "errorMessage")
    public String errorMessage;
}
