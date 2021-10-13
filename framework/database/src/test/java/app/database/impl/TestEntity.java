package app.database.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
