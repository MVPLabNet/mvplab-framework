package app.demo.web.user;

import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author chi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterUserResponse {
    @XmlElement(name = "username")
    public String username;

    @Size(max = 16)
    public String password;
}
