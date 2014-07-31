/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.alfd.app.cards;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfd.app.R;
import com.alfd.app.data.Product;
import com.alfd.app.data.Sensitivity;
import com.alfd.app.data.User;

import java.util.HashMap;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * This class provides an example of custom card with a custom inner layout.
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class EditSensitivityCard extends Card {

    private Activity activity;
    private User user;
    private Product product;
    private Sensitivity sensitivity;
    private TextView txtDisplayName;
    private ImageView imgAvatar;
    private ImageView imgLevel;
    private ImageView imgLevelUnknown;

    private ImageView imgLevelNone;

    private ImageView imgLevelLow;

    private ImageView imgLevelHigh;

    private EditText txtNote;


    private HashMap<String, ImageView> levels;

    public EditSensitivityCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public EditSensitivityCard(Activity activity, User user, Product product, Sensitivity sensitivity) {
        this(activity, R.layout.edit_sensitivity_card_inner_content);
        this.activity = activity;
        this.user = user;
        this.product = product;
        this.sensitivity = sensitivity;
        this.setId(user.getId().toString() + "_" + product.getId().toString());
    }
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        if (txtDisplayName != null) {
            return;
        }
        txtDisplayName = (TextView) view.findViewById(R.id.display_name);
        txtNote = (EditText) view.findViewById(R.id.note);
        txtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                sensitivity.Note = charSequence.toString();
                sensitivity.saveWithCallbacks();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgAvatar = (ImageView) view.findViewById(R.id.avatar);
        imgLevel = (ImageView) view.findViewById(R.id.level);

        imgLevelUnknown = (ImageView) view.findViewById(R.id.btn_unknown);
        imgLevelNone = (ImageView) view.findViewById(R.id.btn_none);
        imgLevelLow = (ImageView) view.findViewById(R.id.btn_low);
        imgLevelHigh = (ImageView) view.findViewById(R.id.btn_high);

        if (levels == null) {
            levels = new HashMap<String, ImageView>();
            levels.put(Sensitivity.Levels.UNKNOWN, imgLevelUnknown);
            levels.put(Sensitivity.Levels.NONE, imgLevelNone);
            levels.put(Sensitivity.Levels.LOW, imgLevelLow);
            levels.put(Sensitivity.Levels.HIGH, imgLevelHigh);

            for (Map.Entry<String, ImageView> entry : levels.entrySet()) {
                ImageView iv = entry.getValue();
                iv.setTag(entry.getKey());

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String level = (String)view.getTag();
                        if (level == null) {
                            return;
                        }
                        updateSensitivityLevel(level);
                        bindLevel();
                    }
                });

            }

        }

        bindLevel();

        txtDisplayName.setText(user.DisplayName);
        if (user.Avatar != null) {
            imgAvatar.setImageResource(user.getAvatarResourceId(activity));
        }
        if (sensitivity != null) {
            txtNote.setText(sensitivity.Note);
        }



    }

    public void updateSensitivityLevel(String level) {
        sensitivity.Level = level;
        if (level != Sensitivity.Levels.UNKNOWN || sensitivity.isNew() == false) {
            sensitivity.saveWithCallbacks();
        }
    }

    public void bindLevel() {
        switchOffLevelButtons();
        String level = sensitivity.Level;
        imgLevel.setImageResource(Sensitivity.getLevelResourceId(activity, level));
        switchOnLevelButton(level);


    }

    private void switchOffLevelButtons() {
        for (ImageView levelView : levels.values()) {
            switchOffLevelButton(levelView);
        }
    }
    private void switchOffLevelButton(ImageView iv) {
        iv.setAlpha(0.3F);
    }
    private void switchOnLevelButton(String level) {
        ImageView iv = levels.get(level);
        iv.setAlpha(1F);
    }


    public long getIdLong() {
        return Long.parseLong(getId(), 10);
    }



}