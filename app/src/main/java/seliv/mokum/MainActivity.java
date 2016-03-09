package seliv.mokum;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Entry;
import seliv.mokum.api.model.Page;
import seliv.mokum.api.model.User;
import seliv.mokum.net.Connection;
import seliv.mokum.ui.EntryWidget;
import seliv.mokum.ui.PostWidget;

public class MainActivity extends AppCompatActivity {
    private String currentUrl = "index.json";

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
//        ScrollView scrollView = (ScrollView) findViewById(R.id.mainScrollView);
//        processRequest(scrollView);
        goToUrl("index.json");
    }

    public void goToUrl(String url) {
        currentUrl = url;
        new ContentLoader().execute(url);
    }

    //    private void processRequest(View view) {
//        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////                String url = "https://mokum.place/index.json";
//        int mLongAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
//        view.animate()
//                .alpha(0.7f)
//                .setDuration(mLongAnimationDuration)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
////                        mLoadingView.setVisibility(View.GONE);
//                    }
//                });
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
//
//        VISITED_URLS.push(url);
//        new ContentLoader().execute(url);
//    }
//
    private class ContentLoader extends AsyncTask<String, Void, Page> {
        @Override
        protected Page doInBackground(String... params) {
            String url = params[0];
            return ServerApi.loadPage(url);
        }

        @Override
        protected void onPostExecute(Page page) {
            setContent(page);
        }
    }

    private void setContent(Page page) {
//        ScrollView scrollView = (ScrollView) findViewById(R.id.mainScrollView);
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
}
