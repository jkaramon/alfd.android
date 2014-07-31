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

public interface SensitivityEndpoints {

    @GET("/sensitivities")
    PagedResult<Sensitivity> getSensitivities(@Query("from") DateTime lastSyncDate);



    @POST("/sensitivities")
    Sensitivity upsertSensitivity(@Body() Sensitivity sensitivity);


}
