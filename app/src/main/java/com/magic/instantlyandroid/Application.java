package com.magic.instantlyandroid;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by manikant.upadhyay on 1/20/2016.
 */
public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


        Parse.initialize(this, "Xuh7EnP1cjHyPhnyyLoSBvVNv8jiDrJmGTm1Q87l", "X7OjRiYvUCThtGrhhWMe7nl7gqjp57NYDmbJ2J3a");
    }

}
