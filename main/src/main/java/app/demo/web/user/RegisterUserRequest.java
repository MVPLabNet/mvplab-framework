package app.demo.web.user;

import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterUserRequest {
    @XmlElement(name = "username")
    @Size(max = 16)
    public String username;

    @Size(max = 16)
    @XmlElement(name = "password")
    public String password;
}
