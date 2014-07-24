package com.alfd.app.rest;


import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface AuthEndpoints {
    @POST("/login-or-join")
    User loginOrJoin(@Body() User user);

}
