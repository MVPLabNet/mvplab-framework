package app.admin.impl.web.api.dashboard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DashboardResponse {
    @XmlElement(name = "name")
    public String name;
    @XmlElement(name = "componentName")
    public String componentName;
    @XmlElement(name = "bundleName")
    public String bundleName;
    @XmlElement(name = "displayOrder")
    public Integer displayOrder;
}
