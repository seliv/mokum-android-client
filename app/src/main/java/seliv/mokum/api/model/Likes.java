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

import java.util.List;
import java.util.Map;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class Likes {
    private boolean can_fav;
    private boolean can_like;
    private int fav_count;
//    private groups;
    private List<Long> likes;
    private int more_likes;
    private Map<Long, User> users;

    public static Likes fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Likes.class);
    }

    public List<Long> getLikes() {
        return likes;
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
