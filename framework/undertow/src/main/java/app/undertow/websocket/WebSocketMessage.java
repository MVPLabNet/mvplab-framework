package app.undertow.websocket;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketMessage {
    @XmlElement(name = "clientId")
    public String clientId;
    @XmlElement(name = "body")
    public String body;
}
