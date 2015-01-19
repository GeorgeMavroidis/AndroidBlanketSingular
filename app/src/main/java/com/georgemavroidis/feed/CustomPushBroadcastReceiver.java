package com.georgemavroidis.feed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.georgemavroidis.feed.AdapterPackage.InstagramFragment;
import com.georgemavroidis.feed.AdapterPackage.TwitterFragment;
import com.georgemavroidis.feed.AdapterPackage.YoutubeFragment;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by george on 14-11-09.
 */
public class CustomPushBroadcastReceiver extends ParsePushBroadcastReceiver {
    String notifs;



    @Override
    protected void onPushReceive(Context context, Intent intent) {
//        super.onPushReceive(context, intent);
        Log.d("here", "received");
        getNotification(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
//        super.onPushDismiss(context, intent);
        Log.d("here", "rdismiss");
        notifs = "";

    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
//        super.onPushOpen(context, intent);
        Log.d("here", "open");
        notifs = "";
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);

    }


    @Override
    protected Notification getNotification(Context context, Intent intent) {

        Log.i("Start", "notification");
        Bundle extras = intent.getExtras();
        String notification_title = "";
        String notification_message = "";

        String jsonData = extras.getString("com.parse.Data");
        try{
            JSONObject notification = new JSONObject(jsonData);
            String title = notification.getString("alert");
            String message = notification.getString("alert");

            notification_title = title;
            notification_message = message;
        }
         catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        notifs = preferences.getString("notifs", null);
        SharedPreferences.Editor editor = preferences.edit();

        if (notifs == null) {
            // the key does not exist
            Log.d("doesnt", "exist");
            String n = "";
            n += notification_message;
            notifs = n;
            editor.putString("notifs", notifs);
            editor.commit();
            editor.apply();


        } else {
            // handle the value
            String n = notifs;
            n += "--|||GEORGE|||--"+notification_message;
            notifs = n;
            editor.putString("notifs", notifs);
            editor.commit();
            editor.apply();


        }

        if(notification_message.contains("posted a video to")){
            FragmentManager fragmentManager = MainActivity.getFM();
            List<Fragment> t = fragmentManager.getFragments();
            YoutubeFragment tf = (YoutubeFragment) t.get(1);
            tf.onRefresh();

        }
        if(notification_message.contains("Tweeted:")){

            FragmentManager fragmentManager = MainActivity.getFM();
            List<Fragment> t = fragmentManager.getFragments();
            TwitterFragment tf = (TwitterFragment) t.get(2);
            tf.onRefresh();

        }
        if(notification_message.contains("instagram photo")){
            FragmentManager fragmentManager = MainActivity.getFM();
            List<Fragment> t = fragmentManager.getFragments();
            InstagramFragment tf = (InstagramFragment) t.get(3);
            tf.onRefresh();
        }






      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setContentText(notification_message);
        mBuilder.setTicker(notification_title);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);

      /* Increase notification number every time a new notification arrives */
        Log.d("A", intent.getExtras().toString());

      /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();


        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
        String[] splitted = notifs.split("\\-\\-\\|\\|\\|GEORGE\\|\\|\\|\\-\\-");

        String newLines = "";
        for(int i = splitted.length-1; i > -1; i--) {
            inboxStyle.addLine(splitted[i]);
            newLines += splitted[i] +"\n";
        }
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(newLines);

        mBuilder.setNumber(splitted.length-1);
        mBuilder.setStyle(bigTextStyle);
      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        int notifyID = 1;
        mBuilder.setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notifyID, mBuilder.build());

//        return super.getNotification(context, intent);
        return null;
    }


}
