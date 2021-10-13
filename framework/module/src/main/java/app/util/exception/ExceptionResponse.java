package app.util.exception;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlAccessorType
@XmlRootElement
public class ExceptionResponse {
    @XmlElement(name = "message")
    public String message;
}
