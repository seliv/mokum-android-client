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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Entry;
import seliv.mokum.api.model.Group;
import seliv.mokum.api.model.Page;
import seliv.mokum.api.model.Subscriptions;
import seliv.mokum.api.model.User;
import seliv.mokum.api.model.UserProfile;
import seliv.mokum.api.model.WhoAmI;
import seliv.mokum.net.Connection;
import seliv.mokum.ui.EntryWidget;
import seliv.mokum.ui.NavigationWidget;
import seliv.mokum.ui.PostWidget;

public class MainActivity extends AppCompatActivity {
    private static final String BUNDLE_STATE_VISITED_URLS = "visitedUrls";
    private static final String BUNDLE_STATE_CURRENT_PAGE = "currentPage";
    private static final String BUNDLE_STATE_USER_PROFILE = "userProfile";

    private Stack<String> visitedUrls = new Stack<>();
    private Page currentPage;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String token = getIntent().getStringExtra("token");
        Connection connection = new Connection(token);
        ServerApi.initConnection(connection);

        if (savedInstanceState != null) {
            String currentPageState = savedInstanceState.getString(BUNDLE_STATE_CURRENT_PAGE);
            if (currentPageState != null) {
                Page page = Page.fromJson(currentPageState);
                setContent(page);
            }
            String[] visitedUrlsState = savedInstanceState.getStringArray(BUNDLE_STATE_VISITED_URLS);
            if (visitedUrlsState != null) {
                visitedUrls = new Stack<>();
                for (String url : visitedUrlsState) {
                    visitedUrls.push(url);
                }
            }
            String userProfileState = savedInstanceState.getString(BUNDLE_STATE_USER_PROFILE);
            if (userProfileState != null) {
                userProfile = UserProfile.fromJson(userProfileState);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            goToUrl(url);
        } else {
            if (currentPage != null) {
                setContent(currentPage);
            } else {
                if (!visitedUrls.empty()) {
                    String lastUrl = visitedUrls.pop();
                    goToUrl(lastUrl);
                } else {
                    goToUrl("index.json");
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String[] visitedUrlsState = visitedUrls.toArray(new String[visitedUrls.size()]);
        outState.putStringArray(BUNDLE_STATE_VISITED_URLS, visitedUrlsState);
        if (currentPage != null) {
            String currentPageState = currentPage.toJson();
            outState.putString(BUNDLE_STATE_CURRENT_PAGE, currentPageState);
        }
        if (userProfile != null) {
            String userProfileState = userProfile.toJson();
            outState.putString(BUNDLE_STATE_USER_PROFILE, userProfileState);
        }
        super.onSaveInstanceState(outState);
    }

    public void goToUrl(String url) {
        visitedUrls.push(url);
        new ContentLoader().execute(url);
    }

    public void showImageGallery(final String[] thumbUrls, final String[] imageUrls, int current) {
        List<MediaInfo> infos = new ArrayList<>(imageUrls.length);
        for (int i = 0; i < thumbUrls.length; i++) {
            final int index = i;
            MediaInfo info = MediaInfo.mediaLoader(new MediaLoader() {
                @Override
                public boolean isImage() {
                    return true;
                }

                @Override
                public void loadMedia(Context context, ImageView imageView, SuccessCallback callback) {
                    Picasso.with(MainActivity.this).
                            load(imageUrls[index]).
                            into(imageView);
                    callback.onSuccess();
                }

                @Override
                public void loadThumbnail(Context context, ImageView thumbnailView, SuccessCallback callback) {
                    Picasso.with(MainActivity.this).
                            load(thumbUrls[index]).
                            into(thumbnailView);
                    callback.onSuccess();
                }
            });
            infos.add(info);
        }

        ScrollGalleryView scrollGalleryView = (ScrollGalleryView) findViewById(R.id.galleryView);
        try {
            Field imageListField = scrollGalleryView.getClass().getDeclaredField("mListOfMedia");
            imageListField.setAccessible(true);
            List<MediaInfo> imageList = (List<MediaInfo>) imageListField.get(scrollGalleryView);
            imageList.clear();

            Field thumbnailsContainerField = scrollGalleryView.getClass().getDeclaredField("thumbnailsContainer");
            thumbnailsContainerField.setAccessible(true);
            LinearLayout thumbnailsContainer = (LinearLayout) thumbnailsContainerField.get(scrollGalleryView);
            thumbnailsContainer.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scrollGalleryView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.scrollContentLayout);
        contentLayout.setVisibility(View.INVISIBLE);
        scrollGalleryView.setVisibility(View.VISIBLE);

        scrollGalleryView.setCurrentItem(current);
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
            setContent(page);
        }
    }

    private void setContent(Page page) {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.scrollContentLayout);
        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        contentLayout.animate()
                .alpha(1.0f)
                .setDuration(mShortAnimationDuration);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        progressBar.setVisibility(View.GONE);

        currentPage = page;
        if (page == null) {
//            Snackbar.make(view, "No JSON returned", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            TextView textView = new TextView(contentLayout.getContext());
            textView.setText("No JSON returned");
            contentLayout.removeAllViews();
            contentLayout.addView(textView);
        } else {
//                Snackbar.make(view, "JSON updated", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            List<Entry> entires = page.getEntries();
            Map<Long, User> users = page.getUsers();
            Map<Long, Group> groups = page.getGroups();

            contentLayout.removeAllViews();
            Menu navigationMenu;
            NavigationWidget navigationWidget;
            if (entires.size() > 0) {
                navigationWidget = new NavigationWidget(contentLayout.getContext());
                navigationWidget.setUrls(page.getOlderEntriesUrl(), page.getNewerEntriesUrl());
                MenuInflater inflater = getMenuInflater();
                navigationMenu = navigationWidget.getMenu().getMenu();
                inflater.inflate(R.menu.menu_main, navigationMenu);
                updateHistoryMenuItem(navigationMenu);
                updateGroupsMenuItem(navigationMenu);
                if (userProfile != null) {
                    navigationWidget.setGroups(userProfile.getSubscribedGroups());
                }
                navigationWidget.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return handleMenuItem(item);
                    }
                });
                contentLayout.addView(navigationWidget);
                for (Entry entry : entires) {
                    EntryWidget entryWidget = new EntryWidget(contentLayout.getContext());
                    entryWidget.setEntry(users, groups, entry);
                    contentLayout.addView(entryWidget);
                }
                NavigationWidget bottomNavigationWidget = new NavigationWidget(contentLayout.getContext());
                bottomNavigationWidget.setUrls(page.getOlderEntriesUrl(), page.getNewerEntriesUrl());
                bottomNavigationWidget.setNavigationOnly(true);
                contentLayout.addView(bottomNavigationWidget);
            } else {
                TextView textView = new TextView(contentLayout.getContext());
                textView.setText("This feed has no entries (possibly a private feed)");
                contentLayout.removeAllViews();
                contentLayout.addView(textView);
                navigationMenu = null;
                navigationWidget = null;
            }
            // Let's try to load subscriptions in background while the user is busy viewing some content now
            if (userProfile == null) {
                new UserProfileLoader(navigationWidget, navigationMenu).execute();
            }
        }
    }

    @Override
    public void onBackPressed() {
        ScrollGalleryView scrollGalleryView = (ScrollGalleryView) findViewById(R.id.galleryView);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.scrollContentLayout);
        if (View.VISIBLE == scrollGalleryView.getVisibility()) {
            contentLayout.setVisibility(View.VISIBLE);
            scrollGalleryView.setVisibility(View.GONE);
            return;
        }
        if (!visitedUrls.isEmpty()) {
            visitedUrls.pop();
        }
        if (visitedUrls.isEmpty()) {
            super.onBackPressed();
        } else {
            goToUrl(visitedUrls.pop());
        }
    }

    private boolean handleMenuItem(MenuItem item) {
        if ((item.getItemId() >= GROUP_MENU_BASE_ID) && userProfile != null) {
            int i = item.getItemId() - GROUP_MENU_BASE_ID;
            if ((i >= 0) && (i < userProfile.getSubscriptions().getGroupSubscriptions().size() - 1)) {
//                String url = userProfile.getSubscriptions().getGroupSubscriptions().get(i).getGroup().getUrl() + ".json";
                // TODO: This is a workaround since Group entries in subscriptions have no URL initialized
                String url = "/" + userProfile.getSubscriptions().getGroupSubscriptions().get(i).getGroup().getName() + ".json";
                goToUrl(url);
                return true;
            }
        }
        if (item.getItemId() >= HISTORY_MENU_BASE_ID) {
            int i = item.getItemId() - HISTORY_MENU_BASE_ID;
            if ((i >= 0) && (i < visitedUrls.size() - 1)) {
                String url = visitedUrls.get(visitedUrls.size() - 2 - i);
                goToUrl(url);
                return true;
            }
        }
        switch (item.getItemId()) {
            case R.id.menu_reload:
                if (!visitedUrls.empty()) {
                    String lastUrl = visitedUrls.pop();
                    goToUrl(lastUrl);
                    return true;
                } else {
                    return false;
                }
            case R.id.menu_home_feed:
                goToUrl("index.json");
                return true;
//            case R.id.menu_my_feed:
//                goToUrl("index.json");
//                return true;
            case R.id.menu_logout:
                AuthManager.saveAuthToken(this, null);
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int HISTORY_MENU_BASE_ID = 1000;
    private static final int GROUP_MENU_BASE_ID = 2000;

    private void updateHistoryMenuItem(Menu menu) {
        MenuItem historyItem = menu.findItem(R.id.menu_history);
        if (visitedUrls.size() <= 1) {
            historyItem.setEnabled(false);
        } else {
            historyItem.setEnabled(true);
            SubMenu subMenu = historyItem.getSubMenu();
            subMenu.clear();
            for (int i = 0; i < Math.min(visitedUrls.size() - 1, 8); i++) {
                String url = visitedUrls.get(visitedUrls.size() - 2 - i);
                subMenu.add(0, HISTORY_MENU_BASE_ID + i, i, url);
            }
        }
        historyItem.setVisible(false); // Mortals shalt not see this item
    }

    private void updateGroupsMenuItem(Menu menu) {
        MenuItem groupsItem = menu.findItem(R.id.menu_groups);
        if ((userProfile == null) ||
                (userProfile.getSubscriptions().getGroupSubscriptions() == null) ||
                (userProfile.getSubscriptions().getGroupSubscriptions().size() == 0))
        {
            groupsItem.setEnabled(false);
        } else {
            List<Subscriptions.GroupSubscription> groupSubscriptions = userProfile.getSubscriptions().getGroupSubscriptions();
            groupsItem.setEnabled(true);
            SubMenu subMenu = groupsItem.getSubMenu();
            subMenu.clear();
            for (int i = 0; i < groupSubscriptions.size(); i++) {
                Group group = groupSubscriptions.get(i).getGroup();
                String name = group.getName();
                if (group.isPrivate()) {
                    name = "\uD83D\uDD12 " + name;
                }
                subMenu.add(0, GROUP_MENU_BASE_ID + i, i, name);
            }
        }
    }

    private static class GalleryImageLoader extends AsyncTask<String, Void, Bitmap> {
        private final ImageView target;
        private final MediaLoader.SuccessCallback callback;

        public GalleryImageLoader(ImageView target, MediaLoader.SuccessCallback callback) {
            this.target = target;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            try {
                InputStream is = new URL(url).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                Log.e(e.getMessage(), e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            target.setImageBitmap(bitmap);
            callback.onSuccess();
        }
    }

    private class UserProfileLoader extends AsyncTask<Void, Void, UserProfile> {
        private final NavigationWidget navigationWidget;
        private final Menu navigationMenu;

        public UserProfileLoader(NavigationWidget navigationWidget, Menu navigationMenu) {
            this.navigationWidget = navigationWidget;
            this.navigationMenu = navigationMenu;
        }

        @Override
        protected UserProfile doInBackground(Void... params) {
            WhoAmI whoAmI = ServerApi.askWhoAmI();
            if (whoAmI == null) {
                return null;
            }
            Subscriptions subscriptions = ServerApi.loadSubscriptions(whoAmI.getUser().getName());
            if (subscriptions == null) {
                return null;
            }
            return new UserProfile(whoAmI, subscriptions);
        }

        @Override
        protected void onPostExecute(UserProfile userProfile) {
            if (userProfile != null) {
                MainActivity.this.userProfile = userProfile;
                if (navigationWidget != null) {
                    navigationWidget.setGroups(userProfile.getSubscribedGroups());
                }
                if (navigationMenu != null) {
                    updateGroupsMenuItem(navigationMenu);
                }
            }
        }
    }
}
