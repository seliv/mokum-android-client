package seliv.mokum.api.model;

import java.util.Date;
import java.util.List;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class Entry {
    private List<Attachment> attachments;
    private boolean can_comment;
    private boolean can_fav;
    private boolean can_hide;
    private boolean can_like;
    private List<Comment> comments;

    private int comments_count;
    private List<Embed> embeds;

    private int fav_count;
    private Date fresh_at;
    private int gap_position;
    private long id;
    private boolean is_public;

    private List<Long> likes;
    private int more_likes;
    private boolean nsfw;
    private Date published_at;
    private Reason reason;

    private String text;
    private String text_as_html;
    private String url;

    private long user_id;
    private boolean user_liked;
    private long version;

    public static Entry fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, Entry.class);
    }


    public long getId() {
        return id;
    }

    public long getUserId() {
        return user_id;
    }

    public String getText() {
        if (text_as_html != null) {
            return text_as_html;
        } else {
            return text;
        }
    }

    public Date getPublishedAt() {
        return published_at;
    }

    public Reason getReason() {
        return reason;
    }

    public List<Long> getLikes() {
        return likes;
    }

    public int getMoreLikes() {
        return more_likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getCommentsCount() {
        return comments_count;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUserLiked() {
        return user_liked;
    }

    public boolean isCanComment() {
        return can_comment;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public boolean isPublic() {
        return is_public;
    }
}
