package com.alfd.app.activities;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductPlaceholderPhotoFragment;
import com.alfd.app.activities.fragments.VoiceNoteFragment;
import com.alfd.app.adapters.NewProductPageAdapter;
import com.alfd.app.data.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends BaseProductActivity  {

    private Product product;
    private NewProductPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);


        List<Fragment> fragments = getFragments();
        pageAdapter = new NewProductPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);


    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(getProductPhotoFragment(ProductImageTypes.OVERVIEW));
        fragments.add(getProductPhotoFragment(ProductImageTypes.INGREDIENTS));
        fragments.add(VoiceNoteFragment.newInstance());
        return fragments;
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




}
