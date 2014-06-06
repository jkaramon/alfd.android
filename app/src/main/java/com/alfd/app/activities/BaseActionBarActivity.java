package com.alfd.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by karamon on 25. 4. 2014.
 */
public class BaseActionBarActivity extends ActionBarActivity {


    @Override
    protected void onPostResume() {
        ActivityHelper.setFonts(this);
        super.onPostResume();
    }
}
