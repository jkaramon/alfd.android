package com.alfd.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.alfd.app.ImgSize;
import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.activities.fragments.ProductFullPhotoFragment;
import com.alfd.app.activities.fragments.ProductGalleryPhotoFragment;
import com.alfd.app.activities.fragments.ProductInfoFragment;
import com.alfd.app.activities.fragments.ProductNameFragment;
import com.alfd.app.activities.fragments.ProductPlaceholderPhotoFragment;
import com.alfd.app.activities.fragments.VoiceNotesFragment;
import com.alfd.app.adapters.NewProductPageAdapter;
import com.alfd.app.adapters.ProductDetailPageAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.ImageResizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseProductActivity implements ProductInfoFragment.OnFragmentInteractionListener  {
    long productId;

    private ProductDetailPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);


        List<Fragment> fragments = getFragments();
        String[] titles = getResources().getStringArray(R.array.product_detail_pages);
        pageAdapter = new ProductDetailPageAdapter(getSupportFragmentManager(), fragments, titles);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);





    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(ProductInfoFragment.newInstance());
        FileHelpers.getProductImageFiles(this, product.BarCode, product.BarType, ImgSize.LARGE);

       // TODO: fragments.add(ProductFullPhotoFragment.newInstance())
        return fragments;
    }


    @Override
    public Product getProduct() {
        return product;
    }
}
