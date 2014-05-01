package com.alfd.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.alfd.app.FragmentFactory;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.ScanIntentResult;
import com.alfd.app.activities.fragments.HomeFragment;
import com.alfd.app.activities.fragments.NavigationDrawerFragment;
import com.alfd.app.activities.fragments.NonExistingProductFragment;
import com.alfd.app.data.Product;
import com.alfd.app.intents.IntentFactory;


public class MainActivity extends BaseActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, NonExistingProductFragment.OnFragmentInteractionListener {

    private String barCode;
    private String barType;
    private String currentFragmentTag;

    public static class FTags {
        public static final String NON_EXISTING_PRODUCT = "non_existing_product";
        public static final String PRODUCT_DETAIL = "product_detail";
        public static final String HOME = "home";

    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SC.CURRENT_FRAGMENT, currentFragmentTag);
        savedInstanceState.putString(SC.BAR_CODE, barCode);
        savedInstanceState.putString(SC.BAR_TYPE, barType);

    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        Product test = new Product();
        test.BarCode = "0123456789012";
        test.BarType = "EAN13";
        onCreateProduct(test);

        if (savedInstanceState != null)
        {
            currentFragmentTag =  savedInstanceState.getString(SC.CURRENT_FRAGMENT);
            if (currentFragmentTag == FTags.NON_EXISTING_PRODUCT)
            {
                barCode = savedInstanceState.getString(SC.BAR_CODE);
                barType = savedInstanceState.getString(SC.BAR_TYPE);
                renderNonExistingProductFragment();

            } else if (currentFragmentTag == FTags.HOME) {
                renderHomeFragment(1);
            }

        }
    }


    private void changeFragment(String tag, FragmentFactory factory)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag(tag);
        if (f == null) {
            f = factory.create();
        }
        currentFragmentTag = tag;
        fragmentManager.beginTransaction()
                .replace(R.id.container, f, tag)
                .commit();

    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        renderHomeFragment(position+1);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SCAN) {

            if(resultCode == Activity.RESULT_OK){
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
            renderProductDetailFragment(p.getId());
        }



    }

    private void renderHomeFragment(final int sectionIndex)
    {
        FragmentFactory factory = new FragmentFactory() {
            @Override
            public Fragment create() {
                return HomeFragment.newInstance(sectionIndex);
            }
        };
        changeFragment(FTags.HOME, factory);
    }

    private void renderProductDetailFragment(Long productId) {
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
        changeFragment(FTags.NON_EXISTING_PRODUCT, factory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            return true;
        }

        return super.onCreateOptionsMenu(menu);
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
    public void onCreateProduct(Product p) {
        Intent i = IntentFactory.createNewProduct(this, p);
        startActivity(i);
    }
}
