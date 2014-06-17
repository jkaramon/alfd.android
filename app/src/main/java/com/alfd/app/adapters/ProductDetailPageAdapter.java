package com.alfd.app.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class ProductDetailPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private Activity activity;
    private List<String> titles;

    public ProductDetailPageAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.activity = activity;
        this.fragments = fragments;
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
