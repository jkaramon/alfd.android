package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;

import java.util.UUID;

public abstract class BaseServerModel extends BaseModel {
    @Column
    public String ServerId;
    @Column
    public int ServerTimestamp = -1;

    @Override
    protected void beforeSave() {
        super.beforeSave();
    }




    public boolean wasSynced(){
        return ServerTimestamp > 0;
    }


}
