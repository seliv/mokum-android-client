package seliv.mokum.api.model;

/**
 * Created by aselivanov on 3/7/2016.
 */
public class LikeResult {
    public static final String RESULT_LIKED = "liked";
    public static final String RESULT_UNLIKED = "unliked";

    private String result;

    public static LikeResult fromJson(String json) {
        return GsonUtil.getGsonInstance().fromJson(json, LikeResult.class);
    }

    public String getResult() {
        return result;
    }
}
