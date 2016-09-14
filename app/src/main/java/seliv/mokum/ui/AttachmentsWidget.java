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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nguyenhoanglam.imagepicker.activity.ImagePicker;

/**
 * Created by aselivanov on 9/15/2016.
 */
public class AttachmentsWidget extends LinearLayout {
    private Button addButton;

    public AttachmentsWidget(Context context) {
        super(context);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f);
        LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        setLayoutParams(paramsFill);
        setOrientation(LinearLayout.HORIZONTAL);

        addButton = new Button(context);
        addButton.setText("Add attachments");
        addButton.setLayoutParams(params);

        addView(addButton);

        addButton.setOnClickListener(new OnClickListener() {
            private int REQUEST_CODE_PICKER = 2000;

            @Override
            public void onClick(View v) {
                ImagePicker.create((Activity) getContext())
                        .folderMode(true) // folder mode (false by default)
                        .folderTitle("Folder") // folder selection title
                        .imageTitle("Tap to select") // image selection title
                        .single() // single mode
                        .multi() // multi mode (default mode)
                        .limit(10) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                        .origin(images) // original selected images, used in multi mode
                        .start(REQUEST_CODE_PICKER); // start image picker activity with request code
            }
        });
    }
}
