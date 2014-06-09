package com.alfd.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductInfoFragment;
import com.alfd.app.activities.fragments.ProductNameFragment;
import com.alfd.app.activities.fragments.ProductPlaceholderPhotoFragment;
import com.alfd.app.activities.fragments.VoiceNotesFragment;
import com.alfd.app.adapters.NewProductPageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewProductActivity extends BaseProductActivity  {

    private NewProductPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);


        titles = getResources().getStringArray(R.array.new_product_pages);
        initFragments();





    }

    private void initFragments() {
        pageAdapter = null;
        fragments = new ArrayList<Fragment>() {};
        fragments.add(getProductPhotoFragment(ProductImageTypes.OVERVIEW));
        fragments.add(getProductPhotoFragment(ProductImageTypes.INGREDIENTS));
        fragments.add(VoiceNotesFragment.newInstance());
        fragments.add(ProductNameFragment.newInstance());
        pageAdapter = new NewProductPageAdapter(getSupportFragmentManager(),fragments, titles);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);
    }



    private Fragment getProductPhotoFragment(String imageType) {
        File[] imageFiles = getImageFiles(imageType);
        if (imageFiles.length > 0) {
            return ProductGalleryPhotoFragment.newInstance(imageType);
        }
        return ProductPlaceholderPhotoFragment.newInstance(imageType);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_product, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            String sourceFragmentKey = data.getStringExtra("source");
            //if (sourceFragmentKey == ProductImageTypes.OVERVIEW) {
                fragments.set(0, getProductPhotoFragment(ProductImageTypes.OVERVIEW));
            //}
            //if (sourceFragmentKey == ProductImageTypes.INGREDIENTS) {
                fragments.set(1, getProductPhotoFragment(ProductImageTypes.INGREDIENTS));
            //}
            pageAdapter.notifyDataSetChanged();



        }
    }



}
