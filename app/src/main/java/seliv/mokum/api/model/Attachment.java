package seliv.mokum.api.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Attachment {
    private String attachment_file_name;
    private long id;
    private long medium_height;
    private String medium_url;
    private long medium_width;
    private String orientation;
    private long original_height;
    private String original_url;
    private long original_width;
    private long post_id;
    private long thumb_height;
    private String thumb_url;
    private long thumb_width;
    private Date updated_at;
    private long user_id;

    public static List<Attachment> listFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<ArrayList<Attachment>>() {}.getType());
    }

    public long getId() {
        return id;
    }

    public String getOrientation() {
        return orientation;
    }

    public long getOriginalHeight() {
        return original_height;
    }

    public String getOriginalUrl() {
        return original_url;
    }

    public long getOriginalWidth() {
        return original_width;
    }

    public long getPostId() {
        return post_id;
    }

    public long getThumbHeight() {
        return thumb_height;
    }

    public String getThumbUrl() {
        return thumb_url;
    }

    public long getThumbWidth() {
        return thumb_width;
    }
}
