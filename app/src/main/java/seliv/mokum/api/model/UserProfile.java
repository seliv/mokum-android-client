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
