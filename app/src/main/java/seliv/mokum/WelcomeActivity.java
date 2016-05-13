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
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String authToken = AuthManager.getAuthToken(this);
        if (authToken != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", authToken);
            startActivity(intent);
        }

        TextView bestOfTextView = (TextView) findViewById(R.id.bestTextView);
        fillLanguageLinks(bestOfTextView, "best_of");
        TextView everythingTextView = (TextView) findViewById(R.id.everythingTextView);
        fillLanguageLinks(everythingTextView, "everything");
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void previewWithoutLogin(String filter, String language) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("token", "");
        String url;
        if (language == null) {
            url = "filter/" + filter + ".json";
        } else {
            url = "filter/" + filter + "/" + language + ".json";
        }
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private static final String[] LANGUAGES = new String[] {"English", "Русский", "Italiano", "Türkçe", "فارسی ", "All"};
    private static final String[] LANGUAGE_CODES = new String[] {"en", "ru", "it", "tr", "fa", null};

    private void fillLanguageLinks(TextView textView, final String filter) {
        String caption = textView.getText().toString();
        SpannableStringBuilder actionsBuilder = new SpannableStringBuilder(caption);
        for (int i = 0; i < LANGUAGES.length; i++) {
            if (i > 0) {
                actionsBuilder.append(", ");
            }
            String language = LANGUAGES[i];
            final String languageCode = LANGUAGE_CODES[i];
            int start = actionsBuilder.length();
            actionsBuilder.append(language);
            int end = actionsBuilder.length();
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
                    previewWithoutLogin(filter, languageCode);
                }
            };
            actionsBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionsBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(actionsBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setClickable(true);
    }
}
