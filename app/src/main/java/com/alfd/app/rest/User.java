package com.alfd.app.rest;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by karamon on 16. 7. 2014.
 */
public class User extends BaseRESTModel {
    public class Google {
        public String id;
        public String token;
        public String email;
        public String name;
        public String pictureUrl;
        public String profileUrl;
    }
    public class Family {
        public String id;
        public String displayName;

    }

    public String displayName;
    public String firstName;
    public String lastName;
    public String email;
    public String avatar;
    public String familyId;



    public Google google = new Google();
    public Family family = new Family();


}
