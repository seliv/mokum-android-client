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

package seliv.mokum.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import seliv.mokum.MainActivity;

/**
 * Created by aselivanov on 3/9/2016.
 */
public class NavigationWidget extends LinearLayout implements PostWidget.Listener {
    private Button olderButton;
    private Button newerButton;
    private Button postButton;
    private Button menuButton;

    private PostWidget postWidget;

    private String olderUrl;
    private String newerUrl;
    private PopupMenu menu;

    public NavigationWidget(Context context) {
        super(context);
        initChildren(context);
        setUrls(null, null);
    }

    public void setUrls(String olderUrl, String newerUrl) {
        this.olderUrl = olderUrl;
        this.newerUrl = newerUrl;
        if (olderUrl == null) {
            olderButton.setVisibility(View.GONE);
        } else {
            olderButton.setVisibility(View.VISIBLE);
        }
        if (newerUrl == null) {
            newerButton.setVisibility(View.GONE);
        } else {
            newerButton.setVisibility(View.VISIBLE);
        }
    }

    public void setNavigationOnly(boolean navigationOnly) {
        int visibility = navigationOnly ? View.INVISIBLE : View.VISIBLE;
        postButton.setVisibility(visibility);
        menuButton.setVisibility(visibility);
    }

    public PopupMenu getMenu() {
        return menu;
    }

    private void initChildren(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        LinearLayout navHolder = new LinearLayout(context);
        navHolder.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams outerStrutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5f);
        LinearLayout.LayoutParams innerStrutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f);
        olderButton = new Button(context);
        olderButton.setText("Older");
        olderButton.setLayoutParams(params);

        postButton = new Button(context);
        postButton.setText("Post");
        postButton.setLayoutParams(params);

        newerButton = new Button(context);
        newerButton.setText("Newer");
        newerButton.setLayoutParams(params);

        menuButton = new Button(context);
        menuButton.setText("...");
        menuButton.setLayoutParams(params);

        TextView viewLO = new TextView(context);
        viewLO.setLayoutParams(outerStrutParams);
        TextView viewLI = new TextView(context);
        viewLI.setLayoutParams(innerStrutParams);
        TextView viewRI = new TextView(context);
        viewRI.setLayoutParams(innerStrutParams);
        TextView viewRO = new TextView(context);
        viewRO.setLayoutParams(outerStrutParams);

        navHolder.addView(viewLO);
        navHolder.addView(olderButton);
        navHolder.addView(viewLI);
        navHolder.addView(postButton);
        navHolder.addView(viewRI);
        navHolder.addView(newerButton);
        navHolder.addView(viewRO);
        navHolder.addView(menuButton);

        postWidget = new PostWidget(context);
        postWidget.setVisibility(View.GONE);

        addView(navHolder);
        addView(postWidget);

        olderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (olderUrl != null) {
                    goToUrl(olderUrl);
                }
            }
        });
        newerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newerUrl != null) {
                    goToUrl(newerUrl);
                }
            }
        });
        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postButton.setEnabled(false);
                postWidget.setVisibility(View.VISIBLE);
                postWidget.animateAppearance();
            }
        });
        postWidget.setListener(this);

        menu = new PopupMenu(context, menuButton);
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
    }

    @Override
    public void onDismissed() {
        postButton.setEnabled(true);
        postWidget.setVisibility(View.GONE);
    }

    private void goToUrl(String url) {
        // TODO: Replace with neat interfaces
        MainActivity activity = (MainActivity) getContext();
        activity.goToUrl(url);
    }
}
