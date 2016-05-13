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

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.WhoAmI;
import seliv.mokum.net.Connection;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView textView = (TextView) findViewById(R.id.TokenTextView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void loginWithToken(View view) {
        EditText tokenEditText = (EditText) findViewById(R.id.TokenEditText);
        String value = tokenEditText.getText().toString();
        AuthManager.saveAuthToken(this, value);

        Connection connection = new Connection(value);
        ServerApi.initConnection(connection);

        new TokenChecker(value).execute();
    }

    public void loginWithSiteCallback(View view) {
        TextView loginProgress = (TextView) findViewById(R.id.loginProgressTextView);
        loginProgress.setText("Once again: the feature is not implemented yet");
    }

    private void startMainView(String value) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("token", value);
        startActivity(intent);
    }

    private class TokenChecker extends AsyncTask<Void, Void, WhoAmI> {
        private final String token;

        public TokenChecker(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            TextView loginProgress = (TextView) findViewById(R.id.loginProgressTextView);
            loginProgress.setText("Validating API Token");
        }

        @Override
        protected WhoAmI doInBackground(Void... params) {
            return ServerApi.askWhoAmI();
        }

        @Override
        protected void onPostExecute(WhoAmI whoAmI) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
            progressBar.setVisibility(View.GONE);
            TextView loginProgress = (TextView) findViewById(R.id.loginProgressTextView);
            if (whoAmI == null) {
                loginProgress.setText("API Token validation failed");
            } else {
                loginProgress.setText("Logged in as " + whoAmI.getUser().getName());
                startMainView(token);
            }
        }
    }
}
