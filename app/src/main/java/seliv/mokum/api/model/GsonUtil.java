package seliv.mokum.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by aselivanov on 3/5/2016.
 */
public class GsonUtil {
    public static Gson getGsonInstance() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    }
}
