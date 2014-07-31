package com.alfd.app.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.alfd.app.Services;
import com.alfd.app.data.Family;
import com.alfd.app.data.Sensitivity;
import com.alfd.app.data.SyncInfo;
import com.alfd.app.rest.PagedResult;
import com.alfd.app.rest.Product;
import com.alfd.app.rest.ProductEndpoints;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.rest.SensitivityEndpoints;
import com.alfd.app.rest.User;
import com.alfd.app.rest.UserEndpoints;
import com.alfd.app.utils.Settings;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Moves temporary Product files (images, voice notes) to final place.
 */
public class SyncService extends BaseIntentService {

    private final ProductEndpoints productSvc;
    private final SensitivityEndpoints sensitivitySvc;
    private UserEndpoints usersSvc;
    private RestAdapter restAdapter;
    private DateTime lastSyncDate;

    public SyncService() {

        super(SyncService.class.getName());
        RESTServer restServer = new RESTServer();
        restAdapter = restServer.getAdapter();
        usersSvc = restAdapter.create(UserEndpoints.class);
        productSvc = restAdapter.create(ProductEndpoints.class);
        sensitivitySvc = restAdapter.create(SensitivityEndpoints.class);

    }

    @Override
    protected Intent createIntent(String category) {
        Intent localIntent = new Intent(Services.SYNC);
        localIntent.addCategory(category);
        return localIntent;
    }


    @Override
    protected void preExecute(Intent workIntent) {

        SyncInfo syncInfo = SyncInfo.get();
        syncInfo.setSyncStarted();
        lastSyncDate = syncInfo.LastSyncDate;
    }

    public void execute() {
        uploadProducts();
        downloadFamilyUsers();
        downloadProducts();
        uploadSensitivities();
        downloadSensitivities();
    }

    private void uploadSensitivities() {
        List<Sensitivity> unsyncedSensitivities = Sensitivity.getUnsynced(Sensitivity.class);
        for(Sensitivity s : unsyncedSensitivities) {
            com.alfd.app.rest.Sensitivity restSensitivity = s.toREST();
            if (restSensitivity.userId == null || restSensitivity.productId == null) {
                continue;
            }
            com.alfd.app.rest.Sensitivity result;
            result = sensitivitySvc.upsertSensitivity(restSensitivity);
            s.sync(result);
        }
    }

    private void downloadSensitivities() {
        PagedResult<com.alfd.app.rest.Sensitivity> sensitivities = sensitivitySvc.getSensitivities(lastSyncDate);
        for (com.alfd.app.rest.Sensitivity restSensitivity : sensitivities.items) {
            Sensitivity sqlSensitivity = Sensitivity.getByServerId(Sensitivity.class, restSensitivity.id);
            if (sqlSensitivity == null) {
                sqlSensitivity = Sensitivity.fromREST(restSensitivity);
            }
            sqlSensitivity.sync(restSensitivity);
        }
    }

    private void uploadProducts() {
        List<com.alfd.app.data.Product> unsyncedProducts = com.alfd.app.data.Product.getUnsynced(com.alfd.app.data.Product.class);
        for(com.alfd.app.data.Product p : unsyncedProducts) {
            Product restProduct = p.toREST();
            Product result;
            if (p.wasSynced()) {
                result = updateProduct(restProduct);
            }
            else {
                result = insertProduct(restProduct);
            }
            p.sync(result);
        }
    }

    private Product insertProduct(Product restProduct) {
        return productSvc.insertProduct(restProduct);
    }

    private Product updateProduct(Product restProduct) {
        return productSvc.updateProduct(restProduct.barCode, restProduct);
    }


    private void downloadProducts() {
        PagedResult<Product> products = productSvc.getProducts(lastSyncDate);
        for (Product p : products.items) {
            com.alfd.app.data.Product sqlProduct = com.alfd.app.data.Product.getByBarCode(p.barCode);
            if (sqlProduct == null) {
                sqlProduct = com.alfd.app.data.Product.fromREST(p);
            }
            sqlProduct.sync(p);
            Intent assetsSyncServiceIntent = ProductAssetsSyncService.createStartIntent(this, p.barCode, p.barType);
            startService(assetsSyncServiceIntent);

        }
    }

    private void downloadFamilyUsers() {
        PagedResult<User> users = usersSvc.myFamilyUsers();
        for (User u : users.items) {
            String serverId = u.id;
            com.alfd.app.data.User sqlUser = com.alfd.app.data.User.getByServerId(com.alfd.app.data.User.class, serverId);
            if (sqlUser == null) {
                sqlUser = com.alfd.app.data.User.fromREST(u);
                sqlUser.FamilyId = Family.getByServerId(Family.class, u.familyId).getId();
            }
            sqlUser.sync(u);
        }
    }


    protected void onFailure(Exception e) {
        if (e instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) e;
            Response response = error.getResponse();
            if (response != null) {
                int status = response.getStatus();
                if (status == 401) {
                    Settings.clearAccessToken();
                }
            }
            else if (e.getMessage().contains("authentication")) {
                Settings.clearAccessToken();
            }
        }
        Intent localIntent = createIntent(Services.STATUS_FAILURE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        SyncInfo si = getSyncInfo();
        si.setSyncFailed();

    }

  

    protected void onSuccess() {
        Intent localIntent = createIntent(Services.STATUS_SUCCESS);
        SyncInfo si = getSyncInfo();
        si.setSyncDone();
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private SyncInfo getSyncInfo() {
        SyncInfo si = SyncInfo.get();
        si.LastSyncDate = DateTime.now();
        return si;

    }
}