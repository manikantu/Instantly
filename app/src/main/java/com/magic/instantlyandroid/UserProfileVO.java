package com.magic.instantlyandroid;

/**
 * Created by manikant.upadhyay on 1/15/2016.
 */
public class UserProfileVO
{

    private String userName = "";
    private String personPhoto = "";
    private String personGooglePlusProfile = "";

    public UserProfileVO(String userName,String personPhoto,String personGooglePlusProfile){

        this.userName = userName;
        this.personPhoto = personPhoto;
        this.personGooglePlusProfile = personGooglePlusProfile;
    }

    public String getUserName(){

        if(this.userName == ""){

            return "User not set";
        }

        return  this.userName;
    }

    public String getUserPic(){

        if(this.personPhoto == ""){

            return "User pic not set";
        }

        return  this.personPhoto;
    }

    public String getUserProfile(){

        if(this.personGooglePlusProfile == ""){

            return "User profile not set";
        }

        return  this.personGooglePlusProfile;
    }
}
