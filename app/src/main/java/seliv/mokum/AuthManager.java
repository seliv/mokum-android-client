package seliv.mokum;

//import android.accounts.Account;
//import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aselivanov on 3/11/2016.
 */
public class AuthManager {
    private static final String MOKUM_ACCOUNT_TYPE = "place.mokum.auth";
    private static final String MOKUM_AUTH_TYPE = "place.mokum.auth.token";

    public static String getAuthToken(Context context) {
//        AccountManager am = AccountManager.get(context);
//        Account[] accounts = am.getAccountsByType(MOKUM_ACCOUNT_TYPE);
//        if (accounts.length > 0) {
//            for (Account account : accounts) {
//                String authToken = am.peekAuthToken(account, MOKUM_AUTH_TYPE);
//                if (authToken != null) {
//                    return authToken;
//                }
//            }
//        }
//        return null;

        SharedPreferences sharedPreferences = context.getSharedPreferences(MOKUM_ACCOUNT_TYPE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(MOKUM_AUTH_TYPE, null);
    }

    public static void saveAuthToken(Context context, String token) {
//        AccountManager am = AccountManager.get(context);
//        Account[] accounts = am.getAccountsByType(MOKUM_ACCOUNT_TYPE);
//        if (accounts.length > 0) {
//            for (Account account : accounts) {
//                am.setAuthToken(account, MOKUM_AUTH_TYPE, token);
//            }
//        } else {
//            Account account = new Account("default_name", MOKUM_ACCOUNT_TYPE);
//            am.addAccountExplicitly(account, null, null); // Result ignored
//            am.setAuthToken(account, MOKUM_AUTH_TYPE, token);
//        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MOKUM_ACCOUNT_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOKUM_AUTH_TYPE, token);
        editor.apply();
    }
}
