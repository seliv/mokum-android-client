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

import java.util.Date;

/**
 * Created by aselivanov on 2/11/2016.
 */
public class Post {
    private boolean comments_disabled;
    private long comments_version;
    private Date created_at;
    private Date fresh_at;
    private long id;
    private boolean nsfw;
    private Date published_at;
    private String text;
    private Date updated_at;
    private long user_id;
    private long version;

    public static Post fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Post.class);
    }

    public String getText() {
        return text;
    }
}
