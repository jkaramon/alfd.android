package com.alfd.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.activities.fragments.ProductFullPhotoFragment;
import com.alfd.app.activities.fragments.ProductInfoFragment;
import com.alfd.app.activities.fragments.SetSensitivityFragment;
import com.alfd.app.adapters.ProductDetailPageAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.interfaces.ProductDetailListener;
import com.alfd.app.services.BaseServiceReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends BaseProductActivity implements ProductDetailListener, android.view.ActionMode.Callback {


    private ProductDetailPageAdapter pageAdapter;
    private File[] imageFiles;
    private List<Fragment> fragments;
    private List<String> titles;
    private MoveTempFilesReceiver moveTempFilesReceiver;
    private LinearLayout addVoiceNoteLayout;
    private LinearLayout contentLayout;

    ActionMode actionMode;
    private ProductInfoFragment productInfoFragment;

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
        productInfoFragment = ProductInfoFragment.newInstance();
        fragments.add(productInfoFragment);
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
                showVoiceNoteRecorder();
                actionMode = startActionMode(this);
                return true;
            case R.id.action_take_picture:
                String imageType = ProductImageTypes.OVERVIEW;
                IntentFactory.takePicture(this, imageType, getTempFileToSave(imageType));
                return true;
            case R.id.action_set_sensitivity:
                showSetSensitivityDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSetSensitivityDialog() {
        startActivityForResult(IntentFactory.setSensitivity(this, product.getId()), RequestCodes.SET_SENSITIVITY);
    }

    @Override
    public void onVoiceNoteRecorded(File recordedFile) {
        super.onVoiceNoteRecorded(recordedFile);
        startMoveProductFilesService();


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
        if (requestCode == RequestCodes.SET_SENSITIVITY && resultCode == Activity.RESULT_OK) {
            productInfoFragment.refreshSensitivityList();
            sync.sync(this, true);

        }

    }

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.record_voice_note, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem menuItem) {
        actionMode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
        actionMode = null;
        hideVoiceNoteRecorder();
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
            if (voiceNotesMoved > 0 || voiceNoteRecorded) {
                activity.refreshVoiceNotes();
                voiceNoteRecorded = false;
            }
            if (imagesMoved > 0) {
                activity.recreate();
            }

        }
    }
}
