package app.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * @author chi
 */
@Table(name = "demo_entity")
@Entity
public class DemoEntity {
    @Id
    @Column(name = "id")
    @NotNull
    public String id;

    @Column(name = "name", length = 16)
    public String name;
}
