package com.alfd.app.activities;

import android.support.v7.app.ActionBarActivity;

import com.alfd.app.rest.RESTServer;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by karamon on 25. 4. 2014.
 */
public class BaseActionBarActivity extends ActionBarActivity {
    private SpiceManager spiceManager = new SpiceManager(RESTServer.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    protected void onPostResume() {
        ActivityHelper.setFonts(this);
        super.onPostResume();
    }
}
