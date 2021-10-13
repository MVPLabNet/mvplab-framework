package app.admin;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author chi
 */
public class ConsoleMenu {
    public String path;
    public String displayName;
    public Boolean enabled = true;
    public String messageKey;
    public Integer displayOrder = 1;
    public List<String> rolesAllowed = Lists.newArrayList();
    public List<Item> children;

    public static class Item {
        public String path;
        public String displayName;
        public String messageKey;
        public Integer displayOrder = 1;
        public List<String> rolesAllowed = Lists.newArrayList();
    }
}
