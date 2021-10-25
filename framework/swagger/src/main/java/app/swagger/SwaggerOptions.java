package app.swagger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SwaggerOptions {
    @XmlElement(name = "apiEnabled")
    public Boolean apiEnabled;
    @XmlElement(name = "webAPIEnabled")
    public Boolean webAPIEnabled;
    @XmlElement(name = "adminAPIEnabled")
    public Boolean adminAPIEnabled;
}
