package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;

import java.util.UUID;

public abstract class BaseServerModel extends BaseModel {
    @Column
    public UUID UniqueId;
    @Column
    public int ServerTimestamp = -1;

    @Override
    protected void beforeSave() {
        super.beforeSave();
        if (UniqueId == null && isNew()) {
            UniqueId = UUID.randomUUID();
        }
    }




    public boolean wasSynced(){
        return ServerTimestamp > 0;
    }


}
