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
import android.widget.RatingBar;
import android.widget.TextView;

import com.alfd.app.ImgSize;
import com.alfd.app.R;
import com.alfd.app.data.Product;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.utils.ImageResizer;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * This class provides an example of custom card with a custom inner layout.
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ProductCard extends Card {

    private Activity activity;
    protected TextView txtDescription;
    private String name;
    private String barCode;
    private String barType;
    private String description;
    private ImageView ivwProductImage;
    private ImageResizer imageWorker;

    public ProductCard(Activity activity) {
        this(activity, R.layout.product_card_inner_content);
        this.activity = activity;

        this.setShadow(false);
    }

    /**
     * @param context
     * @param innerLayout
     */
    public ProductCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }



    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        txtDescription = (TextView) view.findViewById(R.id.product_card_inner_description);
        ivwProductImage = (ImageView)view.findViewById(R.id.product_image);
        imageWorker = new ImageResizer(activity, ImgSize.SMALL);

        if (description != null) {
            txtDescription.setText(description);
        }
        if (barCode != null) {
            imageWorker.loadImage(Product.getPrimaryPhoto(activity, barCode, barType), ivwProductImage);
        }
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                Intent i = IntentFactory.navigateProduct(getContext(), getIdLong());
                activity.startActivity(i);
            }
        });


    }

    public long getIdLong() {
        return Long.parseLong(getId(), 10);
    }

    public void setFromCursor(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(Columns.NAME));
        barCode = cursor.getString(cursor.getColumnIndex(Columns.BAR_CODE));
        barType = cursor.getString(cursor.getColumnIndex(Columns.BAR_TYPE));
        description = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION));



        this.setId("" + cursor.getInt(cursor.getColumnIndex(Columns.ID)));


    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public static class Columns {
        public static String ID = "Id";
        public static String NAME = "Name";
        public static String BAR_CODE = "BarCode";
        public static String BAR_TYPE = "BarType";
        public static String DESCRIPTION = "Description";
    }
}