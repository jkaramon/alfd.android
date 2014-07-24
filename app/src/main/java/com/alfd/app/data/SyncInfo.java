package com.alfd.app.data;

import android.content.ClipData;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;

/**
 * Created by karamon on 15. 7. 2014.
 */

@Table(name = "SyncInfo")
public class SyncInfo extends BaseModel {
    public static class SyncStates {
        public static String NOT_STARTED = "not_started";
        public static String IN_PROGRESS = "in_progress";
        public static String FAILED = "failed";
        public static String SUCCESS = "success";

    }

    @Column
    public DateTime LastSyncDate;
    @Column
    public DateTime LastInitialSyncDate;

    @Column
    public String InitialSyncState = SyncStates.NOT_STARTED;

    @Column
    public String SyncState = SyncStates.NOT_STARTED;



    public boolean initialSyncNeeded() {
        return this.InitialSyncState == SyncStates.NOT_STARTED || this.InitialSyncState == SyncStates.FAILED;
    }

    public void setInitialSyncStarted() {
        this.InitialSyncState = SyncStates.IN_PROGRESS;
        this.saveWithCallbacks();
    }

    public void setInitialSyncDone() {
        this.InitialSyncState = SyncStates.SUCCESS;
        this.saveWithCallbacks();
    }

    public void setInitialSyncFailed() {
        this.InitialSyncState = SyncStates.FAILED;
        this.saveWithCallbacks();
    }


    public static SyncInfo get() {
        return new Select()
                .from(SyncInfo.class)
                .executeSingle();
    }

    public static void set() {
        SyncInfo si = get();
        if (si == null) {
            si = new SyncInfo();
        }
        si.LastSyncDate = DateTime.now();
        si.saveWithCallbacks();

    }


}
