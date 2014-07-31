package com.alfd.app.services;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.data.Family;
import com.alfd.app.data.SyncInfo;
import com.alfd.app.rest.RESTServer;
import com.alfd.app.rest.User;

import retrofit.RestAdapter;

/**
 * Created by karamon on 17. 7. 2014.
 */
public class Sync {


    public void syncCurrentUser(User restUser, String accessToken) {
        com.alfd.app.data.User u = com.alfd.app.data.User.fromREST(restUser);
        u.GoogleAccessToken = accessToken;
        Family family = Family.fromREST(restUser);
        family.saveWithCallbacks();
        u.FamilyId = family.getId();
        u.LoggedIn = true;
        u.saveWithCallbacks();

    }

    public void ensureInitialSync(Activity activity) {
        SyncInfo si = SyncInfo.get();
        if (si.initialSyncAllowed()) {
            return;
        }
        Intent svcIntent = new Intent(activity, InitialSyncService.class);
        activity.startService(svcIntent);
    }

    public void sync(Activity activity, boolean enforce) {
        SyncInfo si = SyncInfo.get();
        if (si.syncDisabled(enforce)) {
            return;
        }
        Intent svcIntent = new Intent(activity, SyncService.class);
        activity.startService(svcIntent);
    }



}
