package com.magic.instantlyandroid;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.List;

public class LandingActivity extends AppCompatActivity implements Serializable,
                                                                  GoogleApiClient.OnConnectionFailedListener,
                                                                  View.OnClickListener
{

    private  final LandingActivity THIS_CLASS = this;
      
    private  GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private TextView mStatusTextView;
    private  String TAG = "LandingActivity";
    private static final int RC_SIGN_IN = 9001;

    private static boolean bFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        InstantlyModel.getInstance().setGoogleClient(mGoogleApiClient);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.name);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        //Initializing GoogleClient for this Activity.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Assigning properties to the SignIn button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());



        //Once verified from Google a/c
        //Check if the username exists in Parse DB
        //
    }

    public void createPFInstallationEntry(){

        ParseInstallation myInstance =  ParseInstallation.getCurrentInstallation();
        String xUser = myInstance.getString("xuser");
        String xTeacher = myInstance.getString("xteacher");

        if (xUser != null && xTeacher != null){
            Log.v("not null ","NN");
        }else{
            ParseUser student = ParseUser.getCurrentUser();

            if (student != null){
                String username = (String) student.get("username");
                String xteacher = (String) student.get("teacherid");
                Log.v("usrename is ", username);

                if (xUser == null && xTeacher == null) {
                    myInstance.put("xuser", username);
                    myInstance.put("xteacher", xteacher);

                    myInstance.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.v("xUser", "xUser has been save");
                        }
                    });
                }else{
                    Log.v("xUser 2","User Installation is already there!!!");
                }

            }else{
                Log.v("null student","St");
            }

            Log.v("Null ","N");
        }

    }


    //Onstart method for the Activity
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN OUT", "handleSignInResult:" + result.isSuccess());

        final GoogleSignInAccount acct = result.getSignInAccount();
        final Status status= result.getStatus();
        Log.v("status is ", String.valueOf(status));

        //Boolean bFound = false;
        LandingActivity.bFound = false;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.logOut();
                    if (e == null) {
                        //System.out.println(objects.size());
                        for (ParseUser u : objects) {
                            ///System.out.println("Parse Email " + u.getEmail() + " Google " + acct.getEmail());
                            if (acct.getEmail().equals(u.getEmail()) && u.get("usertype").toString().equals("Student")) {
                                Boolean b = acct.getEmail().equals(u.getEmail()) && u.get("usertype").toString().equals("Student");
                                UserProfileVO userProfile = new UserProfileVO(acct.getDisplayName(), acct.getPhotoUrl().getPath(), acct.getEmail());
                                InstantlyModel.getInstance().setUserProfile(userProfile);
                                String userType = u.get("usertype").toString();

                                ParseUser.logInInBackground(u.getEmail(), "magic@123", new LogInCallback() {
                                    @Override
                                    public void done(ParseUser user, ParseException e) {
                                        THIS_CLASS.createPFInstallationEntry();
                                    }
                                });

                                updateUI(true);
                                LandingActivity.bFound = true;
                                break;
                            }
                        }

                        if (!LandingActivity.bFound) {

                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            Log.v("SuccessFully Signed", "out");
                                            Toast.makeText(getApplicationContext(), "You are not Registered as Student",
                                                    Toast.LENGTH_LONG).show();
                                            updateUI(false);
                                        }
                                    });

                        }
                    }
                }
            });
        }
    }

    // [END handleSignInResult]

    //Method for [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Method for Handling the Unsuccessful login Result from G+ auth.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //Method for Showing the [progress of the Login via G+.
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    //Method for hiding progress Dialogue when is Successfully Signed In.
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    //Method for Updating UI on the basis of successful Login/Unsuccessful Login.
    private void updateUI(boolean signedIn) {

        if (signedIn) {
            Intent studentIntent = new Intent(LandingActivity.this, StudentViewActivity.class);
            studentIntent.putExtra("xKey","Hello from Landing Page");
            startActivity(studentIntent);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    //OnClick method for this activity.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

   /* @Override
    public void onConnected(Bundle bundle) {

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            //String personPhoto = currentPerson.getImage();
            String personGooglePlusProfile = currentPerson.getUrl();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }*/
}