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
 * Created by aselivanov on 4/21/2016.
 */
public class Subscriptions {
    private List<GroupSubscription> group_subscriptions;
    private List<UserSubscription> subscriptions;

    public static Subscriptions fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Subscriptions.class);
    }

    public List<GroupSubscription> getGroupSubscriptions() {
        return group_subscriptions;
    }

    public class GroupSubscription {
        private Group group;

        public Group getGroup() {
            return group;
        }
    }

    public class UserSubscription {
        private User user;
        private boolean mutual;

        public User getUser() {
            return user;
        }

        public boolean isMutual() {
            return mutual;
        }
    }
}
