package com.alfd.app.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.data.Product;

/**
 * Moves temporary Product files (images, voice notes) to final place.
 */
public class MoveTempProductFilesService extends BaseIntentService {

    Product product;
    private Product.MoveTempFilesResult result;

    public MoveTempProductFilesService() {
        super(MoveTempProductFilesService.class.getName());
    }

    @Override
    protected Intent createIntent(String category) {
        Intent localIntent = new Intent(Services.MOVE_TEMP_FILES);
        localIntent.addCategory(category);
        return localIntent;
    }


    @Override
    protected void preExecute(Intent workIntent) {
        product = new Product();
        product.BarCode = workIntent.getStringExtra(SC.BAR_CODE);
        product.BarType = workIntent.getStringExtra(SC.BAR_TYPE);
    }

    public void execute() {

        result = product.moveTempFiles(this);
        Intent assetsSyncServiceIntent = ProductAssetsSyncService.createStartIntent(this, product.BarCode, product.BarType);
        startService(assetsSyncServiceIntent);
    }



    protected void onFailure(Exception e) {
        Intent localIntent = createIntent(Services.STATUS_FAILURE);
        setIntentData(localIntent);
        localIntent.putExtra(SC.EXCEPTION, e.toString());

        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private void setIntentData(Intent localIntent) {
        localIntent.putExtra(SC.BAR_CODE, product.BarCode)
            .putExtra(SC.BAR_TYPE, product.BarType)
            .putExtra(SC.IMAGES_MOVED, result.movedImageFiles.length)
            .putExtra(SC.VOICE_NOTES_MOVED, result.movedVoiceNoteFiles.length);
    }


    protected void onSuccess() {
        Intent localIntent = createIntent(Services.STATUS_SUCCESS);
        setIntentData(localIntent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }
}