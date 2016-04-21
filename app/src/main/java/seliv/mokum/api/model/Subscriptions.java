package seliv.mokum.api.model;

import java.util.List;

/**
 * Created by aselivanov on 4/21/2016.
 */
public class Subscriptions {
    private List<GroupSubscription> group_subscriptions;
    private List<UserSubscription> subscriptions;

    public static User fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, User.class);
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
