package app.template;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Map;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentView {
    @XmlElement(name = "id")
    public String id;
    @XmlElement(name = "name")
    public String name;
    @XmlElement(name = "attributes")
    public Map<String, Object> attributes;
}
