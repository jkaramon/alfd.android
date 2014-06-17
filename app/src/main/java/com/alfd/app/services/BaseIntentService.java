package com.alfd.app.services;

import android.app.IntentService;
import android.content.Intent;


/**
 * Created by karamon on 17. 6. 2014.
 */
public abstract class BaseIntentService extends IntentService {
    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        preExecute(workIntent);

        process();
    }

    protected void process() {
        try {
            execute();
            onSuccess();
        }
        catch (Exception e) {
            onFailure(e);
        }
    }
    protected abstract Intent createIntent(String category);

    protected abstract void onFailure(Exception e);

    protected abstract void onSuccess();

    protected abstract void execute();


    protected abstract void preExecute(Intent workIntent);
}
