package seliv.mokum.api.model;

import java.util.UUID;

/**
 * Created by aselivanov on 2/12/2016.
 */
public class CommentRequest {
    private Body comment;
    private String _uuid;

    private CommentRequest(Body comment, String _uuid) {
        this.comment = comment;
        this._uuid = _uuid;
    }

    public static CommentRequest create(String text) {
        Body commentBody = new Body(text);
        return new CommentRequest(commentBody, UUID.randomUUID().toString());
    }

    public String toJson() {
        return GsonUtil.getGsonInstance().toJson(this);
    }

    public static class Body {
        private String text;

        public Body(String text) {
            this.text = text;
        }
    };
}
