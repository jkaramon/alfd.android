package com.alfd.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfd.app.ImgSize;
import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.activities.fragments.ProductFullPhotoFragment;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductInfoFragment;
import com.alfd.app.adapters.ProductDetailPageAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.services.BaseServiceReceiver;
import com.alfd.app.utils.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends BaseProductActivity implements ProductInfoFragment.OnFragmentInteractionListener  {
    long productId;

    private ProductDetailPageAdapter pageAdapter;
    private File[] imageFiles;
    private List<Fragment> fragments;
    private List<String> titles;
    private MoveTempFilesReceiver moveTempFilesReceiver;
    private LinearLayout addVoiceNoteLayout;
    private LinearLayout contentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        refreshAll();
        pageAdapter = new ProductDetailPageAdapter(getSupportFragmentManager(), fragments, titles);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);
        IntentFilter svcIntentFilter = new IntentFilter(Services.MOVE_TEMP_FILES);
        svcIntentFilter.addCategory(Services.STATUS_SUCCESS);
        svcIntentFilter.addCategory(Services.STATUS_FAILURE);

        moveTempFilesReceiver = new MoveTempFilesReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(moveTempFilesReceiver, svcIntentFilter);

        addVoiceNoteLayout =(LinearLayout)findViewById(R.id.add_voice_note_layout);;
        contentLayout =  (LinearLayout)findViewById(R.id.content_layout);
        addVoiceNoteLayout.setVisibility(View.GONE);

    }
    private void refreshAll() {
        refreshImageFiles();
        refreshFragmentTitles();
        refreshFragments();
    }

    private void refreshImageFiles() {
        imageFiles = getImageFiles(null);
    }

    private void refreshFragmentTitles() {
        String[] fixedTitles = getResources().getStringArray(R.array.product_detail_pages);
        titles = new ArrayList<String>();
        titles.addAll(Arrays.asList(fixedTitles));
        for (File f : imageFiles) {
            titles.add(f.getName());
        }

    }

    private void refreshFragments() {
        fragments = new ArrayList<Fragment>();
        fragments.add(ProductInfoFragment.newInstance());
        for (File f : imageFiles) {
            fragments.add(ProductFullPhotoFragment.newInstance(f));
        }
    }


    @Override
    public Product getProduct() {
        return product;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_voice_note:
                Toast.makeText(this, "Recording voice note", Toast.LENGTH_SHORT).show();
                showVoiceNoteRecorder();

                return true;
            case R.id.action_take_picture:
                String imageType = ProductImageTypes.OVERVIEW;
                IntentFactory.takePicture(this, imageType, getTempFileToSave(imageType));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onVoiceNoteRecorded() {
        super.onVoiceNoteRecorded();
        hideVoiceNoteRecorder();
    }
    public void showVoiceNoteRecorder() {
        addVoiceNoteLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
    }
    public void hideVoiceNoteRecorder() {
        addVoiceNoteLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            startMoveProductFilesService();
        }

    }
    private class MoveTempFilesReceiver extends BaseServiceReceiver {

        @Override
        protected void onFailure(Context context, Intent intent) {

        }

        @Override
        protected void onSuccess(Context context, Intent resultIntent) {
            ProductDetailActivity activity = ProductDetailActivity.this;


            int voiceNotesMoved = resultIntent.getIntExtra(SC.VOICE_NOTES_MOVED, 0);
            int imagesMoved = resultIntent.getIntExtra(SC.IMAGES_MOVED, 0);

            if (voiceNotesMoved == 0 && imagesMoved == 0) {
                return;
            }
            product.saveWithCallbacks();
            if (voiceNotesMoved > 0) {
                activity.refreshVoiceNotes();
            }
            if (imagesMoved > 0) {
                activity.recreate();
            }

        }
    }
}
