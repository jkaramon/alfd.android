package com.alfd.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.Services;
import com.alfd.app.activities.fragments.AddVoiceNoteFragment;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductNameFragment;
import com.alfd.app.adapters.NewProductPageAdapter;
import com.alfd.app.data.ProductNameCache;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.rest.SuggestProductNameRequest;
import com.alfd.app.services.BaseServiceReceiver;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends BaseProductActivity  {

    private NewProductPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private String[] titles;
    private ProductNameFragment productNameFragment;

    private SuggestProductNameRequest suggestProductNameRequest;

    private MoveTempFilesReceiver moveTempFilesReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        getSpiceManager().removeAllDataFromCache();
        getSpiceManager().execute(suggestProductNameRequest, "suggestProductName", DurationInMillis.ONE_MINUTE * 5, new SuggestProductNameRequestListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);


        titles = getResources().getStringArray(R.array.new_product_pages);
        initFragments();
        suggestProductNameRequest = new SuggestProductNameRequest(product.BarCode);

        IntentFilter svcIntentFilter = new IntentFilter(Services.MOVE_TEMP_FILES);
        svcIntentFilter.addCategory(Services.STATUS_SUCCESS);
        svcIntentFilter.addCategory(Services.STATUS_FAILURE);

        moveTempFilesReceiver = new MoveTempFilesReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(moveTempFilesReceiver, svcIntentFilter);


    }

    private void initFragments() {
        pageAdapter = null;
        fragments = new ArrayList<Fragment>() {};
        fragments.add(addProductPhotoFragment(ProductImageTypes.OVERVIEW));
        fragments.add(addProductPhotoFragment(ProductImageTypes.INGREDIENTS));
        fragments.add(AddVoiceNoteFragment.newInstance());
        productNameFragment = ProductNameFragment.newInstance();
        fragments.add(productNameFragment);
        pageAdapter = new NewProductPageAdapter(getSupportFragmentManager(),fragments, titles);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);
    }



    private Fragment addProductPhotoFragment(String imageType) {
        return ProductGalleryPhotoFragment.newInstance(imageType);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            String sourceFragmentKey = data.getStringExtra("source");
            for (ProductGalleryPhotoFragment pf : getProductGalleryPhotoFragments()) {
                pf.refresh();
            }


        }
    }


    private List<ProductGalleryPhotoFragment> getProductGalleryPhotoFragments() {
        ArrayList<ProductGalleryPhotoFragment> list = new ArrayList<ProductGalleryPhotoFragment>();
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof ProductGalleryPhotoFragment) {
                list.add((ProductGalleryPhotoFragment)f);
            }
        }
        return list;
    }

    private class MoveTempFilesReceiver extends BaseServiceReceiver {

        @Override
        protected void onFailure(Context context, Intent intent) {

        }

        @Override
        protected void onSuccess(Context context, Intent intent) {
            product.saveWithCallbacks();
            Intent i = IntentFactory.navigateProduct(NewProductActivity.this, product);
            startActivity(i);
            NewProductActivity.this.finish();
        }
    }

    private class SuggestProductNameRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.alfd.app.data.ProductNameCache> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(NewProductActivity.this, "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ProductNameCache productNameCache) {
            if (productNameFragment != null) {
                productNameFragment.setProductName(productNameCache.name);
            }
        }
    }
}
