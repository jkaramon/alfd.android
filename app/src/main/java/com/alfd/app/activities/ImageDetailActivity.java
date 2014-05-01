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
import android.widget.Toast;

import com.alfd.app.BuildConfig;
import com.alfd.app.R;
import com.alfd.app.utils.ImageCache;
import com.alfd.app.utils.ImageResizer;
import com.alfd.app.utils.Utils;

public class ImageDetailActivity extends FragmentActivity  {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

    private ImageResizer imageWorker;

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {


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
