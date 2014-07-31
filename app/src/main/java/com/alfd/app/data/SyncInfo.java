package com.alfd.app.data;

import android.content.ClipData;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

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



    public boolean initialSyncAllowed() {
        return this.InitialSyncState == SyncStates.NOT_STARTED || this.InitialSyncState == SyncStates.FAILED;
    }

    public boolean initialSyncDisabled() {
        return !initialSyncAllowed();
    }
    public boolean syncAllowed(boolean enforce) {
        Duration lastSyncDiff = new Duration(LastSyncDate == null ? DateTime.now().minusYears(1) : LastSyncDate, DateTime.now());
        return SyncState != SyncStates.IN_PROGRESS && (enforce || lastSyncDiff.getStandardMinutes() > 5);
    }

    public boolean syncDisabled(boolean enforce) {
        return !syncAllowed(enforce);
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

    public void setSyncDone() {
        this.SyncState = SyncStates.SUCCESS;
        this.saveWithCallbacks();
    }

    public void setSyncFailed() {
        this.SyncState = SyncStates.FAILED;
        this.saveWithCallbacks();
    }
    public void setSyncStarted() {
        this.SyncState = SyncStates.IN_PROGRESS;
        this.saveWithCallbacks();
    }


    public static SyncInfo get() {
        SyncInfo si =  new Select()
                .from(SyncInfo.class)
                .executeSingle();
        if (si == null) {
            si = new SyncInfo();
            si.saveWithCallbacks();
        }
        return si;
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
