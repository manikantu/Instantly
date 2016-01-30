package com.magic.instantlyandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manikant.upadhyay on 1/22/2016.
 */
public class InstantlyPushReceiver extends ParsePushBroadcastReceiver {


    public InstantlyPushReceiver() {
        super();
    }

    //Method for Receiving the Push on the basis of questionId and SessionId
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.e("Push", "Push received: " + json);
            String questionId = (String) json.get("question_id");
            String sessionId = (String) json.get("session_id");

            this.sendMessage(context, questionId,sessionId);

        } catch (JSONException e) {
            Log.e("Push", "Push message json exception: " + e.getMessage());
        }
    }
    //Method for Sending push as an Message on the basis of QuestionId and SessionId
    private void sendMessage(Context context, String questionId, String sessionId) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("onPushDataReceive");
        // You can also include some extra data.
        intent.putExtra("questionId", questionId);
        intent.putExtra("sessionId", sessionId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    //Mandatory Methods for ParsePushBroadcastReceiver
    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }
    //Mandatory Methods for ParsePushBroadcastReceiver
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

}
