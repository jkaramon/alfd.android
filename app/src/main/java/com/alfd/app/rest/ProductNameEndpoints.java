package com.alfd.app.rest;


import retrofit.http.GET;
import retrofit.http.Path;

public interface ProductNameEndpoints {
    @GET("/products/{barCode}/suggestName")
    ProductNameCache suggestName(@Path("barCode") String barCode);

}
