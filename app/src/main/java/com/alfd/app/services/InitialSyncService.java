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
       
    }

    public void execute() {
        syncFamilyUsers();
        syncProducts();
    }


    private void syncProducts() {
        PagedResult<Product> products = productSvc.getProducts();
        for (Product p : products.items) {
            com.alfd.app.data.Product sqlProduct = com.alfd.app.data.Product.getByBarCode(p.barCode);
            if (sqlProduct == null) {
                sqlProduct = com.alfd.app.data.Product.fromREST(p);
            }
            sqlProduct.sync(p);

        }
    }

    private void syncFamilyUsers() {
        PagedResult<User> users = usersSvc.myFamilyUsers();
        for (User u : users.items) {
            String login = u.google.email;
            com.alfd.app.data.User sqlUser = com.alfd.app.data.User.getByGoogleAccountName(login);
            if (sqlUser == null) {
                sqlUser = com.alfd.app.data.User.fromREST(u);
            }
            sqlUser.sync(u);
        }
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
        SyncInfo si = new SyncInfo();
        si.LastSyncDate = DateTime.now();
        si.LastInitialSyncDate = DateTime.now();
        return si;

    }
}