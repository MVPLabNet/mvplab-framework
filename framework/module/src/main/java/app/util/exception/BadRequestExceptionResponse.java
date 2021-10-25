package app.util.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BadRequestExceptionResponse {
    @XmlElement(name = "field")
    public String field;
    @XmlElement(name = "message")
    public String message;
}
