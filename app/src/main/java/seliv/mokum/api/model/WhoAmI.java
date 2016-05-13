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

/**
 * Created by aselivanov on 3/15/2016.
 */
public class WhoAmI {
    private User user;

    public static WhoAmI fromJson(String json) {
        WhoAmI whoAmI = GsonUtil.getGsonInstance().fromJson(json, WhoAmI.class);
        if (whoAmI.getUser() == null) {
            return null;
        }
        return whoAmI;
    }

    public User getUser() {
        return user;
    }
}
