package com.alfd.app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.activities.fragments.OnPhotoInteractionListener;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductPlaceholderPhotoFragment;
import com.alfd.app.adapters.NewProductPageAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.utils.FileHelpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends BaseActionBarActivity implements OnPhotoInteractionListener {

    private Product product;
    private NewProductPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fillProduct(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);


        List<Fragment> fragments = getFragments();
        pageAdapter = new NewProductPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)findViewById(R.id.new_product_view_pager);
        pager.setAdapter(pageAdapter);


    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(getProductPhotoFragment(ProductImageTypes.OVERVIEW));
        fragments.add(getProductPhotoFragment(ProductImageTypes.INGREDIENTS));
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
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SC.BAR_CODE, product.BarCode);
        savedInstanceState.putString(SC.BAR_TYPE, product.BarType);

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
    public void onPhotoSelected(Bitmap imageBitmap, String imageType) {
        savePhoto(imageBitmap, imageType);
    }

    @Override
    public File[] getImageFiles(String imageType) {
        return FileHelpers.getProductImageTempFiles(this, imageType, product.BarCode);
    }

    private void savePhoto(Bitmap imageBitmap, String imageType) {
        File f = FileHelpers.createTempProductImageFile(this, imageType, product.BarCode);
        FileHelpers.saveImageFile(f, imageBitmap);
    }
}
