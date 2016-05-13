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
