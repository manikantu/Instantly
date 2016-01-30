package com.magic.instantlyandroid;

import com.google.android.gms.common.api.GoogleApiClient;

// File Name: Singleton.java
public class InstantlyModel {

    private static InstantlyModel singleton = new InstantlyModel( );
    private GoogleApiClient _googleClient;

    private UserProfileVO userProfile= null;

    public String name;

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private InstantlyModel(){

    }


    /* Static 'instance' method */
    public static InstantlyModel getInstance( ) {
        return singleton;
    }


    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }

    public void setGoogleClient(GoogleApiClient client){

        this._googleClient = client;


    }

    public void setUserProfile(UserProfileVO userProfile){
        this.userProfile = userProfile;
    }

    public UserProfileVO getUserProfile(){

        if(this.userProfile == null){

            return  null;
        }

        return this.userProfile;
    }

    public GoogleApiClient getGoogleClient(){

        return this._googleClient;
    }
}
