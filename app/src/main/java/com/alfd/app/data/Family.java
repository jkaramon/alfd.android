package com.alfd.app.data;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.joda.time.DateTime;

/**
 * Created by karamon on 15. 7. 2014.
 */

@Table(name = "Families")
public class Family extends BaseServerModel {


    @Column
    public String DisplayName;





    public static Family fromREST(com.alfd.app.rest.User restUser) {
        Family f = new Family();
        f.DisplayName = restUser.family.displayName;
        f.ServerId = restUser.familyId;
        f.ServerTimestamp = DateTime.now();
        return f;
    }


}
