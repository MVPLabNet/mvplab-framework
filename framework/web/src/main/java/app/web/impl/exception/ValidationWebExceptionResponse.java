package app.web.impl.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
