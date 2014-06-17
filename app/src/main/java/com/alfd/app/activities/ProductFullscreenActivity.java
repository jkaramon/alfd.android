/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alfd.app.activities;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.activities.fragments.ProductFullPhotoFragment;
import com.alfd.app.adapters.ProductFullScreenPageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductFullScreenActivity extends BaseProductActivity {


    private ProductFullScreenPageAdapter pageAdapter;
    private int currentItemPosition = 0;
    private String currentFileName;
    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_full_screen);
        currentFileName = getIntent().getStringExtra(SC.IMAGE_FULL_NAME);


        List<ProductFullPhotoFragment> fragments = getFragments();
        pageAdapter = new ProductFullScreenPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);

        pager.setCurrentItem(currentItemPosition);
    }



    private List<ProductFullPhotoFragment> getFragments() {
        List<ProductFullPhotoFragment> fragments = new ArrayList<ProductFullPhotoFragment>();
        iterateFiles(fragments, ProductImageTypes.OVERVIEW);
        iterateFiles(fragments, ProductImageTypes.INGREDIENTS);
        return fragments;
    }

    private void iterateFiles(List<ProductFullPhotoFragment> fragments, String imageType) {
        for (File file : getImageFiles(imageType)) {
            ProductFullPhotoFragment fragment = ProductFullPhotoFragment.newInstance(file);
            if (file.getAbsolutePath().equalsIgnoreCase(currentFileName)) {
                currentItemPosition = fragments.size();
            }
            fragments.add(fragment);
        }
    }


}
