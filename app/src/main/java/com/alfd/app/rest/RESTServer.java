package com.alfd.app.rest;

import com.alfd.app.data.ProductNameCache;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class RESTServer extends RetrofitGsonSpiceService {

    private final static String BASE_URL = "http://192.168.14.6:3000";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(ProductNameEndpoints.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

}
