package com.alfd.app.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.activities.fragments.VoiceNoteFragment;
import com.alfd.app.adapters.NewProductPageAdapter;
import com.alfd.app.interfaces.OnPhotoInteractionListener;
import com.alfd.app.data.Product;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

/**
 * Created by karamon on 2. 5. 2014.
 */
public class BaseProductActivity extends BaseActionBarActivity implements OnPhotoInteractionListener, VoiceNoteFragment.OnFragmentInteractionListener {
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fillProduct(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    private void fillProduct(Bundle savedInstanceState) {

        product = new Product();
        if (savedInstanceState == null) {
            Intent i = getIntent();
            product.BarCode = i.getStringExtra(SC.BAR_CODE);
            product.BarType = i.getStringExtra(SC.BAR_TYPE);
        }
        else {
            product.BarCode = savedInstanceState.getString(SC.BAR_CODE);
            product.BarType = savedInstanceState.getString(SC.BAR_TYPE);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SC.BAR_CODE, product.BarCode);
        savedInstanceState.putString(SC.BAR_TYPE, product.BarType);

    }



    @Override
    public void savePhoto(Bitmap imageBitmap, String imageType) {
        if (product.isNew()) {
            File f = FileHelpers.createTempProductImageFile(this, imageType, product.BarCode);
            FileHelpers.saveImageFile(f, imageBitmap);
        }
    }

    @Override
    public File[] getImageFiles(String imageType) {
        if (product.isNew()) {
            return FileHelpers.getProductImageTempFiles(this, imageType, product.BarCode);
        }
        return new File[0];
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
    public File onVoiceNoteFileRequested() {
        if (product.isNew()) {
            return FileHelpers.getTempProductVoiceFile(this, product.BarCode);
        }
        return null;
    }

    @Override
    public void deleteNote() {
        File noteFile = FileHelpers.getTempProductVoiceFile(this, product.BarCode);
        if (noteFile.exists()) {
            noteFile.delete();
        }
    }
}
