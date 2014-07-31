package com.alfd.app.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.alfd.app.Services;
import com.alfd.app.data.SyncInfo;
import com.alfd.app.rest.PagedResult;
import com.alfd.app.rest.Product;
import com.alfd.app.rest.ProductEndpoints;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.rest.User;
import com.alfd.app.rest.UserEndpoints;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Moves temporary Product files (images, voice notes) to final place.
 */
public class InitialSyncService extends BaseIntentService {

    private final ProductEndpoints productSvc;
    private UserEndpoints usersSvc;
    private RestAdapter restAdapter;

    public InitialSyncService() {

        super(InitialSyncService.class.getName());
        RESTServer restServer = new RESTServer();
        restAdapter = restServer.getAdapter();
        usersSvc = restAdapter.create(UserEndpoints.class);
        productSvc = restAdapter.create(ProductEndpoints.class);
    }

    @Override
    protected Intent createIntent(String category) {
        Intent localIntent = new Intent(Services.INITIAL_SYNC);
        localIntent.addCategory(category);
        return localIntent;
    }


    @Override
    protected void preExecute(Intent workIntent) {
       SyncInfo.get().setInitialSyncStarted();
    }

    public void execute() {

    }




    protected void onFailure(Exception e) {
        Intent localIntent = createIntent(Services.STATUS_FAILURE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        SyncInfo si = getSyncInfo();
        si.setInitialSyncFailed();

    }

  

    protected void onSuccess() {
        Intent localIntent = createIntent(Services.STATUS_SUCCESS);
        SyncInfo si = getSyncInfo();
        si.setInitialSyncDone();
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private SyncInfo getSyncInfo() {
        SyncInfo si = SyncInfo.get();
        si.LastInitialSyncDate = DateTime.now();
        return si;

    }
}