package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.alfd.app.rest.BaseRESTModel;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

public abstract class BaseServerModel extends BaseModel {
    @Column
    public String ServerId;
    @Column
    public DateTime ServerTimestamp = null;

    @Column
    public DateTime SyncDirtyTs = null;

    @Column
    public boolean IsDeleted = false;

    @Override
    protected void beforeSave() {
        SyncDirtyTs = DateTime.now();
        super.beforeSave();
    }







    public boolean wasSynced(){
        return ServerTimestamp != null;
    }


    public void sync(BaseRESTModel model) {
        if (IsDeleted) {
            delete();
            return;
        }
        this.ServerId = model.id;
        this.ServerTimestamp = model.updatedAt;
        this.SyncDirtyTs = null;
        this.save();
    }

    public static <T extends BaseServerModel> T getByServerId(Class<T> modelType, String serverId) {
        return new Select()
                .from(modelType)
                .where("ServerId=?", serverId)
                .executeSingle();
    }
    public static <T extends BaseServerModel> List<T> getUnsynced(Class<T> modelType) {
        return new Select()
                .from(modelType)
                .where("ServerTimestamp is null or SyncDirtyTs IS NOT NULL")
                .execute();
    }

}
