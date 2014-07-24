package com.alfd.app.data;
import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by karamon on 14. 4. 2014.
 */
public abstract class BaseModel extends Model {

    public boolean isNew() {
        return getId() == null;
    }

    public final Long saveWithCallbacks() {
        beforeSave();
        Long id = super.save();
        afterSave();
        return id;
    }



    protected void afterSave() {
    }


    protected void beforeSave() {

    }


}
