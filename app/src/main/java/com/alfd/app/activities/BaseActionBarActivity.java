package com.alfd.app.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfd.app.LogTags;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.data.User;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.services.Sync;
import com.alfd.app.utils.Settings;
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
public class BaseActionBarActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private SpiceManager spiceManager = new SpiceManager(RESTServer.class);
    protected Sync sync = new Sync();
    private GoogleApiClient googleApiClient;
    private GPlusAction gPlusAction = GPlusAction.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
        if (User.needSignin()) {
            Settings.clearAccessToken();
            startActivityForResult(new Intent(this, LoginActivity.class), RequestCodes.PROCESS_SIGNIN);
        }
        else {
            //sync.ensureInitialSync(this);
            sync.sync(this, true);
        }

    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (result != null && result.hasResolution()) {
            try {
                result.startResolutionForResult(this, RequestCodes.GPLUS_SIGNIN);
            } catch (IntentSender.SendIntentException e) {

                googleApiClient.connect();
            }
        }

    }
    @Override
    public void onConnected(Bundle bundle) {
        processGPlusAction();
    }

    public void onConnectionSuspended(int i) {

        googleApiClient.connect();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    /**
     * Revoking access from google
     * */
    protected void revokeGplusAccess() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(LogTags.DBG, "User access revoked!");
                            googleApiClient.connect();

                        }

                    });
            gPlusAction = GPlusAction.NONE;
        }

        Settings.clearAccessToken();
        recreate();

    }




    /**
     * Sign-out from google
     * */
    protected void signOutFromGplus() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
            gPlusAction = GPlusAction.NONE;
        }
        Settings.clearAccessToken();
        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                connectToGPlusAndSignout();
                return true;
            case R.id.action_revoke:
                connectToGPlusAndRevokeAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void connectToGPlusAndRevokeAccount() {
        gPlusAction = GPlusAction.REVOKE;
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            revokeGplusAccess();
        }
    }

    private void connectToGPlusAndSignout() {
        gPlusAction = GPlusAction.SIGNOUT;
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            signOutFromGplus();
        }
    }

    @Override
    protected void onPostResume() {
        ActivityHelper.setFonts(this);
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.PROCESS_SIGNIN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
                return;
            }
            if (resultCode == RESULT_OK) {
                recreate();
                return;
            } else {
                Toast.makeText(this, "Error while trying to sign in. Make sure you are online.", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == RequestCodes.GPLUS_SIGNIN) {
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
                processGPlusAction();

            }
            gPlusAction = GPlusAction.NONE;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processGPlusAction() {
        switch (gPlusAction) {
            case SIGNOUT:
                signOutFromGplus();
                break;
            case REVOKE:
                revokeGplusAccess();
                break;
        }
    }


}
