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
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Group;
import seliv.mokum.api.model.Post;
import seliv.mokum.api.model.PostRequest;

/**
 * Created by aselivanov on 2/11/2016.
 */
public class PostWidget extends LinearLayout {
    private List<Group> groups = Arrays.asList((Group)new MyFeedFakeGroup());

    private GroupListWidget groupListWidget;
    private EditText postEdit;
    private CheckBox commentsDisabledCheckBox;
    private Button postButton;
    private Button cancelButton;
    private ProgressBar postProgressBar;
    private TextView resultText;

    private Listener listener;

    public PostWidget(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        groupListWidget = new GroupListWidget(context);
        groupListWidget.setLayoutParams(paramsFill);

        postEdit = new EditText(context);
        postEdit.setLayoutParams(paramsFill);
        postEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        postEdit.setMaxLines(6);

        TextView commentsDisabledLabel = new TextView(context);
        commentsDisabledLabel.setLayoutParams(params);
        commentsDisabledLabel.setText("Disable comments ");

        commentsDisabledCheckBox = new CheckBox(context);
        commentsDisabledCheckBox.setLayoutParams(params);
        commentsDisabledCheckBox.setChecked(false);

        postButton = new Button(context);
        postButton.setLayoutParams(params);
        postButton.setText("Post");

        cancelButton = new Button(context);
        cancelButton.setLayoutParams(params);
        cancelButton.setText("Cancel");

        postProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        postProgressBar.setLayoutParams(params);
        postProgressBar.setVisibility(View.GONE);

        resultText = new TextView(context);
        resultText.setLayoutParams(params);
        resultText.setVisibility(View.GONE);

        LinearLayout controlsLayout = new LinearLayout(context);
        controlsLayout.setOrientation(LinearLayout.HORIZONTAL);
        controlsLayout.setLayoutParams(paramsFill);

        controlsLayout.addView(commentsDisabledLabel);
        controlsLayout.addView(commentsDisabledCheckBox);
        controlsLayout.addView(cancelButton);
        controlsLayout.addView(postButton);
        controlsLayout.addView(postProgressBar);

        addView(groupListWidget);
        addView(postEdit);
        addView(resultText);
        addView(controlsLayout);

        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = postEdit.getText().toString();
                if (commentsDisabledCheckBox.isChecked()) {
                    s = "#@" + s;
                }
                new PostSender(PostWidget.this).execute(s);
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDismissed();
                }
            }
        });
    }

    void animateAppearance() {
        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        ScaleAnimation anim = new ScaleAnimation(1,1,0,1);
        anim.setDuration(mShortAnimationDuration);
        anim.setFillAfter(true);
        this.startAnimation(anim);
    }

    public void setGroups(List<Group> groups) {
        this.groups = new ArrayList<>();
        this.groups.add(new MyFeedFakeGroup());
        this.groups.addAll(groups);
        groupListWidget.setGroups(this.groups);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private static class PostSender extends AsyncTask<String, Void, PostSender.PostSendingResult> {
        private final PostWidget postWidget;

        public PostSender(PostWidget postWidget) {
            this.postWidget = postWidget;
            postWidget.postEdit.setEnabled(false);
            postWidget.commentsDisabledCheckBox.setEnabled(false);
            postWidget.cancelButton.setEnabled(false);
            postWidget.postButton.setVisibility(View.GONE);
            postWidget.postProgressBar.setVisibility(View.VISIBLE);
            postWidget.resultText.setText("Sending a post...");
        }

        @Override
        protected PostSender.PostSendingResult doInBackground(String... params) {
            String text = params[0];
            boolean commentsDisabled;
            if (text.startsWith("#@")) {
                commentsDisabled = true;
                text = text.substring(2, text.length());
            } else {
                commentsDisabled = false;
            }

            PostRequest postRequest = PostRequest.create(text, commentsDisabled);
            Post post = ServerApi.sendPost(postRequest);

            if (post == null) {
                return new PostSendingResult("Error posting a post: JSON is null");
            } else {
                return new PostSendingResult(post);
            }
        }

        @Override
        protected void onPostExecute(PostSendingResult result) {
            super.onPostExecute(result);

            postWidget.postProgressBar.setVisibility(View.GONE);
            postWidget.resultText.setVisibility(View.VISIBLE);

            if (result.error != null) {
                postWidget.postEdit.setEnabled(true);
                postWidget.commentsDisabledCheckBox.setEnabled(true);
                postWidget.cancelButton.setEnabled(true);
                postWidget.postButton.setVisibility(View.VISIBLE);
                postWidget.resultText.setText(result.error);
            } else {
                postWidget.postEdit.setVisibility(View.GONE);
                postWidget.commentsDisabledCheckBox.setVisibility(View.GONE);
                postWidget.postButton.setVisibility(View.GONE);
                postWidget.cancelButton.setVisibility(View.GONE);
                postWidget.resultText.setText(result.post.getText());
            }
        }

        static class PostSendingResult {
//            final Map<Long, User> users;
            final Post post;
            final String error;

            public PostSendingResult(String error) {
//                users = null;
                post = null;
                this.error = error;
            }

            public PostSendingResult(/*Map<Long, User> users, */Post post) {
//                this.users = users;
                this.post = post;
                error = null;
            }
        }
    }

    interface Listener {
        void onDismissed();
    }

    private static class MyFeedFakeGroup extends Group {
        @Override
        public String getDisplayName() {
            return "My feed";
        }
        public String getName() {
            return "MyFeed";
        }
    }
}
