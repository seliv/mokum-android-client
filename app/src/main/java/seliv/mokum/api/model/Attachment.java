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
public class Attachment {
    private String attachment_file_name;
    private long id;
    private int medium_height;
    private String medium_url;
    private int medium_width;
    private String orientation;
    private int original_height;
    private String original_url;
    private int original_width;
    private long post_id;
    private int thumb_height;
    private String thumb_url;
    private int thumb_width;
    private Date updated_at;
    private long user_id;

    public static List<Attachment> listFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<ArrayList<Attachment>>() {}.getType());
    }

    public long getId() {
        return id;
    }

    public int  getMediumHeight() {
        return medium_height;
    }

    public String getMediumUrl() {
        return medium_url;
    }

    public int  getMediumWidth() {
        return medium_width;
    }

    public String getOrientation() {
        return orientation;
    }

    public int getOriginalHeight() {
        return original_height;
    }

    public String getOriginalUrl() {
        return original_url;
    }

    public int  getOriginalWidth() {
        return original_width;
    }

    public long getPostId() {
        return post_id;
    }

    public int getThumbHeight() {
        return thumb_height;
    }

    public String getThumbUrl() {
        return thumb_url;
    }

    public int getThumbWidth() {
        return thumb_width;
    }
}
