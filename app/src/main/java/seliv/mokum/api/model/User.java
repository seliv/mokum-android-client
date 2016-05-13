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

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seliv on 2/7/16.
 */
public class User {
    private long id;
    private String name;
    private String display_name;
    private String status;
    private String description;
    private boolean is_friend;
    private String avatar_url;

    public static User fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, User.class);
    }

    public static Map<Long, User> mapFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<HashMap<Long, User>>() {}.getType());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getStatus() {
        return status;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }
}
