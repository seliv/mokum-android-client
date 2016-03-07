package seliv.mokum.api.model;

import java.util.List;
import java.util.Map;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class Comments {
    private List<Comment> comments;
    private int comments_count;
    private int gap_position;
//    private groups;
//    private river;
    private Map<Long, User> users;

    public static Comments fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Comments.class);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
