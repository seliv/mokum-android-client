package seliv.mokum.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import seliv.mokum.MainActivity;

/**
 * Created by aselivanov on 3/9/2016.
 */
public class NavigationWidget extends LinearLayout {
    private Button olderButton;
    private Button newerButton;

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
        setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        olderButton = new Button(context);
        olderButton.setText("Older entries");
        olderButton.setLayoutParams(params);

        newerButton = new Button(context);
        newerButton.setText("Newer entries");
        newerButton.setLayoutParams(params);

        addView(olderButton);
        addView(newerButton);

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
    }

    private void goToUrl(String url) {
        // TODO: Replace with neat interfaces
        MainActivity activity = (MainActivity) getContext();
        activity.goToUrl(url);
    }
}
