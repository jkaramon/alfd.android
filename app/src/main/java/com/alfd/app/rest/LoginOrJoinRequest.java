package com.alfd.app.rest;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by karamon on 13. 6. 2014.
 */
public class LoginOrJoinRequest extends RetrofitSpiceRequest<User, AuthEndpoints> {
    private User user;

    public LoginOrJoinRequest(User user) {
        super(User.class, AuthEndpoints.class);
        this.user = user;
    }
    @Override
    public User loadDataFromNetwork() throws Exception {
        Ln.d("Call web service ");
        return getService().loginOrJoin(user);
    }
}
