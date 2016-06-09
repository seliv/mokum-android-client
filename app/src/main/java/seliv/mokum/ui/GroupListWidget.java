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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import seliv.mokum.api.model.Group;

/**
 * Created by aselivanov on 5/21/2016.
 */
public class GroupListWidget extends LinearLayout {
    private final List<Group> groups;

    private TextView toLabel;
    private Button addButton;
    private LinearLayout groupsView;

    public GroupListWidget(Context context, List<Group> groups) {
        super(context);
        this.groups = Collections.unmodifiableList(groups);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0f);
        LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        setLayoutParams(paramsFill);
        setOrientation(LinearLayout.HORIZONTAL);
        toLabel = new TextView(context);
        toLabel.setText("To: ");
        toLabel.setLayoutParams(params);

        groupsView = new LinearLayout(context);
        groupsView.setOrientation(LinearLayout.VERTICAL);
        groupsView.setLayoutParams(paramsFill);

        addButton = new Button(context);
        addButton.setText("+");
        addButton.setLayoutParams(params);

        addView(toLabel);
        addView(groupsView);
        addView(addButton);

        GroupLineWidget groupLineWidget = new GroupLineWidget(context, groups);
        groupLineWidget.setName("Test Group");
        groupsView.addView(groupLineWidget);

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setVisibility(View.GONE); // Invisible until group list is initialized
    }

    public void setGroups(List<Group> groups) {
        if (groups.size() > 0) {
            setVisibility(View.VISIBLE);
        }
    }

    private void addGroupLine() {

    }

    private static class GroupLineWidget extends LinearLayout {
        ArrayAdapter<String> adapter;
        private Button removeButton;
        private Spinner nameView;

        public GroupLineWidget(Context context, List<Group> groups) {
            super(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsFill = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            setLayoutParams(paramsFill);
            setOrientation(LinearLayout.HORIZONTAL);
            removeButton = new Button(context);
            removeButton.setText("X");
            removeButton.setLayoutParams(params);
            nameView = new Spinner(context);
//            nameView.setText("");
            nameView.setLayoutParams(paramsFill);
            addView(removeButton);
            addView(nameView);
//            nameView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    nameView.showDropDown();
//                }
//            });

            ArrayList<String> groupNames = new ArrayList<>(groups.size());
            for (Group group : groups) {
                groupNames.add(group.getName());
            }
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, groupNames); // new String[]{"First", "Second", "Test Group", "Third", "Fourth"}
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameView.setAdapter(adapter);
        }

        public void setName(String name) {
            if (name != null) {
                int spinnerPosition = adapter.getPosition(name);
                if (spinnerPosition >= 0) {
                    nameView.setSelection(spinnerPosition);
                }
            }
        }
    }
}
