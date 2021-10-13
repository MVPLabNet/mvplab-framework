package app.undertow.websocket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketDisconnectMessage {
    @XmlElement(name = "clientId")
    public String clientId;
}
