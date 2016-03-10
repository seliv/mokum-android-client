package seliv.mokum;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Entry;
import seliv.mokum.api.model.Page;
import seliv.mokum.api.model.User;
import seliv.mokum.net.Connection;
import seliv.mokum.ui.EntryWidget;
import seliv.mokum.ui.NavigationWidget;
import seliv.mokum.ui.PostWidget;

public class MainActivity extends AppCompatActivity {
    private final Stack<String> visitedUrls = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String token = getIntent().getStringExtra("token");
        Connection connection = new Connection(token);
        ServerApi.initConnection(connection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        goToUrl("index.json");
    }

    public void goToUrl(String url) {
        visitedUrls.push(url);
        new ContentLoader().execute(url);
    }

    private class ContentLoader extends AsyncTask<String, Void, Page> {
        @Override
        protected void onPreExecute() {
            int mLongAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
            View view = findViewById(R.id.scrollContentLayout);
            view.animate()
                    .alpha(0.7f)
                    .setDuration(mLongAnimationDuration);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Page doInBackground(String... params) {
            String url = params[0];
            return ServerApi.loadPage(url);
        }

        @Override
        protected void onPostExecute(Page page) {
            View view = findViewById(R.id.scrollContentLayout);
            int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            view.animate()
                    .alpha(1.0f)
                    .setDuration(mShortAnimationDuration);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
            progressBar.setVisibility(View.GONE);
            setContent(page);
        }
    }

    private void setContent(Page page) {
        if (page == null) {
//            Snackbar.make(view, "No JSON returned", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            LinearLayout contentLayout = (LinearLayout) findViewById(R.id.scrollContentLayout);
            TextView textView = new TextView(contentLayout.getContext());
            textView.setText("No JSON returned");
            contentLayout.addView(textView);
        } else {
//                Snackbar.make(view, "JSON updated", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            List<Entry> entires = page.getEntries();
            Map<Long, User> users = page.getUsers();

            LinearLayout contentLayout = (LinearLayout) findViewById(R.id.scrollContentLayout);
            contentLayout.removeAllViews();
            if (entires.size() > 0) {
                NavigationWidget navigationWidget = new NavigationWidget(contentLayout.getContext());
                navigationWidget.setUrls(page.getOlderEntriesUrl(), page.getNewerEntriesUrl());
                contentLayout.addView(navigationWidget);
                for (Entry entry : entires) {
                    EntryWidget entryWidget = new EntryWidget(contentLayout.getContext());
                    entryWidget.setEntry(users, entry);
                    contentLayout.addView(entryWidget);
                }
            } else {
                TextView textView = new TextView(contentLayout.getContext());
                textView.setText("This feed has no entries (can be a private feed)");
                contentLayout.addView(textView);
            }
            contentLayout.addView(new PostWidget(contentLayout.getContext()));
        }
    }

    @Override
    public void onBackPressed() {
        if (!visitedUrls.isEmpty()) {
            visitedUrls.pop();
        }
        if (visitedUrls.isEmpty()) {
            super.onBackPressed();
        } else {
            goToUrl(visitedUrls.pop());
        }
    }
}
