package seliv.mokum.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import seliv.mokum.MainActivity;
import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Attachment;
import seliv.mokum.api.model.Comment;
import seliv.mokum.api.model.Comments;
import seliv.mokum.api.model.Entry;
import seliv.mokum.api.model.LikeResult;
import seliv.mokum.api.model.Likes;
import seliv.mokum.api.model.User;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class EntryWidget extends LinearLayout {
    private TextView userText;
    private TextView entryText;
    private TextView timeText;
    private TextView actionsText;
    private ProgressBar actionsProgressBar;
    private TextView likesIcon;
    private TextView likesText;
    private ProgressBar likesProgressBar;
    private ImageView avatar;
    private LinearLayout attachmentsView;
    private LinearLayout commentsView;

    public EntryWidget(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        initChildren(context);
    }

    /*
        public EntryWidget(Context context, AttributeSet attrs) {
            super(context, attrs);
            initChildren(context);
        }

    */
    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        userText = new TextView(context);
        userText.setLayoutParams(params);
        userText.setTypeface(userText.getTypeface(), 1); // 1 = bold
        entryText = new TextView(context);
        entryText.setLayoutParams(params);
        timeText = new TextView(context);
        timeText.setLayoutParams(params);
        timeText.setTextSize(11.0f);
        actionsText = new TextView(context);
        actionsText.setLayoutParams(params);
        actionsText.setTextSize(11.0f);
        actionsProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        actionsProgressBar.setLayoutParams(params);
        actionsProgressBar.setVisibility(View.GONE);
        likesIcon = new TextView(context);
        likesIcon.setLayoutParams(params);
        likesIcon.setTextSize(11.0f);
        likesText = new TextView(context);
        likesText.setLayoutParams(params);
        likesText.setTextSize(11.0f);
        likesProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        likesProgressBar.setLayoutParams(params);
        likesProgressBar.setVisibility(View.GONE);

        int pxSize = getPxSize(50);
        avatar = new ImageView(context);
        avatar.setLayoutParams(params);
        avatar.setMaxWidth(pxSize);
        avatar.setMaxHeight(pxSize);
        avatar.setMinimumWidth(pxSize);
        avatar.setMinimumHeight(pxSize);
        avatar.setPadding(0, getPxSize(7), getPxSize(4), 0);

//        params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        likeButton = new Button(context);
//        likeButton.setLayoutParams(params);
//        hideButton = new Button(context);
//        hideButton.setLayoutParams(params);

        LinearLayout.LayoutParams paramsWithMargin = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paramsWithMargin.setMargins(0, 0, 0, getPxSize(8));
        LinearLayout postAndComments = new LinearLayout(getContext());
        postAndComments.setLayoutParams(paramsWithMargin);
        postAndComments.setOrientation(LinearLayout.VERTICAL);
        postAndComments.addView(userText);
        postAndComments.addView(entryText);

        attachmentsView = new LinearLayout(postAndComments.getContext());
        attachmentsView.setOrientation(LinearLayout.HORIZONTAL);
        HorizontalScrollView attachmentsScroll = new HorizontalScrollView(postAndComments.getContext());
        attachmentsScroll.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        attachmentsScroll.setHorizontalFadingEdgeEnabled(true);
        attachmentsScroll.setFadingEdgeLength(getPxSize(16));
        attachmentsScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        attachmentsScroll.addView(attachmentsView);
        postAndComments.addView(attachmentsScroll);

        LinearLayout timeAndActions = new LinearLayout(getContext());
        timeAndActions.setLayoutParams(params);
        timeAndActions.setOrientation(LinearLayout.HORIZONTAL);
        timeAndActions.addView(timeText);
        timeAndActions.addView(actionsProgressBar);
        timeAndActions.addView(actionsText);

        postAndComments.addView(timeAndActions);

        LinearLayout likesAndProgress = new LinearLayout(getContext());
        likesAndProgress.setLayoutParams(params);
        likesAndProgress.setOrientation(LinearLayout.HORIZONTAL);
        likesAndProgress.addView(likesIcon);
        likesAndProgress.addView(likesProgressBar);
        likesAndProgress.addView(likesText);

        postAndComments.addView(likesAndProgress);

        commentsView = new LinearLayout(postAndComments.getContext());
        commentsView.setOrientation(LinearLayout.VERTICAL);
        postAndComments.addView(commentsView);

        addView(avatar);
        addView(postAndComments);
//        addView(likeButton);
//        addView(hideButton);
    }

    private int getPxSize(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, getResources().getDisplayMetrics());
    }

    public void setEntry(Map<Long, User> users, Entry entry) {
        long userId = entry.getUserId();
        User user = users.get(userId);

        SpannableStringBuilder userNameBuilder = new SpannableStringBuilder();
        userNameBuilder.append(user.getDisplayName());
        final String userUrl = users.get(userId).getName() + ".json";
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                System.out.println("userUrl = " + userUrl);
                goToUrl(userUrl);
            }
        };
        userNameBuilder.setSpan(clickable, 0, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userNameBuilder.setSpan(new ForegroundColorSpan(0xFF555599), 0, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userText.setText(userNameBuilder);
        userText.setMovementMethod(LinkMovementMethod.getInstance());
        userText.setHighlightColor(Color.TRANSPARENT);
        userText.setClickable(true);

        entryText.setText(entry.getText());
        timeText.setText(DateUtils.getRelativeTimeSpanString(entry.getPublishedAt().getTime()));

        setAttachments(entry.getUrl(), users, entry.getAttachments());

        canComment = entry.isCanComment();
        setActions(entry.getId(), entry.isUserLiked());
        setLikes(entry.getUrl(), users, entry.getLikes(), entry.getMoreLikes());
        String avatarUrl = user.getAvatarUrl();
        String url;
        if (avatarUrl.contains("system/avatars")) {
            url = "https://mokum.place" + avatarUrl;
        } else {
            url = "http:" + avatarUrl;
        }
        new AvatarLoader(avatar).execute(url);

//        likeButton.setText("like");
//        hideButton.setText("hide");

        setComments(entry.getUrl(), users, entry.getComments(), entry.getCommentsCount());
    }

    private boolean liked; // TODO: Not a good solution, should introduce a good model or store entire entry
    private long entryId; // TODO: Not a good solution, should introduce a good model or store entire entry
    private boolean canComment; // TODO: Not a good solution, should introduce a good model or store entire entry

    private boolean hasCommentWidget() {
        for (int i = 0; i < commentsView.getChildCount(); i++) {
            View view = commentsView.getChildAt(i);
            if (view instanceof CommentWidget) {
                return true;
            }
        }
        return false;
    }

    private void setActions(final long entryId, final boolean liked) {
        this.liked = liked;
        this.entryId = entryId;

        SpannableStringBuilder actionsBuilder = new SpannableStringBuilder(" - ");
        if (canComment) {
            int start = actionsBuilder.length();
            actionsBuilder.append("Comment");
            int end = actionsBuilder.length();
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
                    if (!hasCommentWidget()) {
                        CommentWidget commentWidget = new CommentWidget(getContext(), entryId);
                        commentsView.setVisibility(View.VISIBLE);
                        commentsView.addView(commentWidget);
                    }
                }
            };
            actionsBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionsBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            actionsBuilder.append("Like");
        }


        actionsBuilder.append(" - ");
        int start = actionsBuilder.length();
        if (liked) {
            actionsBuilder.append("Unlike");
        } else {
            actionsBuilder.append("Like");
        }
        int end = actionsBuilder.length();
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                actionsProgressBar.setVisibility(View.VISIBLE);
                new LikeSender(EntryWidget.this, entryId, !liked).execute();
            }
        };
        actionsBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionsBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        actionsText.setText(actionsBuilder);
        actionsText.setMovementMethod(LinkMovementMethod.getInstance());
        actionsText.setHighlightColor(Color.TRANSPARENT);
        actionsText.setClickable(true);
    }

    private String likesUrl; // TODO: Not a good solution, should introduce a good model or store entire entry

    private void setLikes(String entryUrl, Map<Long, User> users, List<Long> likes, int moreLikes) {
        if (likes.size() > 0) {
            likesProgressBar.setVisibility(View.GONE);
            likesText.setVisibility(View.VISIBLE);
            likesIcon.setVisibility(View.VISIBLE);
            likesIcon.setText("\u263a ");
            SpannableStringBuilder likesBuilder = new SpannableStringBuilder();
            for (Long likeUserId : likes) {
                if (likesBuilder.length() > 0) {
                    likesBuilder.append(", ");
                }
                int start = likesBuilder.length();
                likesBuilder.append(users.get(likeUserId).getDisplayName());
                int end = likesBuilder.length();
                final String userUrl = users.get(likeUserId).getName() + ".json";
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("userUrl = " + userUrl);
                        goToUrl(userUrl);
                    }
                };
                likesBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                likesBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (this.likesUrl == null) {
                this.likesUrl = "https://mokum.place" + entryUrl + "/likes.json";
            }
            if ((moreLikes > 0) && (entryUrl != null)) {
                likesBuilder.append(", and ");
                int start = likesBuilder.length();
                likesBuilder.append(String.valueOf(moreLikes)).append(" more");
                int end = likesBuilder.length();
                final String likesUrl = entryUrl;
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        likesIcon.setVisibility(View.GONE);
                        likesProgressBar.setVisibility(View.VISIBLE);
                        new LikesLoader(EntryWidget.this).execute(likesUrl);
                    }
                };
                likesBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                likesBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            likesText.setText(likesBuilder);
            likesText.setMovementMethod(LinkMovementMethod.getInstance());
            likesText.setHighlightColor(Color.TRANSPARENT);
            likesText.setClickable(true);
        } else {
            likesText.setVisibility(View.INVISIBLE);
            likesIcon.setVisibility(View.INVISIBLE);
            likesProgressBar.setVisibility(View.GONE);
        }
    }

    private void setAttachments(String entryUrl, Map<Long, User> users, List<Attachment> attachments) {
        if (attachments.size() > 0) {
            for (Attachment attachment : attachments) {
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                int pxWidth = getPxSize(150);
                int pxHeight = getPxSize(100);
                imageView.setMaxWidth(pxWidth);
//                imageView.setMinimumWidth(pxWidth);
                imageView.setMaxHeight(pxHeight);
//                imageView.setMinimumHeight(pxHeight);
                imageView.setPadding(0, getPxSize(7), getPxSize(4), 0);

                String url = "https://mokum.place" + attachment.getThumbUrl();
                new AvatarLoader(imageView).execute(url);

                attachmentsView.addView(imageView);
            }
        }
    }

    private void setComments(String entryUrl, Map<Long, User> users, List<Comment> comments, int commentsCount) {
        if (comments.size() > 0) {
            commentsView.setVisibility(View.VISIBLE);
            commentsView.removeAllViews();
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                TextView iconView = new TextView(getContext());
                iconView.setLayoutParams(params);
                iconView.setText("\uD83D\uDCAC ");
                iconView.setTextSize(12.0f);

                SpannableStringBuilder commentBuilder = new SpannableStringBuilder();
                commentBuilder.append(comment.getText()).append(" - ");
                int start = commentBuilder.length();
                commentBuilder.append(users.get(comment.getUserId()).getDisplayName());
                int end = commentBuilder.length();
                final String userUrl = users.get(comment.getUserId()).getName() + ".json";
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("userUrl = " + userUrl);
                        goToUrl(userUrl);
                    }
                };
                commentBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentBuilder.setSpan(new ForegroundColorSpan(0xFF555599), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                TextView commentView = new TextView(getContext());
                commentView.setLayoutParams(params);
//                commentView.setTypeface(userText.getTypeface(), 1); // 1 = bold
                commentView.setText(commentBuilder);
//                Spanned spannedText = Html.fromHtml(comment.getText() + " - <font color=#555599>" + users.get(comment.getUserId()).getDisplayName() + "</font>");
//                commentView.setText(spannedText);
                commentView.setTextSize(12.0f);
                commentView.setMovementMethod(LinkMovementMethod.getInstance());
                commentView.setHighlightColor(Color.TRANSPARENT);
                commentView.setClickable(true);

                LinearLayout singleCommentView = new LinearLayout(getContext());
                singleCommentView.setOrientation(LinearLayout.HORIZONTAL);
                singleCommentView.addView(iconView);
                singleCommentView.addView(commentView);

                commentsView.addView(singleCommentView);

                if ((i == 0) && commentsCount > comments.size()) {
                    final ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
                    progressBar.setLayoutParams(params);
                    progressBar.setVisibility(View.GONE);

                    TextView emptyIconView = new TextView(getContext());
                    emptyIconView.setLayoutParams(params);
                    emptyIconView.setText("     ");
                    emptyIconView.setTextSize(12.0f);

                    TextView moreView = new TextView(getContext());
                    moreView.setLayoutParams(params);
                    moreView.setTypeface(userText.getTypeface(), Typeface.ITALIC);
                    moreView.setText(Html.fromHtml("<font color=#555599>" + (commentsCount - comments.size()) + " more comments</font>"));
                    moreView.setTextSize(12.0f);

                    if (entryUrl != null) {
                        final String url = entryUrl;
                        commentsView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressBar.setVisibility(View.VISIBLE);
                                new CommentsLoader(EntryWidget.this).execute(url);
                            }
                        });
                    } else {
                        commentsView.setOnClickListener(null);
                    }

                    LinearLayout moreCommentView = new LinearLayout(getContext());
                    moreCommentView.setOrientation(LinearLayout.HORIZONTAL);
                    moreCommentView.addView(emptyIconView);
                    moreCommentView.addView(progressBar);
                    moreCommentView.addView(moreView);
                    commentsView.addView(moreCommentView);
                }
            }
        } else {
            commentsView.setVisibility(View.INVISIBLE);
        }
    }

    private void goToUrl(String url) {
//        Intent intent = new Intent(getContext(), MainActivity.class);
//        intent.putExtra("token", token);
//        intent.putExtra("url", url);
//        getContext().startActivity(intent);
        // TODO: Replace with neat interfaces
        MainActivity activity = (MainActivity) getContext();
        activity.goToUrl(url);
    }

    private static class AvatarLoader extends AsyncTask<String, Void, Bitmap> {
        private final ImageView target;

        public AvatarLoader(ImageView target) {
            this.target = target;
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
        }
    }

    private static class CommentsLoader extends AsyncTask<String, Void, CommentsLoader.CommentsLoadingResult> {
        private final EntryWidget entryWidget;
        private String retryUrl;

        public CommentsLoader(EntryWidget target) {
            this.entryWidget = target;
        }

        @Override
        protected CommentsLoadingResult doInBackground(String... params) {
            String url = params[0];
            retryUrl = url;
            Comments comments = ServerApi.loadComments(url);

            if (comments == null) {
                return new CommentsLoadingResult("Error loading comments: JSON is null");
            } else {
                return new CommentsLoadingResult(comments.getUsers(), comments.getComments());
            }
        }

        @Override
        protected void onPostExecute(CommentsLoadingResult result) {
            super.onPostExecute(result);
            if (result.error != null) {
                entryWidget.commentsView.removeAllViews();
                TextView errorView = new TextView(entryWidget.commentsView.getContext());
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                errorView.setLayoutParams(params);
                errorView.setText("[Tap to retry] " + result.error);
                errorView.setTextSize(8.0f);
                entryWidget.commentsView.addView(errorView);

                errorView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CommentsLoader(entryWidget).execute(retryUrl);
                    }
                });
            } else {
                entryWidget.setComments(null, result.users, result.comments, result.comments.size());
            }
        }

        static class CommentsLoadingResult {
            final Map<Long, User> users;
            final List<Comment> comments;
            final String error;

            public CommentsLoadingResult(String error) {
                users = null;
                comments = null;
                this.error = error;
            }

            public CommentsLoadingResult(Map<Long, User> users, List<Comment> comments) {
                this.users = users;
                this.comments = comments;
                error = null;
            }
        }
    }

    private static class LikesLoader extends AsyncTask<String, Void, LikesLoader.LikesLoadingResult> {
        private final EntryWidget entryWidget;
        private String retryUrl;

        public LikesLoader(EntryWidget target) {
            this.entryWidget = target;
        }

        @Override
        protected LikesLoader.LikesLoadingResult doInBackground(String... params) {
            String url = params[0];
            retryUrl = url;
            Likes likes = ServerApi.loadLikes(url);

            if (likes == null) {
                return new LikesLoadingResult("Error loading likes: JSON is null");
            } else {
                return new LikesLoadingResult(likes.getUsers(), likes.getLikes());
            }
        }

        @Override
        protected void onPostExecute(LikesLoadingResult result) {
            super.onPostExecute(result);
            entryWidget.likesProgressBar.setVisibility(View.INVISIBLE);
            entryWidget.likesIcon.setVisibility(View.VISIBLE);
            if (result.error != null) {
                entryWidget.likesText.setText("[Tap to retry] " + result.error);
                entryWidget.likesText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LikesLoader(entryWidget).execute(retryUrl);
                    }
                });
            } else {
                entryWidget.setLikes(null, result.users, result.likes, 0);
            }
        }

        static class LikesLoadingResult {
            final Map<Long, User> users;
            final List<Long> likes;
            final String error;

            public LikesLoadingResult(String error) {
                users = null;
                likes = null;
                this.error = error;
            }

            public LikesLoadingResult(Map<Long, User> users, List<Long> likes) {
                this.users = users;
                this.likes = likes;
                error = null;
            }
        }
    }

    private static class LikeSender extends AsyncTask<Void, Void, LikeSender.LikeSendingResult> {
        private final EntryWidget entryWidget;
        private final long entryId;
        private final boolean like;

        public LikeSender(EntryWidget target, long entryId, boolean like) {
            this.entryWidget = target;
            this.entryId = entryId;
            this.like = like;
        }

        @Override
        protected LikeSendingResult doInBackground(Void... params) {
            LikeResult likeResult = ServerApi.sendLike(entryId, like);

            if (likeResult == null) {
                return new LikeSendingResult("Error sending like: JSON is null");
            } else {
                LikeSendingResult result;
                if (LikeResult.RESULT_LIKED.equalsIgnoreCase(likeResult.getResult())) {
                    result = new LikeSendingResult(true);
                } else if (LikeResult.RESULT_UNLIKED.equalsIgnoreCase(likeResult.getResult())) {
                    result = new LikeSendingResult(false);
                } else {
                    result = new LikeSendingResult("Error sending like: unknown result " + likeResult.getResult());
                }
                return result;
            }
        }

        @Override
        protected void onPostExecute(LikeSendingResult result) {
            super.onPostExecute(result);
            entryWidget.actionsProgressBar.setVisibility(View.GONE);
            if (result.error != null) {
                entryWidget.actionsText.setText("[Tap to retry] " + result.error);
                entryWidget.actionsText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LikeSender(entryWidget, entryId, like).execute();
                    }
                });
            } else {
                entryWidget.setActions(entryWidget.entryId, like);

                // Do likes reloading
                entryWidget.likesIcon.setVisibility(View.GONE);
                entryWidget.likesProgressBar.setVisibility(View.VISIBLE);
                new LikesLoader(entryWidget).execute(entryWidget.likesUrl);
            }
        }

        static class LikeSendingResult {
            final boolean liked;
            final String error;

            public LikeSendingResult(String error) {
                liked = false;
                this.error = error;
            }

            public LikeSendingResult(boolean liked) {
                this.liked = liked;
                error = null;
            }
        }
    }
}
