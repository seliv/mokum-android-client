package seliv.mokum.api.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Comment {
    private Date created_at;
    private long id;
    private long post_id;
    private String text;
    private Date updated_at;
    private long user_id;

    public static Comment fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Comment.class);
    }

    public static List<Comment> listFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<ArrayList<Comment>>() {}.getType());
    }

    public String getText() {
        return text;
    }

    public long getUserId() {
        return user_id;
    }
}
