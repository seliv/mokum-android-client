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
public class Comments {
    private List<Comment> comments;
    private int comments_count;
    private int gap_position;
//    private groups;
//    private river;
    private Map<Long, User> users;

    public static Comments fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Comments.class);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
