package com.alfd.app.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.alfd.app.SC;
import com.alfd.app.activities.fragments.ProductNameFragment;
import com.alfd.app.activities.fragments.VoiceNotesFragment;
import com.alfd.app.interfaces.OnPhotoInteractionListener;
import com.alfd.app.data.Product;
import com.alfd.app.tasks.MoveTempProductFiles;
import com.alfd.app.utils.FileHelpers;

import java.io.File;

/**
 * Created by karamon on 2. 5. 2014.
 */
public class BaseProductActivity extends BaseActionBarActivity implements OnPhotoInteractionListener, VoiceNotesFragment.OnFragmentInteractionListener, ProductNameFragment.OnFragmentInteractionListener {
    protected Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fillProduct(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    private void fillProduct(Bundle savedInstanceState) {

        product = new Product();
        if (savedInstanceState == null) {
            Intent i = getIntent();
            long productId = i.getLongExtra(SC.PRODUCT_ID, -1);
            if (productId != -1) {
                product = Product.load(Product.class, productId);
            }
            else {
                product.BarCode = i.getStringExtra(SC.BAR_CODE);
                product.BarType = i.getStringExtra(SC.BAR_TYPE);
            }
        }
        else {
            long productId = savedInstanceState.getLong(SC.PRODUCT_ID, -1);
            if (productId != -1) {
                product = Product.load(Product.class, productId);
            }
            else {
                product.BarCode = savedInstanceState.getString(SC.BAR_CODE);
                product.BarType = savedInstanceState.getString(SC.BAR_TYPE);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (product.isNew()) {
            savedInstanceState.putString(SC.BAR_CODE, product.BarCode);
            savedInstanceState.putString(SC.BAR_TYPE, product.BarType);
        }
        else {
            savedInstanceState.putLong(SC.PRODUCT_ID, product.getId());
        }
    }





    @Override
    public File getTempFileToSave(String imageType) {
        return FileHelpers.createTempProductImageFile(this, imageType, product.BarCode);
    }

    @Override
    public File[] getImageFiles(String imageType) {
        if (product.isNew()) {
            return FileHelpers.getProductImageTempFiles(this, imageType, product.BarCode);
        }
        else {
            // TODO: Implement!
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void showFullScreenDetail(File currentImage, ActivityOptions options) {
        final Intent i = new Intent(this, ProductFullScreenActivity.class);
        i.putExtra(SC.IMAGE_FULL_NAME, currentImage.getAbsolutePath());
        if (product.isNew()) {
            i.putExtra(SC.BAR_CODE, product.BarCode);
            i.putExtra(SC.BAR_TYPE, product.BarType);
        }

        if (options != null) {
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }



    @Override
    public void onVoiceNoteRecorded() {

    }

    @Override
    public File createVoiceNoteFile() {
        if (product.isNew()) {
            return FileHelpers.createTempProductVoiceFile(this, product.BarCode);
        }
        else {
            return FileHelpers.createProductVoiceFile(this, product.UniqueId);
        }
    }

    @Override
    public File[] getVoiceNoteFiles() {
        if (product.isNew()) {
            return FileHelpers.getProductVoiceTempFiles(this, product.BarCode);
        }
        else {
            // TODO: Implement!
            return product.getVoiceNotes(this);
        }
    }

    @Override
    public void deleteNote(File noteFile) {
        if (noteFile.exists()) {
            noteFile.delete();
        }
    }

    @Override
    public String suggestProductName() {
        return "Hermelín - král sýrů";
    }

    @Override
    public void onCreateProduct(String productName) {
        product.Name = productName;
        product.saveWithCallbacks();
        MoveTempProductFiles moveTempProductFiles = new MoveTempProductFiles(this);
        AsyncTask<Product, Integer, Long> task = moveTempProductFiles.execute(product);
    }
}
