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
