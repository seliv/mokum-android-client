package seliv.mokum.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Post;
import seliv.mokum.api.model.PostRequest;

/**
 * Created by aselivanov on 2/11/2016.
 */
public class PostWidget extends LinearLayout {
    private EditText postEdit;
    private CheckBox commentsDisabledCheckBox;
    private Button postButton;
    private Button cancelButton;
    private ProgressBar postProgressBar;
    private TextView resultText;

    public PostWidget(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        postEdit = new EditText(context);
        postEdit.setLayoutParams(paramsFill);
        postEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        postEdit.setMaxLines(6);
        postEdit.setText("Test post from Android.");

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

}