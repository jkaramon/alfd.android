package com.alfd.app;

import com.activeandroid.ActiveAndroid;
import com.alfd.app.data.DbSeed;
import com.alfd.app.utils.FileHelpers;


/**
 * Created by karamon on 11. 4. 2014.
 */
public class AlfdApplication extends android.app.Application {
    @Override
    public void onCreate()
    {
        super.onCreate();


        ActiveAndroid.initialize(this);
        String dbName = ActiveAndroid.getDatabase().getPath();
        //getBaseContext().deleteDatabase(dbName);

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

