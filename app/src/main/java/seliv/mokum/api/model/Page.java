package seliv.mokum.api.model;

import java.util.List;
import java.util.Map;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class Page {
    List<Entry> entries;
    private Map<Long, User> users;
    private Map<Long, Group> groups;
    private String older_entries_url;
    private String newer_entries_url;

    public static Page fromJson(String json) {
        Page page = GsonUtil.getGsonInstance().fromJson(json, Page.class);
        // Basic error validation
        if ((page.entries == null) || (page.users == null)) {
            return null;
        }
        return page;
    }

    public String toJson() {
        return GsonUtil.getGsonInstance().toJson(this);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public Map<Long, Group> getGroups() {
        return groups;
    }

    public String getOlderEntriesUrl() {
        return older_entries_url;
    }

    public String getNewerEntriesUrl() {
        return newer_entries_url;
    }
}
