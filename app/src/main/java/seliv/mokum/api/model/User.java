package seliv.mokum.api.model;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seliv on 2/7/16.
 */
public class User {
    private long id;
    private String name;
    private String display_name;
    private String status;
    private String description;
    private boolean is_friend;
    private String avatar_url;

    public static User fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, User.class);
    }

    public static Map<Long, User> mapFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<HashMap<Long, User>>() {}.getType());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }
}
