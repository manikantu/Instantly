package com.magic.instantlyandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;


public class StudentViewActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int PROFILE_PIC_SIZE = 400;

    private GoogleApiClient mGoogleApiClient;
    private Button signout;
    private TextView name;
    private String TAG = "StudentViewActivity";
    private TextView mTextView;
    private ImageView image;
    private static final int RC_SIGN_IN = 9001;
    private TextView question;

    private TextView option1;
    private TextView option2;
    private TextView option3;
    private TextView option4;

    private JSONArray options;
    private String sessionId = "";
    private RelativeLayout questionContainer;
    private RelativeLayout noQuestionView;
    private Integer selectedOptionIndex = -1;
    private CountDownTimer waitTimer;
    private static StudentViewActivity _this;

    private long millisUntilFinished =30000;
    long seconds = millisUntilFinished / 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        _this = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.mGoogleApiClient = InstantlyModel.getInstance().getGoogleClient();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_view);
        this.signout = (Button) findViewById(R.id.signOut);
        this.signout.setOnClickListener(this);


        name = (TextView) findViewById(R.id.name);
        mTextView = (TextView) findViewById(R.id.mTextView);
        image = (ImageView) findViewById(R.id.userImage);
        question = (TextView) findViewById(R.id.questionDetail);
        option1=(TextView)findViewById(R.id.option1);
        option2=(TextView)findViewById(R.id.option2);
        option3=(TextView)findViewById(R.id.option3);
        option4=(TextView)findViewById(R.id.option4);
        questionContainer =(RelativeLayout)findViewById(R.id.questionContainer);
        noQuestionView = (RelativeLayout) findViewById(R.id.noQuestionView);

        Log.v("SIGN OUT", "SIGN OUT RECIEVED =========== start ");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("onPushDataReceive"));
    }

    //Braodcast Receiver for recieving the data from push notifications from parse

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String questionId = intent.getStringExtra("questionId");
            _this.sessionId = intent.getStringExtra("sessionId");
            Log.d("receiver", "Got question id : " + questionId);

            _this.noQuestionView.setVisibility(View.INVISIBLE);
            _this.questionContainer.setVisibility(View.VISIBLE);
            startQuestionTimer();

            StudentViewActivity.showQuestion(questionId);
            //_this.option1.setText("Hello");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("QuestionBank");
            query.whereEqualTo("objectId", questionId);
            query.findInBackground(new FindCallback<ParseObject>() {
                //Log.v("Q====","showQuestion 1");
                public void done(List<ParseObject> objects, ParseException e) {

                    String len = Integer.toString(objects.size());

                    if (e == null) {
                        Log.v("Q===@@@@@@=", "showQuestion 3");

                        ParseObject object = objects.get(0);
                        String title = object.getString("question_title");
                        _this.options = object.getJSONArray("options");

                        _this.question.setText(title);
                        Integer lenX = _this.options.length();

                        TextView [] tvs = new TextView[4];
                        tvs[0] = _this.option1;
                        tvs[1] = _this.option2;
                        tvs[2] = _this.option3;
                        tvs[3] = _this.option4;

                        for (Integer i = 0; i < lenX; i++) {
                            try {
                                JSONObject option = _this.options.getJSONObject(i);
                                String correct = (String) option.get("correct");
                                String opt = (String) option.get("option");
                                TextView optionItem = tvs[i];
                                optionItem.setText(opt);

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                    } else {

                    }
                }
            });

        }
    };

    //Method for silent sign in.

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Method which is mandatory for onConnectionFailedListener.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Method to signout from current mGoogleApiClient (Current session Token by Google)
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.v("SuccessFully Signed", "out");
                        updateUI(true);
                    }
                });
    }


   //Method for Logging the questions via their respective questionId's
    private static void showQuestion(String questionId) {

        Log.v("Q====", "showQuestion 0");

    }

    //Method which tests whther the signInResult is "True" or "False" and used for caching the current Sign in.
     private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN OUT", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    //Method is used when the Signout button is pressed to Load the UpdatedUI i.e Login page.
    private void updateUI(boolean signedIn) {
        if (signedIn) {
        } else {

            name.setText(R.string.signed_out);
            Intent loginIntent = new Intent(StudentViewActivity.this, LandingActivity.class);
            startActivity(loginIntent);
        }
    }

    //Activity method.(Is invoked when this activity is loaded)
    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();


        UserProfileVO userInfo = InstantlyModel.getInstance().getUserProfile();

        String personName = userInfo.getUserName();
        String personEmail = userInfo.getUserProfile();
        String personPhoto = userInfo.getUserPic();

        Log.v("Your Email is: ", personEmail);
        getProfileInformation();
        // image.setImageBitmap(bitmap);
        // Log.v("Hello your dp is here", String.valueOf(bitmap));
        Log.v("Hi", personPhoto);
        name.setText("" + personName);

    }
    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Log.v("name",personName);
                Log.v("personPhotoUrl",personPhotoUrl);
                Log.v("personGooglePlusProfile",personGooglePlusProfile);
                Log.v("email",email);





                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(image).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    //Timer method for a question
    private void startQuestionTimer() {

        waitTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
               seconds =30- millisUntilFinished / 1000;
                Log.v("Time left" , String.valueOf(seconds));
                mTextView.setText(" Time (In Seconds) Left: " + millisUntilFinished / 1000 );
            }

            public void onFinish() {
                mTextView.setText("done!");
                _this.noQuestionView.setVisibility(View.VISIBLE);
                _this.questionContainer.setVisibility(View.INVISIBLE);
                try {
                    notAttempted();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }.start();
    }
    private void notAttempted() throws JSONException{
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        String teacherId = ParseInstallation.getCurrentInstallation().getString("xteacher").toString();
        Log.v("TeacherId ", teacherId);


        pushQuery.whereEqualTo("xuser", teacherId);
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);

        String message = "Response from Student";
        push.setMessage(message);
        JSONObject data = new JSONObject();
        data.put("nt","spa");
        data.put("teacher","ranjan.dey@magicsw.com");
        push.setData(data);
        push.sendInBackground();


        final ParseObject studentSessionInput = new ParseObject("StudentSessionInput");
        String currentUser = "manikant.upadhyay@magicsw.com";

        studentSessionInput.put("studentId",currentUser);
        studentSessionInput.put("os", "android");
        studentSessionInput.put("responseTime","0");
        studentSessionInput.put("response","NA");
        studentSessionInput.put("numAttemptChanged","0");
        studentSessionInput.put("sessionId",this.sessionId);

        studentSessionInput.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.v("you have not attempted ", "question");
            }
        });



    }
    //Onclick listener for options(Picking out the response from onClick on options(1-4) )
    private  void onSubmitButtonClick() throws JSONException {
        final JSONObject option = this.options.getJSONObject(this.selectedOptionIndex);
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        String teacherId = ParseInstallation.getCurrentInstallation().getString("xteacher").toString();
        Log.v("TeacherId ", teacherId);


        pushQuery.whereEqualTo("xuser", teacherId);
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);

        String message = "Response from Student";
        push.setMessage(message);
        JSONObject data = new JSONObject();
        data.put("nt","spa");
        data.put("teacher","ranjan.dey@magicsw.com");
        push.setData(data);
        push.sendInBackground();


        final ParseObject studentSessionInput = new ParseObject("StudentSessionInput");
        String currentUser = "manikant.upadhyay@magicsw.com";
        studentSessionInput.put("studentId",currentUser);
        studentSessionInput.put("os", "android");
        String xString = Integer.toString((int) (seconds));
        studentSessionInput.put("responseTime",xString);
        studentSessionInput.put("response",(String)option.get("correct"));
        studentSessionInput.put("numAttemptChanged","1");
        studentSessionInput.put("sessionId",this.sessionId);

        studentSessionInput.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String Response = (String) studentSessionInput.get("response");
                Log.v("Your response is", Response);
                String toastMessage = "";

                if(Response.equals("Correct")){
                    toastMessage = "Your answer is correct";
                }
                else {
                    toastMessage = "Your answer is incorrect";
                }

                Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_LONG).show();
                _this.noQuestionView.setVisibility(View.VISIBLE);
                _this.questionContainer.setVisibility(View.INVISIBLE);
                question.setText("");
                question.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option1.setText("");
                option1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option2.setText("");
                option2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option3.setText("");
                option3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option4.setText("");
                option4.setBackgroundColor(Color.parseColor("#FFFFFF"));


            }
        });
    }

    private void resetColor(TextView option){

        /*TextView [] tvs = {this.option1,this.option2,this.option3,this.option4};

        for(Integer i=0;i<tvs.length;i++){

            TextView tv = tvs[i];
            tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }*/
    }


    //Onclick method for the Activity.
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signOut:
                signOut();
                finish();
                break;

            case R.id.option1:
                this.selectedOptionIndex = 0;
                Log.v("You pressed", "option1");
                option1.setBackgroundColor(Color.parseColor("#5FCAE5"));
                option2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option4.setBackgroundColor(Color.parseColor("#FFFFFF"));
               this.resetColor(this.option1);

                break;

            case R.id.option2:
                Log.v("You pressed", "option2");
                this.selectedOptionIndex = 1;
                option2.setBackgroundColor(Color.parseColor("#5FCAE5"));
                option1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option4.setBackgroundColor(Color.parseColor("#FFFFFF"));

                this.resetColor(this.option2);

                break;

            case R.id.option3:
                this.selectedOptionIndex = 2;
                Log.v("You pressed", "option3");
                option3.setBackgroundColor(Color.parseColor("#5FCAE5"));
                option1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option4.setBackgroundColor(Color.parseColor("#FFFFFF"));

                this.resetColor(this.option3);
                break;

            case R.id.option4:
                this.selectedOptionIndex = 3;
                Log.v("You pressed", "option4");
                option4.setBackgroundColor(Color.parseColor("#5FCAE5"));
                option1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                option3.setBackgroundColor(Color.parseColor("#FFFFFF"));

                this.resetColor(this.option4);
                break;

            case R.id.pushAnswer:

                try {
                    this.onSubmitButtonClick();
                    if(waitTimer != null) {
                        waitTimer.cancel();
                        waitTimer = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }
    }
}