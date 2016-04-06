package seliv.mokum.api.model;

/**
 * Created by aselivanov on 4/5/2016.
 */
public class Group {
    private long id; // ?
    private String description;
    private String display_name;
    private String name;
    private boolean nsfw;
    private String status;
    private String url;

    public String getDisplayName() {
        return display_name;
    }

    public String getUrl() {
        return url;
    }
}
