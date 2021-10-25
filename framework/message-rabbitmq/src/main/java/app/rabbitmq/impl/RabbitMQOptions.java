package app.rabbitmq.impl;


import jakarta.validation.constraints.NotNull;

/**
 * @author chi
 */
public class RabbitMQOptions {
    @NotNull
    public String host;
    @NotNull
    public Integer port;
    @NotNull
    public String username;
    @NotNull
    public String password;
    @NotNull
    public String virtualHost;
}
