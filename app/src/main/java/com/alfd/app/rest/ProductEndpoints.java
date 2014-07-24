package com.alfd.app.rest;


import retrofit.http.GET;

public interface ProductEndpoints {
    @GET("/products/")
    PagedResult<Product> getProducts();

}
