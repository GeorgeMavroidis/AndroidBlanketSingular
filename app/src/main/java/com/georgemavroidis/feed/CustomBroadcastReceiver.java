package com.georgemavroidis.feed;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by george on 14-11-09.
 */
public class CustomBroadcastReceiver extends ParseBroadcastReceiver {

    private static final String TAG = "CustomBroadcastReceiver";
    private static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d("here", "I am here");
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.i("we", "got action " + action + " on channel " + channel + " with:");

            if (action.equalsIgnoreCase("com.georgemavroidis.pointlessblog.NEW_NOTIF")) {
                String title = "title";
                if (json.has("header"))
                    title = json.getString("header");
                generateNotification(context, title, json);
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

    private void generateNotification(Context context, String title, JSONObject json) {

        Intent intent = new Intent(context, MainActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

    numMessages = 0;
    NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_action_refresh)
                    .setContentTitle(title)
                    .setContentText("New Post in the Blog")
                    .setNumber(++numMessages);

    mBuilder.setContentIntent(contentIntent);

    mNotifM.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
