package app.web.impl.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ExceptionResponse {
    @XmlElement(name = "errorMessage")
    public String errorMessage;
}
