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

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Reason {
    private List<Long> user_likes;
    private List<Long> user_comments;
    private List<Long> user_private;
    private List<Long> group;
    private List<Long> user;

    public List<Long> getUserLikes() {
        return user_likes;
    }

    public List<Long> getUserComments() {
        return user_comments;
    }

    public List<Long> getUserPrivate() {
        return user_private;
    }

    public List<Long> getGroup() {
        return group;
    }

    public List<Long> getUser() {
        return user;
    }
}
