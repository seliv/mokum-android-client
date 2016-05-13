/*
 * This file is a part of Mokum.place client application for Android.
 * Copyright (C) 2016 Alexey @seliv Selivanov.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        private String platform = "Android App";
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
