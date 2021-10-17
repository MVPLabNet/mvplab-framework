package app.database.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author chi
 */
@Entity
@Table(name = "test_entity")
public class TestEntity {
    @Id
    @Column(name = "id", length = 36)
    public String id;

    @Column(name = "name", length = 32)
    public String name;
}
