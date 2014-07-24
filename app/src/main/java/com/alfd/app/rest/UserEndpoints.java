package com.alfd.app.rest;


import com.alfd.app.data.*;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

public interface UserEndpoints {
    @GET("/users/my-family/users")
    PagedResult<User> myFamilyUsers();

}
