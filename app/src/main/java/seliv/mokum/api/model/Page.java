package seliv.mokum.api.model;

import java.util.List;
import java.util.Map;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class Page {
    List<Entry> entries;
    private Map<Long, User> users;
    private String older_entries_url;
    private String newer_entries_url;

    public static Page fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Page.class);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public String getOlderEntriesUrl() {
        return older_entries_url;
    }

    public String getNewerEntriesUrl() {
        return newer_entries_url;
    }
}
