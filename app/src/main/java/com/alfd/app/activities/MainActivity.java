package com.alfd.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alfd.app.FragmentFactory;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.ScanIntentResult;
import com.alfd.app.activities.fragments.ScanOrSearchFragment;
import com.alfd.app.activities.fragments.NonExistingProductFragment;
import com.alfd.app.data.Product;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.utils.FragmentUtils;


public class MainActivity extends BaseActionBarActivity
        implements NonExistingProductFragment.OnFragmentInteractionListener {

    private String barCode;
    private String barType;
    private String currentFragmentTag;
    private ScanOrSearchFragment scanOrSearchFragment;

    public static class FTags {
        public static final String NON_EXISTING_PRODUCT = "non_existing_product";
        public static final String PRODUCT_DETAIL = "product_detail";
        public static final String SCAN_OR_SEARCH = "scan_or_search";

    }

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SC.CURRENT_FRAGMENT_TAG, currentFragmentTag);
        savedInstanceState.putString(SC.BAR_CODE, barCode);
        savedInstanceState.putString(SC.BAR_TYPE, barType);
        if (scanOrSearchFragment != null) {
            savedInstanceState.putString(SC.SEARCH_TEXT, scanOrSearchFragment.getSearchString());

        }


    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //startActivity(new Intent(this, LoginActivity.class));
        //return;
//        barCode = "8594008352008";
//        barType = "EAN13";
//        renderNonExistingProductFragment();

//        Product p = Product.load(Product.class, 1);
//        Intent i = IntentFactory.navigateProduct(this, p);
//        startActivity(i);
//        return;
        //Intent i = IntentFactory.takePicture(this, FileHelpers.createTempProductImageFile(this, ProductImageTypes.OVERVIEW, "0123456789123"));



        mTitle = getTitle();



        if (savedInstanceState != null)
        {
            currentFragmentTag =  savedInstanceState.getString(SC.CURRENT_FRAGMENT_TAG);
            if (currentFragmentTag == FTags.NON_EXISTING_PRODUCT)
            {
                barCode = savedInstanceState.getString(SC.BAR_CODE);
                barType = savedInstanceState.getString(SC.BAR_TYPE);
                renderNonExistingProductFragment();

            } else if (currentFragmentTag == FTags.SCAN_OR_SEARCH) {
                renderScanOrSearchFragment(savedInstanceState.getString(SC.SEARCH_TEXT));
            }

        }
        else {
            renderScanOrSearchFragment(null);
        }
    }







    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SCAN) {

            if (resultCode == Activity.RESULT_OK) {
                identifyBarCode(data);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }


    }

    protected void identifyBarCode(Intent data)
    {
        barCode = data.getStringExtra(ScanIntentResult.BAR_CODE);
        barType = data.getStringExtra(ScanIntentResult.BAR_TYPE);
        Product p = Product.findByBarCode(barCode);
        if (p==null){

            renderNonExistingProductFragment();

        }
        else {
            Intent i = IntentFactory.navigateProduct(this, p);
            startActivity(i);
        }



    }

    private void renderScanOrSearchFragment(String searchText)
    {
        FragmentFactory factory = new FragmentFactory() {
            @Override
            public Fragment create() {
                return ScanOrSearchFragment.newInstance();
            }
        };
        currentFragmentTag = FTags.SCAN_OR_SEARCH;
        scanOrSearchFragment = (ScanOrSearchFragment)FragmentUtils.change(this, currentFragmentTag, factory);
        scanOrSearchFragment.setSearchString(searchText);
    }



    private void renderNonExistingProductFragment() {
        // update the main content by replacing fragments
        final Product p = new Product();
        p.BarCode = barCode;
        p.BarType = barType;
        FragmentFactory factory = new FragmentFactory() {
            @Override
            public Fragment create() {
                return NonExistingProductFragment.newInstance(p);
            }
        };
        currentFragmentTag = FTags.NON_EXISTING_PRODUCT;
        FragmentUtils.change(this, currentFragmentTag, factory);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateProduct(Product p) {
        Intent i = IntentFactory.createNewProduct(this, p);
        startActivity(i);
    }
}
