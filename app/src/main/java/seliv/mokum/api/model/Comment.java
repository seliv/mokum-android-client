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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Comment {
    private Date created_at;
    private long id;
    private long post_id;
    private String text;
    private String text_as_html;
    private Date updated_at;
    private long user_id;

    public static Comment fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Comment.class);
    }

    public static List<Comment> listFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<ArrayList<Comment>>() {}.getType());
    }

    public String getText() {
        if (text_as_html != null) {
            return text_as_html;
        } else {
            return text;
        }
    }

    public long getUserId() {
        return user_id;
    }
}
