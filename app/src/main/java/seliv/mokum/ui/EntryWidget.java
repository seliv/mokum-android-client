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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import seliv.mokum.MainActivity;
import seliv.mokum.R;
import seliv.mokum.api.ServerApi;
import seliv.mokum.api.model.Attachment;
import seliv.mokum.api.model.Comment;
import seliv.mokum.api.model.Comments;
import seliv.mokum.api.model.Entry;
import seliv.mokum.api.model.Group;
import seliv.mokum.api.model.LikeResult;
import seliv.mokum.api.model.Likes;
import seliv.mokum.api.model.Reason;
import seliv.mokum.api.model.User;

/**
 * Created by aselivanov on 2/5/2016.
 */
public class EntryWidget extends RelativeLayout {
    private TextView userAndEntryText;
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
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(getPxSize(16), 0, getPxSize(16), 0);
        setLayoutParams(params);
        initChildren(context);
    }

    private void initChildren(Context context) {
        inflate(context, R.layout.layout_entry, this);
        userAndEntryText = (TextView) findViewById(R.id.userAndEntryTextView);
        timeText = (TextView) findViewById(R.id.timeTextView);
        actionsText = (TextView) findViewById(R.id.actionsText);
        actionsProgressBar = (ProgressBar) findViewById(R.id.actionsProgressBar);
        likesIcon = (TextView) findViewById(R.id.likesIconTextView);
        likesText = (TextView) findViewById(R.id.likesTextView);
        likesProgressBar = (ProgressBar) findViewById(R.id.likesProgressBar);
        avatar = (ImageView) findViewById(R.id.avatarImageView);
        attachmentsView = (LinearLayout) findViewById(R.id.attachmentsView);
        commentsView = (LinearLayout) findViewById(R.id.commentsLayout);
    }

    private int getPxSize(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, getResources().getDisplayMetrics());
    }

    public void setEntry(Map<Long, User> users, Map<Long, Group> groups, Entry entry) {
        long userId = entry.getUserId();
        User user = users.get(userId);

        SpannableStringBuilder userNameBuilder = new SpannableStringBuilder();
        if (isEntryPrivate(entry, users)) {
            userNameBuilder.append(Html.fromHtml("&#x1f512; "));
        }
        int userNameStartPos = userNameBuilder.length();
        userNameBuilder.append(user.getDisplayName());
        final String userUrl = users.get(userId).getName() + ".json";
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                System.out.println("userUrl = " + userUrl);
                goToUrl(userUrl);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                Typeface tf = ds.getTypeface();
                ds.setTypeface(Typeface.create(tf, Typeface.BOLD));
            }
        };
        userNameBuilder.setSpan(clickable, userNameStartPos, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userNameBuilder.setSpan(new ForegroundColorSpan(0xFF555599), userNameStartPos, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        constructReasonDescription(userNameBuilder, entry.getReason(), users, groups);

        userNameBuilder.append("\n");
        int entryStartPos = userNameBuilder.length();
        userNameBuilder.append(Html.fromHtml(entry.getText()));
        userNameBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPostText)), entryStartPos, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        replaceUnderlinedUrl(userNameBuilder);
        userNameBuilder.setSpan(new AvatarLeadingMarginSpan(), 0, userNameBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        userAndEntryText.setText(userNameBuilder);
        userAndEntryText.setMovementMethod(LinkMovementMethod.getInstance());
        userAndEntryText.setHighlightColor(Color.TRANSPARENT);
        userAndEntryText.setLinkTextColor(0xFF555599);
        userAndEntryText.setClickable(true);

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

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
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

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
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

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
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

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
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
            likesText.setVisibility(View.GONE);
            likesIcon.setVisibility(View.GONE);
            likesProgressBar.setVisibility(View.GONE);
        }
    }

    private void setAttachments(String entryUrl, Map<Long, User> users, List<Attachment> attachments) {
        if (attachments.size() > 0) {
            int maxHeight = Integer.MIN_VALUE;
            for (Attachment attachment : attachments) {
                if (attachment.getThumbHeight() > maxHeight) {
                    maxHeight = attachment.getThumbHeight();
                }
            }
            int pxHeight = getPxSize(maxHeight);
            if (pxHeight > maxHeight * 2) {
                pxHeight = maxHeight * 2; // Artificially limiting thumbnail size for extra-high DPI screens
            }

            final String[] thumbUrls = new String[attachments.size()];
            final String[] imageUrls = new String[attachments.size()];
            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);
                thumbUrls[i] = "https://mokum.place" + attachment.getThumbUrl();
                imageUrls[i] = "https://mokum.place" + attachment.getMediumUrl();
            }

            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                // Never scaling the width up, only down.
                int width = Math.min(attachment.getThumbWidth(), attachment.getThumbWidth() * maxHeight / attachment.getThumbHeight());
                int pxWidth = getPxSize(width);
                if (pxWidth > width * 2) {
                    pxWidth = width * 2; // Artificially limiting thumbnail size for extra-high DPI screens
                }
                imageView.setMaxWidth(pxWidth);
                imageView.setMinimumWidth(pxWidth);
                imageView.setMaxHeight(pxHeight);
                imageView.setMinimumHeight(pxHeight);
                imageView.setPadding(0, getPxSize(7), getPxSize(4), 0);

                String url = "https://mokum.place" + attachment.getThumbUrl();
                new AvatarLoader(imageView).execute(url);

                final int index = i;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) v.getContext()).showImageGallery(thumbUrls, imageUrls, index);
                    }
                });

                attachmentsView.addView(imageView);
            }
        }
    }

    private void setComments(String entryUrl, Map<Long, User> users, List<Comment> comments, int commentsCount) {
        if (comments.size() > 0) {
            commentsView.setVisibility(View.VISIBLE);
            commentsView.removeAllViews();
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsWithMargin = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsWithMargin.setMargins(0, 0, 0, getPxSize(6));
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                TextView iconView = new TextView(getContext());
                iconView.setLayoutParams(params);
                iconView.setText("\uD83D\uDCAC ");
                iconView.setTextSize(13.0f);

                SpannableStringBuilder commentBuilder = new SpannableStringBuilder();
                commentBuilder.append(Html.fromHtml(comment.getText())).append(" - ");
                replaceUnderlinedUrl(commentBuilder);
                int start = commentBuilder.length();
                commentBuilder.append(users.get(comment.getUserId()).getDisplayName());
                int end = commentBuilder.length();
                final String userUrl = users.get(comment.getUserId()).getName() + ".json";
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("userUrl = " + userUrl);
                        goToUrl(userUrl);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
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
                commentView.setTextSize(13.0f);
                commentView.setLineSpacing(1f, 1.0f);
                commentView.setMovementMethod(LinkMovementMethod.getInstance());
                commentView.setHighlightColor(Color.TRANSPARENT);
                commentView.setLinkTextColor(0xFF555599);
                commentView.setTextColor(getResources().getColor(R.color.colorCommentText));
                commentView.setClickable(true);

                LinearLayout singleCommentView = new LinearLayout(getContext());
                singleCommentView.setOrientation(LinearLayout.HORIZONTAL);
                singleCommentView.addView(iconView);
                singleCommentView.addView(commentView);

                commentsView.addView(singleCommentView, paramsWithMargin);

                if ((i == 0) && commentsCount > comments.size()) {
                    final ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
                    progressBar.setLayoutParams(params);
                    progressBar.setVisibility(View.GONE);

                    TextView emptyIconView = new TextView(getContext());
                    emptyIconView.setLayoutParams(params);
                    emptyIconView.setText("     ");
                    emptyIconView.setTextSize(13.0f);

                    TextView moreView = new TextView(getContext());
                    moreView.setLayoutParams(params);
                    moreView.setTypeface(moreView.getTypeface(), Typeface.ITALIC);
                    moreView.setText(Html.fromHtml("<font color=#555599>" + (commentsCount - comments.size()) + " more comments</font>"));
                    moreView.setTextSize(13.0f);
                    moreView.setLineSpacing(1f, 1.0f);

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
                    commentsView.addView(moreCommentView, paramsWithMargin);
                }
            }
        } else {
            commentsView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isEntryPrivate(Entry entry, Map<Long, User> users) {
//        if (entry.getReason().getUserPrivate() != null) {
//            return true;
//        }
//        User user = users.get(entry.getUserId());
//        if ("private".equalsIgnoreCase(user.getStatus())) {
//            return true;
//        }
//        return false;
        return !entry.isPublic();
    }

    private void constructReasonDescription(SpannableStringBuilder builder, Reason reason, Map<Long, User> users, Map<Long, Group> groups) {
        String andSuffix = "";
        boolean hasReason = false;
        int resonStartPos = builder.length();
        if ((reason.getUser() != null) || (reason.getGroup() != null)) {
            hasReason = true;
            builder.append("posted to ");
            andSuffix = ", ";
            String suffixLocal = "";
            if (reason.getUser() != null) {
                for (Long userId : reason.getUser()) {
                    User user = users.get(userId);
                    // TODO: Extract a method to add clickable user / other entity, remove cut-n-paste
                    builder.append(suffixLocal);
                    int startPos = builder.length();
                    builder.append(user.getDisplayName());
                    final String userUrl = user.getName() + ".json";
                    ClickableSpan clickable = new ClickableSpan() {
                        public void onClick(View view) {
                            System.out.println("userUrl = " + userUrl);
                            goToUrl(userUrl);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            Typeface tf = ds.getTypeface();
                            ds.setTypeface(Typeface.create(tf, Typeface.BOLD));
                        }
                    };
                    builder.setSpan(clickable, startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(0xFF555599), startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    suffixLocal = ", ";
                }
            }
            if (reason.getGroup() != null) {
                for (Long groupId : reason.getGroup()) {
                    Group group = groups.get(groupId);
                    // TODO: Extract a method to add clickable user / other entity, remove cut-n-paste
                    builder.append(suffixLocal);
                    int startPos = builder.length();
                    builder.append(group.getDisplayName());
                    final String groupUrl = group.getUrl() + ".json";
                    ClickableSpan clickable = new ClickableSpan() {
                        public void onClick(View view) {
                            System.out.println("groupUrl = " + groupUrl);
                            goToUrl(groupUrl);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            Typeface tf = ds.getTypeface();
                            ds.setTypeface(Typeface.create(tf, Typeface.BOLD));
                        }
                    };
                    builder.setSpan(clickable, startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(0xFF555599), startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    suffixLocal = ", ";
                }
            }
        }
        if (reason.getUserLikes() != null) {
            hasReason = true;
            builder.append(andSuffix);
            builder.append("liked by ");
            andSuffix = ", ";
            String suffixLocal = "";
            for (Long userId : reason.getUserLikes()) {
                User user = users.get(userId);
                // TODO: Extract a method to add clickable user / other entity, remove cut-n-paste
                builder.append(suffixLocal);
                int startPos = builder.length();
                builder.append(user.getDisplayName());
                final String userUrl = user.getName() + ".json";
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("userUrl = " + userUrl);
                        goToUrl(userUrl);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        Typeface tf = ds.getTypeface();
                        ds.setTypeface(Typeface.create(tf, Typeface.BOLD));
                    }
                };
                builder.setSpan(clickable, startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(0xFF555599), startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                suffixLocal = ", ";
            }
        }
        if (reason.getUserComments() != null) {
            hasReason = true;
            builder.append(andSuffix);
            builder.append("commented by ");
            andSuffix = ", ";
            String suffixLocal = "";
            for (Long userId : reason.getUserComments()) {
                User user = users.get(userId);
                // TODO: Extract a method to add clickable user / other entity, remove cut-n-paste
                builder.append(suffixLocal);
                int startPos = builder.length();
                builder.append(user.getDisplayName());
                final String userUrl = user.getName() + ".json";
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("userUrl = " + userUrl);
                        goToUrl(userUrl);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        Typeface tf = ds.getTypeface();
                        ds.setTypeface(Typeface.create(tf, Typeface.BOLD));
                    }
                };
                builder.setSpan(clickable, startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(0xFF555599), startPos, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                suffixLocal = ", ";
            }
        }
        if (reason.getUserPrivate() != null) {
            hasReason = true;
            builder.append("posted to private sub-feed");
        }
        if (hasReason) {
            builder.insert(resonStartPos, " (");
            builder.append(")");
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

    private void replaceUnderlinedUrl(SpannableStringBuilder spannableStringBuilder) {
        URLSpan[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = spannableStringBuilder.getSpanStart(span);
            int end = spannableStringBuilder.getSpanEnd(span);
            spannableStringBuilder.removeSpan(span);
            String url = span.getURL();
            if (url.startsWith("/")) {
                // Mokum internal URL - handling internally
                if (!url.contains(".json")) {
                    if (url.contains("?")) {
                        url = url.replace("?", ".json?");
                    } else {
                        while (url.endsWith("/")) {
                            url = url.substring(0, url.length() - 1);
                        }
                        url = url + ".json";
                    }
                }
                final String jsonUrl = url;
                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(View view) {
                        System.out.println("Inline url = " + jsonUrl);
                        goToUrl(jsonUrl);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                spannableStringBuilder.setSpan(clickable, start, end, 0);
            } else {
                URLSpan newSpan = new URLSpan(url) {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                spannableStringBuilder.setSpan(newSpan, start, end, 0);
            }
        }
    }
    private class AvatarLeadingMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {
        @Override
        public int getLeadingMarginLineCount() {
            return 3;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            if (first) {
                return getPxSize(54 + 8);
            } else {
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        }
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
