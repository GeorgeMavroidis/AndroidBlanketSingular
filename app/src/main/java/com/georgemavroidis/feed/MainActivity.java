package com.georgemavroidis.feed;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.georgemavroidis.feed.AdapterPackage.TabsPagerAdapter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.graphics.Color.WHITE;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener, YouTubePlayer.OnInitializedListener {
    private ViewPager viewPager;
    private static TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private ProgressDialog pDialog;
    private TextView mTextView;
    // URL to get contacts JSON
    private static String url = "http://www.thewotimes.com/Y/fetch.php?user="+ R.string.uniser;

    JSONObject credentials;
    String[] new_tabs = {};
    // JSON Node names
    private static final String TWITTER = "twitter";
    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    // Tab titles
    JSONArray contacts = null;
    static MediaPlayer mediaPlayer;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    private String[] tabs = { "JacksGap", "Twitter", "Instagram"};


    public static final String API_KEY = "AIzaSyB0oGK4Ozx7-hAbRRIZXr5xLFnL9rpNGKs";
    public static String VIDEO_ID = "o7VVHhK9zf0";


    private static Context context;
    private static View view;
    private static View fullscreen;
    private static FragmentManager FM;
    private static YouTubePlayer myPlayer;

    public static void setYoutubeVideo(String vidid){
//        getYoutubePF.getLayoutParams().height = 200;
        WindowManager windowManager = (WindowManager) getAppContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display =  windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View v = getYoutube();
        VIDEO_ID = vidid;
        updateYoutube(vidid);

//
//        v.getLayoutParams().height = size.x/16 * 9;

        v.getLayoutParams().height = size.y;
        v.getLayoutParams().width = size.x;
//        v.setRotation(90.0f);
        View ful = getFull();
//        ful.setX(v.getLayoutParams().width - ful.getWidth());
//        ful.setY(v.getLayoutParams().height - ful.getHeight());

        v.invalidate();
        v.requestLayout();
    }
    public static Context getAppContext() {
        return MainActivity.context;
    }
    public static FragmentManager getFM() {
        return MainActivity.FM;
    }
    public static View getFull() {
        return MainActivity.fullscreen;
    }
    public static View getYoutube() {
        return MainActivity.view;
    }
    public static YouTubePlayer getPlayer() {
        return MainActivity.myPlayer;
    }
    public static FragmentPagerAdapter getAdapter() {
        return MainActivity.mAdapter;
    }
    public static void updateYoutube(String vidid){
        VIDEO_ID = vidid;
        YouTubePlayerSupportFragment mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
        mYoutubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                MainActivity.myPlayer = youTubePlayer;
                if (!b) {
                    youTubePlayer.cueVideo(VIDEO_ID);
                    youTubePlayer.setShowFullscreenButton(false);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

        });
        FragmentManager fragmentManager = getFM();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.youtubeplayerview, mYoutubePlayerFragment);
        fragmentTransaction.commit();



    }
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker("Project Y")
                    :analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
    private int _xDelta;
    private int _yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        new GetContacts().execute();
        super.onCreate(savedInstanceState);

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        MainActivity.context = this;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notifs", "");
        editor.commit();
        editor.apply();

        Tracker t = getTracker(TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(getString(R.string.uniser) +" - Android");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

//        getActionBar().setDisplayShowHomeEnabled(false);  // hides action bar icon
//        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_main);
        MainActivity.view = findViewById(R.id.youtubeplayerview);

        WindowManager windowManager = (WindowManager) getBaseContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display =  windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View getYoutubePF = (View) findViewById(R.id.youtubeplayerview);
        getYoutubePF.getLayoutParams().height = 0;


        YouTubePlayerSupportFragment mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
//        mYoutubePlayerFragment.initialize(API_KEY, this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MainActivity.FM = fragmentManager;
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.youtubeplayerview, mYoutubePlayerFragment);
//        fragmentTransaction.commit();


//        VolleyApplication vol = new VolleyApplication();
        // Initilization
        MainActivity.fullscreen = findViewById(R.id.fullscreen);
        MainActivity.fullscreen.setY(100);
        MainActivity.fullscreen.setFocusable(true);
        MainActivity.fullscreen.bringToFront();
        MainActivity.fullscreen.setX(-100);

        MainActivity.fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // myView tapped //
                getActionBar().hide();
                WindowManager windowManager = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                Display display =  windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;


//                MainActivity.view.setY(200);

                MainActivity.view.invalidate();
                MainActivity.view.requestLayout();

//                MainActivity.view.setRotation(90.0f);
//
//                final float scale = context.getResources().getDisplayMetrics().density;
//                int w = (int) (width * scale + 0.5f);
//                int h = (int) (height * scale + 0.5f);
//
//                MainActivity.view.getLayoutParams().height = width;
//                MainActivity.view.getLayoutParams().width = height;
//                Log.d("dimenions", width + " " + height  + " w" +w + " " + h);
//
//                MainActivity.fullscreen.setX(-100);
//                MainActivity.fullscreen.setY(-100);
//                MainActivity.myPlayer.setFullscreen(true);

            }
        });


        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setBackgroundDrawable(new ColorDrawable(WHITE));
        // Adding Tabs
        int i = 0;
        for (String tab_name : tabs) {

            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this).setCustomView(getTabView(i)));
            i++;

        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        contactList = new ArrayList<HashMap<String, String>>();

//        AudioPlayerBroadcastReceiver broadcastReceiver = new AudioPlayerBroadcastReceiver();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//        // set the custom action
//        intentFilter.addAction("com.georgemavroidis.tyleroakley.ACTION_PLAY");
//        // register the receiver
//        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.title_bar, null);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(getBaseContext().getApplicationContext(),
                "onInitializationFailure()",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        MainActivity.myPlayer = player;
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//        if(id == R.id.action_refresh){
//            Log.d("here", "here");
//
//
//        }

        return super.onOptionsItemSelected(item);
    }


    private View getTabView(int i) {
        // TODO Auto-generated method stub
        RelativeLayout r = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.custom_tab, null);
        TextView t = (TextView) r.findViewById(R.id.custom_tab_text);
        t.setText(tabs[i]);
        int color;
        color = WHITE;

//        r.setBackgroundColor(color);

//        t.setBackgroundColor(color);
        return r;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog


        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);


            if (jsonStr != null) {
                try {
                    credentials = new JSONArray(jsonStr).getJSONObject(0);
                    // Getting JSON Array node
                    System.out.println(""+credentials.getString(TWITTER));




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            ArrayList<String> where = new ArrayList<String>();
            for(Iterator<String> iter = credentials.keys();iter.hasNext();) {
                String key = iter.next();
                where.add(key);
            }

        }

    }
    public static void playMusic(String paths){
//        Intent switchIntent = new Intent("com.georgemavroidis.tyleroakley.ACTION_PLAY");
//        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(getAppContext(), 100, switchIntent, 0);

        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(MainActivity.getAppContext(), Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" + paths));
//            mediaPlayer.prepare();
        mediaPlayer.start(); // no need to call prepare(); create() does that for you



    }

}
