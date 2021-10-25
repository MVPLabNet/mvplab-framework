package app.demo.web;

import app.demo.web.user.RegisterUserResponse;

import java.util.Optional;

/**
 * @author chi
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public Optional<RegisterUserResponse> get() {
        return Optional.empty();
    }
}
