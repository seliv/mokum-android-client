package seliv.mokum.api.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by aselivanov on 2/11/2016.
 */
public class PostRequest {
    private Body post;
    private String _uuid;

    private PostRequest(Body post, String _uuid) {
        this.post = post;
        this._uuid = _uuid;
    }

    public static PostRequest create(String text, boolean commentsDisabled) {
        Body postBody = new Body(Collections.singletonList("user"), text, commentsDisabled, false);
        return new PostRequest(postBody, UUID.randomUUID().toString());
    }

    public String toJson() {
        return GsonUtil.getGsonInstance().toJson(this);
    }

    public static class Body {
        private List<String> timelines;
        private String text;
        private boolean comments_disabled;
        private boolean nsfw;

        public Body(List<String> timelines, String text, boolean comments_disabled, boolean nsfw) {
            this.timelines = timelines;
            this.text = text;
            this.comments_disabled = comments_disabled;
            this.nsfw = nsfw;
        }
    }
}
