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

package seliv.mokum.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import seliv.mokum.api.model.Comment;
import seliv.mokum.api.model.CommentRequest;
import seliv.mokum.api.model.Comments;
import seliv.mokum.api.model.LikeResult;
import seliv.mokum.api.model.Likes;
import seliv.mokum.api.model.Page;
import seliv.mokum.api.model.Post;
import seliv.mokum.api.model.PostRequest;
import seliv.mokum.api.model.Subscriptions;
import seliv.mokum.api.model.WhoAmI;
import seliv.mokum.net.Connection;
import seliv.mokum.net.JsonWithCode;

/**
 * Created by aselivanov on 3/5/2016.
 */
public class ServerApi {
    private static final String ROOT_URL = "https://mokum.place";
    private static final String API_URL = ROOT_URL + "/api/v1";

    private static final String COMMENT_URL = API_URL + "/posts/%d/comments.json";
    private static final String POST_URL = API_URL + "/posts.json";
    private static final String LIKE_URL = API_URL + "/posts/%d/likes";

    private static final String PAGE_URL = ROOT_URL + "/%s";
    private static final String COMMENTS_URL = ROOT_URL + "/%s/comments.json";
    private static final String LIKES_URL = ROOT_URL + "%s/likes.json";

    private static final String WHO_AM_I_URL = API_URL + "/whoami.json";
    private static final String SUBSCRIPTIONS_URL = ROOT_URL + "/%s/subscriptions.json";

    private static final String ATTACHMENTS_URL = API_URL + "/attachments.json";

    private static Connection connection;

    @Deprecated
    public static void initConnection(Connection connection) {
        ServerApi.connection = connection;
    }

    public static Comment sendComment(long postId, CommentRequest commentRequest) {
        String url = String.format(COMMENT_URL, postId);
        try {
            JsonWithCode jsonWithCode = connection.doPost(url, new JSONObject(commentRequest.toJson()));
            return Comment.fromJson(jsonWithCode.getJson().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Post sendPost(PostRequest postRequest) {
        try {
            JsonWithCode jsonWithCode = connection.doPost(POST_URL, new JSONObject(postRequest.toJson()));
            JSONObject json = jsonWithCode.getJson();
            JSONObject postJson = json.getJSONObject("post");
            return Post.fromJson(postJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LikeResult sendLike(long postId, boolean like) {
        String url = String.format(LIKE_URL, postId);
        JsonWithCode jsonWithCode;
        try {
            if (like) {
                jsonWithCode = connection.doPost(url);
            } else {
                jsonWithCode = connection.doDelete(url);
            }
            JSONObject json = jsonWithCode.getJson();
            return LikeResult.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Page loadPage(String pageUrl) {
        String url = String.format(PAGE_URL, pageUrl);
        try {
            JsonWithCode jsonWithCode = connection.doGet(url);
            JSONObject json = jsonWithCode.getJson(); // TODO: Handle null here
            return Page.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Comments loadComments(String entryUrl) {
        String url = String.format(COMMENTS_URL, entryUrl);
        try {
            JsonWithCode jsonWithCode = connection.doGet(url);
            JSONObject json = jsonWithCode.getJson();
            return Comments.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Likes loadLikes(String entryUrl) {
        String url = String.format(LIKES_URL, entryUrl);
        try {
            JsonWithCode jsonWithCode = connection.doGet(url);
            JSONObject json = jsonWithCode.getJson();
            return Likes.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WhoAmI askWhoAmI() {
        String url = String.format(WHO_AM_I_URL);
        try {
            JsonWithCode jsonWithCode = connection.doGet(url);
            JSONObject json = jsonWithCode.getJson();
            return WhoAmI.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Subscriptions loadSubscriptions(String userName) {
        String url = String.format(SUBSCRIPTIONS_URL, userName);
        try {
            JsonWithCode jsonWithCode = connection.doGet(url);
            JSONObject json = jsonWithCode.getJson();
            return Subscriptions.fromJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
