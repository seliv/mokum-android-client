package seliv.mokum.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Comment;
import seliv.mokum.api.model.CommentRequest;

/**
 * Created by aselivanov on 2/12/2016.
 */
public class CommentWidget extends LinearLayout {
    private final long postId;

    private EditText commentEdit;
    private Button commentButton;
    private Button cancelButton;
    private ProgressBar commentProgressBar;
    private TextView resultText;

    public CommentWidget(Context context, long postId) {
        super(context);
        this.postId = postId;
        setOrientation(LinearLayout.VERTICAL);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        commentEdit = new EditText(context);
        commentEdit.setLayoutParams(paramsFill);
        commentEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentEdit.setMaxLines(6);
        commentEdit.setText("Test comment from Android.");

//        commentEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            }
//        });
        commentEdit.requestFocus();

        commentButton = new Button(context);
        commentButton.setLayoutParams(params);
        commentButton.setText("Post");

        cancelButton = new Button(context);
        cancelButton.setLayoutParams(params);
        cancelButton.setText("Cancel");

        commentProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        commentProgressBar.setLayoutParams(params);
        commentProgressBar.setVisibility(View.GONE);

        resultText = new TextView(context);
        resultText.setLayoutParams(params);
        resultText.setVisibility(View.GONE);

        LinearLayout controlsLayout = new LinearLayout(context);
        controlsLayout.setOrientation(LinearLayout.HORIZONTAL);
        controlsLayout.setLayoutParams(paramsFill);

        controlsLayout.addView(commentButton);
        controlsLayout.addView(commentProgressBar);
        controlsLayout.addView(cancelButton);

        addView(commentEdit);
        addView(resultText);
        addView(controlsLayout);

        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = commentEdit.getText().toString();
                new CommentSender(CommentWidget.this, postId, s).execute();
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout)CommentWidget.this.getParent()).removeView(CommentWidget.this);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
//        this.setScaleY(0.0f);
//        this.animate().scaleY(1.0f).setDuration(mShortAnimationDuration);
        ScaleAnimation anim = new ScaleAnimation(1,1,0,1);
        anim.setDuration(mShortAnimationDuration);
        anim.setFillAfter(true);
        this.startAnimation(anim);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(commentEdit, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private static class CommentSender extends AsyncTask<Void, Void, CommentSender.CommentSendingResult> {
        private final CommentWidget commentWidget;
        private final long postId;
        private final String text;

        public CommentSender(CommentWidget commentWidget, long postId, String text) {
            this.commentWidget = commentWidget;
            this.postId = postId;
            this.text = text;

            commentWidget.commentEdit.setEnabled(false);
            commentWidget.cancelButton.setEnabled(false);
            commentWidget.commentButton.setVisibility(View.GONE);
            commentWidget.commentProgressBar.setVisibility(View.VISIBLE);
            commentWidget.resultText.setText("Sending a comment...");
        }

        @Override
        protected CommentSendingResult doInBackground(Void... params) {
            CommentRequest commentRequest = CommentRequest.create(text);
            Comment comment = ServerApi.sendComment(postId, commentRequest);

            if (comment == null) {
                return new CommentSendingResult("Error posting a comment: comment is null");
            } else {
                return new CommentSendingResult(comment);
            }
        }

        @Override
        protected void onPostExecute(CommentSendingResult result) {
            super.onPostExecute(result);

            commentWidget.commentProgressBar.setVisibility(View.GONE);
            commentWidget.resultText.setVisibility(View.VISIBLE);

            if (result.error != null) {
                commentWidget.commentEdit.setEnabled(true);
                commentWidget.cancelButton.setEnabled(true);
                commentWidget.commentButton.setVisibility(View.VISIBLE);
                commentWidget.resultText.setText(result.error);
            } else {
                commentWidget.commentEdit.setVisibility(View.GONE);
                commentWidget.commentButton.setVisibility(View.GONE);
                commentWidget.cancelButton.setVisibility(View.GONE);
                commentWidget.resultText.setText(result.comment.getText());
            }
        }

        static class CommentSendingResult {
            final Comment comment;
            final String error;

            public CommentSendingResult(String error) {
                comment = null;
                this.error = error;
            }

            public CommentSendingResult(Comment comment) {
                this.comment = comment;
                error = null;
            }
        }
    }

}
