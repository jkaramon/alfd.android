package com.alfd.app.data;
import com.activeandroid.Model;
/**
 * Created by karamon on 14. 4. 2014.
 */
public abstract class BaseModel extends Model {
    public boolean isNew(){
        return getId() == null;
    }
}