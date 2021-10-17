package app.database;

import app.AbstractModule;
import app.Binder;
import app.Configurable;
import app.Environment;
import app.database.impl.DatabaseConfigImpl;
import app.database.impl.DatabaseImpl;
import app.database.impl.TransactionalInterceptor;
import app.util.exception.Errors;
import com.google.common.collect.ImmutableList;

import jakarta.transaction.Transactional;
import java.nio.file.Path;
import java.util.List;

/**
 * @author chi
 */
public class DatabaseModule extends AbstractModule implements Configurable<DatabaseConfig> {
    private DatabaseImpl database;

    @Override
    protected void configure() {
        DatabaseOptions options = options("database", DatabaseOptions.class);
        if (Boolean.TRUE.equals(options.createTableEnabled) && app().env() != Environment.DEV) {
            throw Errors.internalError("PROD env doesn't allow to auto create tables");
        }
        options.url = resetBaseDir(options.url, app().dataDir().resolve("database"));
        this.database = new DatabaseImpl(options);
        this.database.setDir(app().dataDir());
        bind(Database.class).toInstance(this.database);
        bindInterceptor(Transactional.class, new TransactionalInterceptor(this.database));
        onStartup(database::start);
    }

    @Override
    public List<String> declareRoles() {
        return ImmutableList.of();
    }

    @Override
    public DatabaseConfig configurator(AbstractModule module, Binder binder) {
        return new DatabaseConfigImpl(binder, database);
    }

    private String resetBaseDir(String jdbcURL, Path databaseDir) {
        if (jdbcURL != null) {
            return jdbcURL.replace(":./", ':' + databaseDir.toString() + '/');
        }
        return null;
    }
}
