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
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfd.app.BuildConfig;
import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.ImageCache;
import com.alfd.app.utils.ImageLoader;
import com.alfd.app.utils.ImageResizer;
import com.alfd.app.utils.ImageWorker;
import com.alfd.app.utils.Utils;

import java.io.File;

public class ImageDetailActivity extends Activity {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

    private ImageLoader imageWorker;
    File imageFile;
    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.image_detail);
        super.onCreate(savedInstanceState);
        fillImageFile(savedInstanceState);
        imageWorker = new ImageLoader(this);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageWorker.loadImage(imageFile, imageView);
    }
    private void fillImageFile(Bundle savedInstanceState) {
        String filename;
        if (savedInstanceState == null) {
            filename = getIntent().getStringExtra(SC.IMAGE_FULL_NAME);
        }
        else {
            filename = savedInstanceState.getString(SC.IMAGE_FULL_NAME);
        }
        imageFile = new File(filename);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SC.IMAGE_FULL_NAME, imageFile.getAbsolutePath());
    }

    @Override
    public void onResume() {
        super.onResume();
        imageWorker.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageWorker.setExitTasksEarly(true);
        imageWorker.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageWorker.closeCache();
    }







}
