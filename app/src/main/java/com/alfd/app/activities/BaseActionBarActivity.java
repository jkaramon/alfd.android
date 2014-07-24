package com.alfd.app.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfd.app.LogTags;
import com.alfd.app.RequestCodes;
import com.alfd.app.data.User;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.services.Sync;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.SpiceManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by karamon on 25. 4. 2014.
 */
public class BaseActionBarActivity extends ActionBarActivity  {
    private SpiceManager spiceManager = new SpiceManager(RESTServer.class);
    private Sync sync = new Sync();



    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
        if (User.needSignin()) {
            startActivityForResult(new Intent(this, LoginActivity.class), RequestCodes.PROCESS_SIGNIN);
        }
        else {
            sync.ensureInitialSync(this);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.PROCESS_SIGNIN) {
            if (resultCode == RESULT_OK) {
                recreate();
                return;
            } else {
                Toast.makeText(this, "Error while trying to sign in. Make sure you are online.", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






}
