package com.alfd.app.rest;


import org.joda.time.DateTime;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface ProductEndpoints {

    @GET("/products/")
    PagedResult<Product> getProducts(@Query("from") DateTime lastSyncDate);

    @GET("/products/{barCode}/images")
    PagedResult<String> getProductImages(@Path("barCode") String barCode, @Query("from") DateTime lastSyncDate);

    @GET("/products/{barCode}/images/{imageName}")
    Response getProductImage(@Path("barCode") String barCode, @Path("imageName") String imageName, @Query("size") String imageSize);

    @GET("/products/{barCode}/voice-notes")
    PagedResult<String> getVoiceNotes(@Path("barCode") String barCode, @Query("from") DateTime lastSyncDate);

    @GET("/products/{barCode}/voice-notes/{voiceNoteName}")
    Response getVoiceNote(@Path("barCode") String barCode, @Path("voiceNoteName") String voiceNoteName);

    @POST("/products/{barCode}/images")
    @Multipart()
    Response uploadProductImage(@Path("barCode") String barCode, @Part("uniqueId") String uniqueId, @Part("file") TypedFile file);

    @POST("/products/{barCode}/voice-notes")
    @Multipart()
    Response uploadVoiceNote(@Path("barCode") String barCode, @Part("uniqueId") String uniqueId, @Part("file") TypedFile file);

    @POST("/products")
    Product insertProduct(@Body() Product product);

    @POST("/products/{barCode}")
    Product updateProduct(@Path("barCode") String barCode, @Body() Product product);


}
