package com.alfd.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.alfd.app.LoginErrorCodes;
import com.alfd.app.data.User;
import com.alfd.app.rest.LoginOrJoinRequest;
import com.alfd.app.rest.ProductNameCache;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.rest.SuggestProductNameRequest;
import com.alfd.app.services.Sync;
import com.alfd.app.utils.Settings;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.Status;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfd.app.LogTags;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.activities.ActivityHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.IOException;
import java.io.InputStream;


public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    /* Client used to interact with Google APIs. */

    protected GoogleApiClient googleApiClient;

    private SpiceManager spiceManager = new SpiceManager(RESTServer.class);

    private ConnectionResult connectionResult;

    private LoginOrJoinRequest loginOrJoinRequest;

    private Sync sync;
    private boolean gPlusIntentInProgress;
    private boolean signInClicked;

    private SignInButton btnSignIn;

    private String accessToken;
    private TextView subtitle;
    private ViewGroup progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sync = new Sync();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setSize(SignInButton.SIZE_WIDE);
        subtitle = (TextView)findViewById(R.id.subtitle);
        progressBarLayout = (ViewGroup)findViewById(R.id.login_progress);
        hideLoginProgress();
        // Button click listeners
        btnSignIn.setOnClickListener(this);
    }




    @Override
    protected void onStart() {
        googleApiClient.connect();
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }


        if (!gPlusIntentInProgress) {
            // Store the ConnectionResult for later usage
            connectionResult = result;

            if (signInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }


    private void showLoginProgress() {
        progressBarLayout.setVisibility(View.VISIBLE);
        subtitle.setVisibility(View.GONE);
    }
    private void hideLoginProgress() {
        progressBarLayout.setVisibility(View.GONE);
        subtitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        getToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.GPLUS_SIGNIN) {
            gPlusIntentInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }



    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                gPlusIntentInProgress = true;
                connectionResult.startResolutionForResult(this, RequestCodes.GPLUS_SIGNIN);
            } catch (IntentSender.SendIntentException e) {
                gPlusIntentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            showLoginProgress();

        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            hideLoginProgress();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked
                signInWithGplus();
                updateUI(true);
                break;

        }


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
        }

    }
    /**
     * Sign-in into google
     * */
    protected void signInWithGplus() {
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultAndFinish(RESULT_CANCELED);
    }

    /**
     * Sign-out from google
     * */
    protected void signOutFromGplus() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();

        }
    }
    private void getToken() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                accessToken = null;
                String scope = "audience:server:client_id:215330885955-3r9oh088v2t3nqd8ii8s6r88rss8ckja.apps.googleusercontent.com";
                try {
                    return GoogleAuthUtil.getToken(LoginActivity.this, Plus.AccountApi.getAccountName(googleApiClient), scope);

                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.

                } catch (UserRecoverableAuthException e) {
                    // Recover
                    accessToken = null;
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(String token) {
                accessToken = token;
                Settings.setAccessToken(accessToken);
                Log.i(LogTags.DBG, "Access token retrieved:" + token);
                syncUser();
            }

        };
        task.execute();
    }

    private void syncUser() {
        String googleAccountName = User.getGoogleAccountName(googleApiClient);
        User u = User.getByGoogleAccountName(googleAccountName);
        if (u == null) {
            loginOrRegisterUser();

        }
        else {
            u.updateFromGplusProfile(googleApiClient);
            u.GoogleAccessToken = accessToken;
            u.LoggedIn = true;
            u.saveWithCallbacks();
            setResultAndFinish(RESULT_OK);
        }


    }

    private void loginOrRegisterUser() {
        User u = User.createFromGplusProfile(googleApiClient);
        loginOrJoinRequest = new LoginOrJoinRequest(u.toREST());

        getSpiceManager().execute(loginOrJoinRequest, new LoginOrJoinRequestListener());
    }

    private void setResultAndFinish(int result) {
        Intent i = getIntent();
        setResult(result, i);
        finish();
    }

    private class LoginOrJoinRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.alfd.app.rest.User> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(LoginActivity.this, "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT).show();
            setResultAndFinish(LoginErrorCodes.NETWORK_ERROR);
        }

        @Override
        public void onRequestSuccess(com.alfd.app.rest.User restUser) {
            sync.syncCurrentUser(restUser, accessToken);
            setResultAndFinish(RESULT_OK);

        }
    }

}
