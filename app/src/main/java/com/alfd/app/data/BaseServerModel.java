package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;
import com.alfd.app.rest.BaseRESTModel;

import org.joda.time.DateTime;

import java.util.UUID;

public abstract class BaseServerModel extends BaseModel {
    @Column
    public String ServerId;
    @Column
    public DateTime ServerTimestamp = null;

    @Override
    protected void beforeSave() {
        super.beforeSave();
    }





    public boolean wasSynced(){
        return ServerTimestamp != null;
    }


    public void sync(BaseRESTModel model) {
        this.ServerId = model.id;
        this.ServerTimestamp = model.updatedAt;
        this.saveWithCallbacks();
    }

}
