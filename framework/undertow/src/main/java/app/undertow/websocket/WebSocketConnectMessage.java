package app.undertow.websocket;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketConnectMessage {
    @XmlElement(name = "clientId")
    public String clientId;
}
