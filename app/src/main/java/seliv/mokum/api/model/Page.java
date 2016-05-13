/*
 * This file is a part of Mokum.place client application for Android.
 * Copyright (C) 2016 Alexey @seliv Selivanov.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package seliv.mokum.api.model;

import java.util.Collections;
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
        if (groups != null) {
            return groups;
        } else {
            return Collections.emptyMap();
        }
    }

    public String getOlderEntriesUrl() {
        return older_entries_url;
    }

    public String getNewerEntriesUrl() {
        return newer_entries_url;
    }
}
