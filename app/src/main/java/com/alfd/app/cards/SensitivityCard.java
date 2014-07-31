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
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfd.app.R;
import com.alfd.app.data.Product;
import com.alfd.app.data.Sensitivity;
import com.alfd.app.data.User;
import com.alfd.app.intents.IntentFactory;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * This class provides an example of custom card with a custom inner layout.
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class SensitivityCard extends Card {

    private Activity activity;
    private User user;
    private Product product;
    private Sensitivity sensitivity;
    private TextView txtDisplayName;
    private ImageView imgAvatar;
    private ImageView imgLevel;


    public SensitivityCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public SensitivityCard(Activity activity, User user, Product product, Sensitivity sensitivity) {
        this(activity, R.layout.sensitivity_card_inner_content);
        this.activity = activity;
        this.user = user;
        this.product = product;
        this.sensitivity = sensitivity;
    }
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        txtDisplayName = (TextView) parent.findViewById(R.id.display_name);
        imgAvatar = (ImageView) view.findViewById(R.id.avatar);
        imgLevel = (ImageView) view.findViewById(R.id.level);


        txtDisplayName.setText(user.DisplayName);
        if (user.Avatar != null) {
            imgAvatar.setImageResource(user.getAvatarResourceId(activity));
        }
        if (sensitivity != null) {
            imgLevel.setImageResource(Sensitivity.getLevelResourceId(activity, sensitivity.Level));
        }


        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                Toast.makeText(activity, "sss", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public long getIdLong() {
        return Long.parseLong(getId(), 10);
    }



}