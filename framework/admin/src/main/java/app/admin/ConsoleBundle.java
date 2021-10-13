package app.admin;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author chi
 */
public class ConsoleBundle {
    public String name;
    public String path;
    public String scriptFile;
    public List<String> scriptFiles = Lists.newArrayList();
    public List<String> messages = Lists.newArrayList();
    public ConsoleMenu menu;
    public List<ConsoleMenu> extraMenu = Lists.newArrayList();
    public Map<String, Object> options = Maps.newHashMap();
    public Map<String, String> routes = Maps.newHashMap();
}
