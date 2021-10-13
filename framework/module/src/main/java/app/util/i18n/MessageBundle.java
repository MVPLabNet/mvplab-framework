package app.util.i18n;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public interface MessageBundle {
    Optional<String> get(String key);

    Optional<String> get(String key, String language);

    List<String> keys();
}