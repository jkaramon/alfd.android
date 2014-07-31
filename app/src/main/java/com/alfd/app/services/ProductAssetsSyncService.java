package com.alfd.app.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alfd.app.ImgSize;
import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.data.Family;
import com.alfd.app.data.SyncInfo;
import com.alfd.app.rest.PagedResult;
import com.alfd.app.rest.Product;
import com.alfd.app.rest.ProductEndpoints;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.rest.User;
import com.alfd.app.rest.UserEndpoints;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.Settings;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 * Moves temporary Product files (images, voice notes) to final place.
 */
public class ProductAssetsSyncService extends BaseIntentService {

    private final ProductEndpoints productSvc;
    private RestAdapter restAdapter;

    private com.alfd.app.data.Product product;
    private DateTime lastSyncDate;

    public ProductAssetsSyncService() {

        super(ProductAssetsSyncService.class.getName());
        RESTServer restServer = new RESTServer();
        restAdapter = restServer.getAdapter();
        productSvc = restAdapter.create(ProductEndpoints.class);
    }

    public static Intent createStartIntent(Context ctx, String barCode, String barType) {
        Intent i = new Intent(ctx, ProductAssetsSyncService.class);

        i.putExtra(SC.BAR_CODE, barCode);
        i.putExtra(SC.BAR_TYPE, barType);
        return i;
    }

    @Override
    protected Intent createIntent(String category) {
        Intent localIntent = new Intent(Services.PRODUCT_ASSETS_SYNC);
        localIntent.addCategory(category);
        return localIntent;
    }


    @Override
    protected void preExecute(Intent workIntent) {
        String barCode = workIntent.getStringExtra(SC.BAR_CODE);
        product = com.alfd.app.data.Product.findByBarCode(barCode);
        SyncInfo syncInfo = SyncInfo.get();
        syncInfo.setSyncStarted();
        lastSyncDate = syncInfo.LastSyncDate;
    }

    public void execute() {
        if (product == null) {
            return;
        }
        downloadProductImages();
        downloadVoiceNotes();

        uploadVoiceNotes();
        uploadProductImages();

    }

    private void uploadProductImages() {
        File[] files = FileHelpers.getImageFilesToUpload(this);
        for (File f : files) {
            try {
                uploadProductImage(f);
                f.delete();
            } catch (Exception e){
                Log.wtf("SYNC", e);
                if (shouldDeleteFile(e)) {
                    f.delete();
                }
                continue;
            }

        }

    }


    private void uploadVoiceNotes() {
        File[] files = FileHelpers.getVoiceNoteFilesToUpload(this);
        for (File f : files) {
            try {
                uploadVoiceNote(f);
                f.delete();
            } catch (Exception e){
                Log.wtf("SYNC", e);
                if (shouldDeleteFile(e)) {
                    f.delete();
                }
                continue;
            }
        }


    }

    private boolean shouldDeleteFile(Exception e) {
        if (e instanceof RetrofitError) {
            RetrofitError error = (RetrofitError)e;
            Response response = error.getResponse();
            if (response != null) {
                int status = response.getStatus();
                if (status == 400) {
                    return true;
                }
            }


        }
        return false;
    }
    private void uploadProductImage(File f) {
        String[] parts = f.getName().split("\\.");
        if (parts.length!= 4) {
            throw new IllegalArgumentException("Error while parsing product image file name - " + f.getName());
        }
        String barCode = parts[0];
        String barType = parts[1];
        String uniqueId = parts[2];
        TypedFile typedFile = new TypedFile("image/jpeg", f);
        productSvc.uploadProductImage(barCode, uniqueId, typedFile);

    }

    private void uploadVoiceNote(File f) {
        String[] parts = f.getName().split("\\.");
        if (parts.length!= 4) {
            throw new IllegalArgumentException("Error while parsing voice note file name - " + f.getName());
        }
        String barCode = parts[0];
        String barType = parts[1];
        String uniqueId = parts[2];
        TypedFile typedFile = new TypedFile("audio/mp4", f);
        productSvc.uploadVoiceNote(barCode, uniqueId, typedFile);
    }

    private void downloadVoiceNotes() {
        PagedResult<String> voiceNoteNames = productSvc.getVoiceNotes(product.BarCode, lastSyncDate);
        for (String voiceNoteName : voiceNoteNames.items) {
            if (FileHelpers.voiceNoteExists(this, product.BarCode, product.BarType, voiceNoteName)) {
                continue;
            }
            Response response = productSvc.getVoiceNote(product.BarCode, voiceNoteName);
            TypedInput ti = response.getBody();
            try {

                FileHelpers.createVoiceNoteFromStream(this, ti.in(), voiceNoteName, product.BarCode, product.BarType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadProductImages() {


        PagedResult<String> imageNames = productSvc.getProductImages(product.BarCode, lastSyncDate);
        for (String imgName : imageNames.items) {
            if (FileHelpers.productImageExists(this, product.BarCode, product.BarType, imgName)) {
                continue;
            }
            downloadProductImage(imgName, ImgSize.SMALL);
        }
    }

    private void downloadProductImage(String imgName, ImgSize imgSize) {

        String imageSizeName = FileHelpers.getImageSizeName(imgSize);
        Response response = productSvc.getProductImage(product.BarCode, imgName, imageSizeName);
        TypedInput ti = response.getBody();
        try {
            FileHelpers.createProductImageFromStream(this, ti.in(), imgName, imgSize, product.BarCode, product.BarType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void onFailure(Exception e) {
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