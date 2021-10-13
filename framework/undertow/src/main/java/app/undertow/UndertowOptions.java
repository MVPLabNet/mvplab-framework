package app.undertow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
