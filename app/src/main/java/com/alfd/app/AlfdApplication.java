package com.alfd.app;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.alfd.app.data.DbSeed;
import com.alfd.app.utils.FileHelpers;

import net.danlew.android.joda.JodaTimeAndroid;


/**
 * Created by karamon on 11. 4. 2014.
 */
public class AlfdApplication extends android.app.Application {

    private static Context context;


    public static Context getAppContext() {
        return AlfdApplication.context;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        JodaTimeAndroid.init(this);
        AlfdApplication.context = getApplicationContext();
        ActiveAndroid.initialize(this);
        seedDb();
        FileHelpers.ensureDirectoryStructureExists(getBaseContext());
    }

    protected void seedDb()
    {
        DbSeed s = new DbSeed();
        s.seed();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

}

