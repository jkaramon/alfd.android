package com.alfd.app.data;

import android.util.Log;
import android.widget.Toast;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.alfd.app.LogTags;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by karamon on 15. 7. 2014.
 */

@Table(name = "Users")
public class User extends BaseServerModel {

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    @Column
    public String DisplayName;

    @Column
    public String FirstName;

    @Column
    public String LastName;


    @Column
    public Boolean LoggedIn;



    @Column
    public String Email;

    @Column
    public String Avatar;

    @Column
    public String GoogleAccessToken;

    @Column
    public String GoogleAccountName;

    @Column
    public String GooglePictureUrl;

    @Column
    public String GoogleProfileUrl;

    @Column
    public String GoogleId;

    @Column
    public long FamilyId;


    public static boolean needSignin() {
        User u = getLoggedIn();
        if (u == null) {
            return true;
        }
        return !u.LoggedIn || u.GoogleAccessToken == null;
    }
    public static User getLoggedIn() {
        return new Select()
                .from(User.class)
                .where("LoggedIn = 1")
                .executeSingle();
    }

    public static User getByGoogleAccountName(String googleAccountName) {
        return new Select()
                .from(User.class)
                .where("GoogleAccountName = ?", googleAccountName)
                .executeSingle();
    }




    public static String getGoogleAccountName(GoogleApiClient googleApiClient) {
        try {
            return Plus.AccountApi.getAccountName(googleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public void updateFromGplusProfile(GoogleApiClient googleApiClient) {
        try {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            if (currentPerson != null) {
                String personPhotoUrl = currentPerson.getImage().getUrl();
                GoogleProfileUrl = currentPerson.getUrl();
                GooglePictureUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static User createFromGplusProfile(GoogleApiClient googleApiClient) {
        User u = new User();
        try {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            if (currentPerson != null) {
                u.DisplayName = currentPerson.getDisplayName();
                u.FirstName = currentPerson.getName().getGivenName();
                u.LastName = currentPerson.getName().getFamilyName();

                u.Email = Plus.AccountApi.getAccountName(googleApiClient);
                u.GoogleAccountName = u.Email;
                u.GoogleProfileUrl = currentPerson.getUrl();
                u.GooglePictureUrl = currentPerson.getImage().getUrl();;
                u.GoogleId = currentPerson.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    public com.alfd.app.rest.User toREST() {
        com.alfd.app.rest.User restUser = new com.alfd.app.rest.User();
        restUser.displayName = DisplayName;
        restUser.firstName = FirstName;
        restUser.lastName = LastName;
        restUser.avatar = Avatar;
        restUser.email = Email;
        restUser.google.id = GoogleId;
        restUser.google.token = GoogleAccessToken;
        restUser.google.email = GoogleAccountName;
        restUser.google.name = DisplayName;
        restUser.google.pictureUrl = GooglePictureUrl;
        restUser.google.profileUrl = GoogleProfileUrl;

        return restUser;
    }

    public static User fromREST(com.alfd.app.rest.User restUser) {
        User u = new User();
        u.DisplayName = restUser.displayName;
        u.GooglePictureUrl = restUser.google.pictureUrl;
        u.GoogleProfileUrl = restUser.google.profileUrl;
        u.LastName = restUser.lastName;
        u.Avatar = restUser.avatar;
        u.Email = restUser.email;
        u.ServerId = restUser.id;
        u.ServerTimestamp = restUser.updatedAt;
        u.GoogleAccountName = restUser.google.email;
        u.GoogleId = restUser.google.id;

        return u;
    }


}
