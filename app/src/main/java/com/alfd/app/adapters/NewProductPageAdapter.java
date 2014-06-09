package com.alfd.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.activeandroid.app.Application;
import com.alfd.app.R;

import java.util.List;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class NewProductPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private Activity activity;
    private String[] titles;

    public NewProductPageAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles) {
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
        return titles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
