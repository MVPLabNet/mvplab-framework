package app.admin.impl.web;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author chi
 */
public class ConsoleMenuView {
    public String path;
    public Boolean enabled;
    public String displayName;
    public Integer displayOrder;
    public List<String> rolesAllowed;
    public List<ConsoleMenuItemView> children;

    public static class ConsoleMenuItemView {
        public String path;
        public String displayName;
        public String messageKey;
        public Integer displayOrder = 1;
        public List<String> rolesAllowed = Lists.newArrayList();
    }
}
