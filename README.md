# MVPLab Framework

A developer friendly module based JAX-RS micro service framework. 

- **Built for startup, provides well designed quick prototyping code base.**
- Supports template editing, perfect for building landing pages.
- JAX-RS module framework
  * Enhancements to Jersey
  * Supports Guice style DI/AOP 
  * Embedded HTTP server with undertow

## Getting Started

These instructions will get you a copy of the JWeb CMS up and running on your local machine.<br>

### Prerequisites

1. Download and install [Open JDK 11](http://jdk.java.net/11/) or [Oracle JDK 11](http://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-4416644.html)
2. If you want to use MySQL as database. (***Optional, JWeb default embeds HSQL***)
   1. Download and install [MySQL](https://dev.mysql.com/downloads/mysql/). 
   2. Create a database. <br>
   ```CREATE DATABASE main CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;```
   3. Create a database user with schema update permission. <br>
   ```   CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';   GRANT ALL PRIVILEGES ON * . * TO 'user'@'localhost';   FLUSH PRIVILEGES;   ```

## Run Source Code

1. Clone the repo
2. Import as a Gradle project to Intellij IDEA or Eclipse.
3. Run main/src/java/Main.java

## Code Examples

To start an `App`: 

```
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Path dir = Paths.get(System.getProperty("user.home")).resolve(".JWeb");
        App app = new UndertowApp(dir);
        ServiceLoader.load(AbstractModule.class).forEach(app::install);
        app.start();
    }
}
```

To create a `Module`:
```

public class TodoServiceModuleImpl extends TodoServiceModule {
    @Override
    protected void configure() {
        //import DatabaseModule to register entity and create repository
        module(DatabaseModule.class)
            .entity(Task.class);
        
        bind(TaskService.class);
        
        //register service implementation
        api().service(TaskWebService.class, TaskWebServiceImpl.class);
    }
}
```


## License

This project is licensed under the AGPL License - see the [LICENSE.md](LICENSE.md) file for details
For commerce license, please contact ``chi#mvplab.net``

