package app.database;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseOptions {
    @XmlElement(name = "url")
    public String url;

    @XmlElement(name = "username")
    public String username;

    @XmlElement(name = "password")
    public String password;

    @XmlElement(name = "dialect")
    public String dialect;

    @XmlElement(name = "driver")
    public String driver;

    @XmlElement(name = "createTableEnabled")
    public Boolean createTableEnabled = false;

    @NotNull
    @XmlElement(name = "showSQLEnabled")
    public Boolean showSQLEnabled = false;

    @Valid
    @XmlElement(name = "pool")
    public PoolOptions pool = new PoolOptions();

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PoolOptions {
        @NotNull
        @XmlElement(name = "max")
        public Integer max = 32;

        @NotNull
        @XmlElement(name = "min")
        public Integer min = 8;
    }
}
