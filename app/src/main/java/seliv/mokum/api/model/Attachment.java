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
    private int medium_height;
    private String medium_url;
    private int medium_width;
    private String orientation;
    private int original_height;
    private String original_url;
    private int original_width;
    private long post_id;
    private int thumb_height;
    private String thumb_url;
    private int thumb_width;
    private Date updated_at;
    private long user_id;

    public static List<Attachment> listFromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, new TypeToken<ArrayList<Attachment>>() {}.getType());
    }

    public long getId() {
        return id;
    }

    public int  getMediumHeight() {
        return medium_height;
    }

    public String getMediumUrl() {
        return medium_url;
    }

    public int  getMediumWidth() {
        return medium_width;
    }

    public String getOrientation() {
        return orientation;
    }

    public int getOriginalHeight() {
        return original_height;
    }

    public String getOriginalUrl() {
        return original_url;
    }

    public int  getOriginalWidth() {
        return original_width;
    }

    public long getPostId() {
        return post_id;
    }

    public int getThumbHeight() {
        return thumb_height;
    }

    public String getThumbUrl() {
        return thumb_url;
    }

    public int getThumbWidth() {
        return thumb_width;
    }
}
