package app.web.impl.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationWebExceptionResponse {
    @XmlElement(name = "field")
    public String field;
    @XmlElement(name = "errorMessage")
    public String errorMessage;
}
