package app.undertow;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UndertowOptions {
    @XmlElement(name = "host")
    public String host = "localhost";
    @XmlElement(name = "port")
    public int port = 8080;
    @XmlElement(name = "webSocketEnabled")
    public Boolean webSocketEnabled = true;
}
