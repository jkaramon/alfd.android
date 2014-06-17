package com.alfd.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alfd.app.Services;

/**
 * Created by karamon on 17. 6. 2014.
 */
public abstract class BaseServiceReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getCategories().contains(Services.STATUS_SUCCESS)) {
            onSuccess(context, intent);
        }
        else if (intent.getCategories().contains(Services.STATUS_FAILURE)) {
            onFailure(context, intent);
        }
    }

    protected abstract void onFailure(Context context, Intent intent);

    protected abstract void onSuccess(Context context, Intent intent);
}
