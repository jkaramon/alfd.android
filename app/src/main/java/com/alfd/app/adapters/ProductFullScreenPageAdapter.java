package com.alfd.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.alfd.app.activities.fragments.ProductFullPhotoFragment;

import java.util.List;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class ProductFullScreenPageAdapter extends FragmentStatePagerAdapter {
    private List<ProductFullPhotoFragment> fragments;

    public ProductFullScreenPageAdapter(FragmentManager fm, List<ProductFullPhotoFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public ProductFullPhotoFragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
