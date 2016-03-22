package seliv.mokum.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import seliv.mokum.MainActivity;

/**
 * Created by aselivanov on 3/9/2016.
 */
public class NavigationWidget extends LinearLayout implements PostWidget.Listener {
    private Button olderButton;
    private Button newerButton;
    private Button postButton;

    private PostWidget postWidget;

    private String olderUrl;
    private String newerUrl;

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

    private void initChildren(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        LinearLayout navHolder = new LinearLayout(context);
        navHolder.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams outerStrutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5f);
        LinearLayout.LayoutParams innerStrutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f);
        olderButton = new Button(context);
        olderButton.setText("Older entries");
        olderButton.setLayoutParams(params);

        postButton = new Button(context);
        postButton.setText("Post");
        postButton.setLayoutParams(params);

        newerButton = new Button(context);
        newerButton.setText("Newer entries");
        newerButton.setLayoutParams(params);

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
