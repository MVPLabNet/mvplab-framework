package app.demo.service;

import app.database.QueryV2;
import app.database.Repository;
import app.demo.entity.DemoEntity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author chi
 */
public class DemoEntityService {
    @Inject
    Repository<DemoEntity> repository;

    @Transactional
    public void create(DemoEntity entity) {
        repository.insert(entity);
    }

    public DemoEntity get() {
        final QueryV2<DemoEntity> query = repository.queryV2("SELECT t FROM DemoEntity t");
        final Optional<DemoEntity> one = query.findOne();
        if (one.isEmpty()) {
            DemoEntity demoEntity = new DemoEntity();
            demoEntity.id = UUID.randomUUID().toString();
            demoEntity.name = "test";
            create(demoEntity);
            return demoEntity;
        }
        return one.get();
    }
}
