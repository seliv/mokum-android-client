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
