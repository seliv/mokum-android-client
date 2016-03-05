package seliv.mokum.net;

import org.json.JSONObject;

/**
 * Created by aselivanov on 3/5/2016.
 */
public class JsonWithCode {
    private final JSONObject json;
    private final int httpResponseCode;

    public JsonWithCode(JSONObject json, int httpResponseCode) {
        this.json = json;
        this.httpResponseCode = httpResponseCode;
    }

    public JSONObject getJson() {
        return json;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }
}
