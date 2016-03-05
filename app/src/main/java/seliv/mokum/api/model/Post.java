package seliv.mokum.api.model;

import java.util.Date;

/**
 * Created by aselivanov on 2/11/2016.
 */
public class Post {
    private boolean comments_disabled;
    private long comments_version;
    private Date created_at;
    private Date fresh_at;
    private long id;
    private boolean nsfw;
    private Date published_at;
    private String text;
    private Date updated_at;
    private long user_id;
    private long version;

    public static Post fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Post.class);
    }

    public String getText() {
        return text;
    }
}
