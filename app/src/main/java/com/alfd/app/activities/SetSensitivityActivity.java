package com.alfd.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.alfd.app.SC;
import com.alfd.app.data.Product;
import com.alfd.app.interfaces.ProductDetailListener;

import com.alfd.app.R;

public class SetSensitivityActivity extends ActionBarActivity implements ProductDetailListener {

    private long productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sensitivity);
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState != null) {
            extras = savedInstanceState;
        }
        productId = extras.getLong(SC.PRODUCT_ID, -1);
        product = Product.load(Product.class, productId);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SC.PRODUCT_ID, productId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.set_sensitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResultAndFinish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setResultAndFinish() {
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public Product getProduct() {
        return product;
    }
}
