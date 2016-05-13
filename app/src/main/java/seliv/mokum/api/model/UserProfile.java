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
 * A synthetic entity that doesn't reflect any Mokum JSON entity directly and holds general information
 * related to currently authenticated user. This information is supposed to be "almost static", i.e.
 * unlikely to be changed while the application is actively used.
 *
 * Created by aselivanov on 4/21/2016.
 */
public class UserProfile {
    private final WhoAmI whoAmI;
    private final Subscriptions subscriptions;

    public UserProfile(WhoAmI whoAmI, Subscriptions subscriptions) {
        this.whoAmI = whoAmI;
        this.subscriptions = subscriptions;
    }

    public static UserProfile fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, UserProfile.class);
    }

    public String toJson() {
        return GsonUtil.getGsonInstance().toJson(this);
    }

    public WhoAmI getWhoAmI() {
        return whoAmI;
    }

    public Subscriptions getSubscriptions() {
        return subscriptions;
    }
}
