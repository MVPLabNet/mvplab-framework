package app.util.exception;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlAccessorType
@XmlRootElement
public class ExceptionResponse {
    @XmlElement(name = "message")
    public String message;
}
