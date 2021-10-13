package app.admin.impl.service;

import app.admin.ConsoleBundle;
import app.admin.ConsoleMenu;
import app.util.exception.Errors;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class Console {
    private final Map<String, ConsoleBundle> consoleModules = Maps.newHashMap();
    private final Map<String, ConsoleBundle> scriptFiles = Maps.newHashMap();
    private String defaultPath = "/admin/";

    public Console install(ConsoleBundle consoleBundle) {
        consoleModules.put(consoleBundle.name, consoleBundle);
        if (!Strings.isNullOrEmpty(consoleBundle.scriptFile)) {
            scriptFiles.put(consoleBundle.scriptFile, consoleBundle);
        }
        return this;
    }

    public ConsoleBundle bundle(String bundleName) {
        ConsoleBundle consoleBundle = consoleModules.get(bundleName);
        if (consoleBundle == null) {
            throw Errors.internalError("missing console bundle, name={}", bundleName);
        }
        return consoleBundle;
    }

    public Console setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
        return this;
    }

    public String defaultPath() {
        return this.defaultPath;
    }

    public Optional<ConsoleBundle> findByScriptFile(String scriptFile) {
        return Optional.ofNullable(scriptFiles.get(scriptFile));
    }

    public List<ConsoleBundle> bundles() {
        return ImmutableList.copyOf(consoleModules.values());
    }

    public List<ConsoleMenu> menus() {
        List<ConsoleMenu> menus = Lists.newArrayList();
        for (ConsoleBundle bundle : bundles()) {
            if (bundle.menu != null) {
                menus.add(bundle.menu);
            }
        }
        return Ordering.from(Comparator.<ConsoleMenu>comparingInt(o -> o.displayOrder)).sortedCopy(menus);
    }
}
