package seliv.mokum.api.model;

import java.util.List;
import java.util.Map;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class Likes {
    private boolean can_fav;
    private boolean can_like;
    private int fav_count;
//    private groups;
    private List<Long> likes;
    private int more_likes;
    private Map<Long, User> users;

    public static Likes fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Likes.class);
    }

    public List<Long> getLikes() {
        return likes;
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
