package com.alfd.app.rest;

import com.alfd.app.data.ProductNameCache;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by karamon on 13. 6. 2014.
 */
public class SuggestProductNameRequest extends RetrofitSpiceRequest<ProductNameCache, ProductNameEndpoints> {
    private String barCode;

    public SuggestProductNameRequest(String barCode) {
        super(ProductNameCache.class, ProductNameEndpoints.class);
        this.barCode = barCode;
    }
    @Override
    public ProductNameCache loadDataFromNetwork() throws Exception {
        Ln.d("Call web service ");
        return getService().suggestName(barCode);
    }
}
