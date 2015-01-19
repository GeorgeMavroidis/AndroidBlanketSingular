package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.georgemavroidis.feed.MainActivity;
import com.georgemavroidis.feed.R;
import com.georgemavroidis.feed.ServiceHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TwitterFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener {

    private ProgressDialog pDialog;
    private String url = "";

    // Tab titles
    JSONArray contacts = null;
    JSONObject credentials;
    JSONArray items = null;
    JSONObject json_dic = null;
    // JSON Node names
    private static final String TWITTER = "twitter";
    String res;
    Boolean fetching = false;
    String nextPageToken;
    TwitterAdapter adapter;
    ListView listView;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static TwitterFragment newInstance(String username) {
        TwitterFragment f = new TwitterFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        f.setArguments(args);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_top_rated, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.black);
        listView= (ListView) rootView.findViewById(R.id.sampleListView);
        Log.d("t", getArguments().getString("username"));




            String tem = readFromFile();

            if (tem != "") {
                try {
                    items = new JSONArray(tem);

                    nextPageToken = items.getJSONObject(items.length() - 1).getJSONObject("entry").getString("id_str");
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                String newurl = "http://thewotimes.com/Y/current.php?user=" + getString(R.string.uniser) + "&type=twitter&get=true";
                new GetContacts(newurl).execute();

            }




        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TableLayout table = (TableLayout) getView().findViewById(R.id.row);
//

    }

    @Override
    public void onRefresh() {
        String newurl = "http://thewotimes.com/Y/current.php?user="+getString(R.string.uniser)+"&type=twitter&get=true";

        new GetContacts(newurl).execute();

    }

    public void ref(){

    }

    public class GetContacts extends AsyncTask<Void, Void, Void> {

        private GetContacts(String giveurl){
            url = giveurl;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String twitter = sh.makeServiceCall(url, ServiceHandler.GET);

            if (twitter != null) {
                try {
                    String filename = getArguments().getString("username")+"-twitter.txt";
                    String string = twitter;
                    FileOutputStream outputStream;

                    try {
                        outputStream = MainActivity.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(string.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONArray t = new JSONArray(twitter);
//                    [[[items objectAtIndex:[items count]-1] objectForKey:@"entry"] objectForKey:@"id_str"];
                    nextPageToken = t.getJSONObject(t.length()-1).getJSONObject("entry").getString("id_str");
                    items = t;


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
            setAdapter();


        }
    }
    public void setAdapter(){

        ArrayList<TwitterCell> arrayOfUsers = new ArrayList<TwitterCell>();
// Create the adapter to convert the array to views
        adapter = new TwitterAdapter(MainActivity.getAppContext(), arrayOfUsers);
// Attach the adapter to a ListView
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                if(mLastFirstVisibleItem<firstVisibleItem){
//                    getActivity().getActionBar().hide();
                    Log.i("SCROLLING DOWN",totalItemCount-visibleItemCount+" TRUE "+mLastFirstVisibleItem + " ");
                    if(mLastFirstVisibleItem == totalItemCount-visibleItemCount-1){
                        new FetchAjax().execute();
                        Log.d("here", "here");


                    }

                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
//                    getActivity().getActionBar().show();
//                        Log.i("SCROLLING UP","TRUE");
                }
                mLastFirstVisibleItem=firstVisibleItem;


            }
        });

        populateAdapter();
        mSwipeRefreshLayout.setRefreshing(false);
//            TwitterCell temp = new TwitterCell("1", "", "", "","", "img", "ret", "fav", "100", "");


        fetching = false;
    }


    public void populateAdapter(){

        for(int i = 0; i < items.length(); i ++){

            try {
                JSONObject temp_items = items.getJSONObject(i).getJSONObject("entry");
                String text = temp_items.getString("text");
                String profile_photo = temp_items.getJSONObject("user").getString("profile_image_url_https");
                profile_photo = profile_photo.replace("normal", "bigger");
                String name = temp_items.getJSONObject("user").getString("name");
                String user_name = temp_items.getJSONObject("user").getString("screen_name");
                String imageMedia = "";
                String time = temp_items.getString("created_at");
                try{
                    imageMedia =  temp_items.getJSONObject("entities").getJSONArray("media").getJSONObject(0).get("media_url").toString();

                }catch(JSONException m){
//                        m.printStackTrace();
                }

                TwitterCell temp = new TwitterCell("1", profile_photo, name, user_name,text, "img", "ret", "fav", time, imageMedia);


                adapter.add(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public String convertTime(String timestamp){

        long date = System.currentTimeMillis()/1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        Date epoch = null;
        try {
            epoch = dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long milliseconds = epoch.getTime()-new Date().getTime();


        long different =new Date().getTime() - epoch.getTime() ;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long weeksBetweenDates = different/weeksInMilli;
        long daysBetweenDates = different / daysInMilli;
//        different = different % daysInMilli;

        long hoursBetweenDates = different / hoursInMilli;
//        different = different % hoursInMilli;

        long minutesBetweenDates = different / minutesInMilli;
//        different = different % minutesInMilli;

        long secondsBetweenDates = different / secondsInMilli;

        if(secondsBetweenDates < 60){
            timestamp = secondsBetweenDates+"s";

        }else
            if(minutesBetweenDates < 60){
                timestamp = minutesBetweenDates +"m";
            } else
                if(hoursBetweenDates < 24){
                    timestamp = hoursBetweenDates + "h";
                }else
                    if(daysBetweenDates < 7){
                        timestamp = daysBetweenDates +"d";

                     } else{

                            SimpleDateFormat month = new SimpleDateFormat("dd MMM");
                            timestamp = month.format(epoch);
//                            timestamp = weeksBetweenDates +"w";
                       }

//        Log.d("stuff", milliseconds+" Seconds: "+ elapsedSeconds +" Minutes: " +elapsedMinutes +" Hours: " + elapsedHours);

//        printDifference(new Date(), epoch);

        return timestamp;


    }
    public void printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

    }
    public class TwitterCell {
        public String theId;
        public String theProfilePicture;
        public String theUsername;
        public String theAtUsername;
        public String theMessage;
        public String theImage;
        public String theRetweets;
        public String theFavs;
        public String theTime;
        public String theImageMedia;

        public TwitterCell(String theId, String pp, String user, String atUser, String msg, String img, String ret, String fav, String time, String imageM) {
            this.theId = theId;
            this.theProfilePicture = pp;
            this.theUsername = user;
            this.theAtUsername = atUser;
            this.theMessage = msg;
            this.theImage = img;
            this.theRetweets = ret;
            this.theFavs = fav;
            this.theTime = time;
            this.theImageMedia = imageM;

        }
    }
    public class TwitterAdapter extends ArrayAdapter<TwitterCell> {
        public TwitterAdapter(Context context, ArrayList<TwitterCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TwitterFragment.TwitterCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.twitter_layout, parent, false);
            }

            TextView usernameTextView = (TextView) convertView.findViewById(R.id.username);
            TextView atUsernameTextView = (TextView) convertView.findViewById(R.id.at_username);
            ImageView profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
            TextView messageTextView = (TextView) convertView.findViewById(R.id.message);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.time);
            ImageView main_image = (ImageView) convertView.findViewById(R.id.main_image);

            usernameTextView.setText(cell.theUsername);
            atUsernameTextView.setText("@" + cell.theAtUsername);
            Picasso.with(getContext()).load(cell.theProfilePicture).into(profile_picture);
            messageTextView.setText(cell.theMessage);
            timeTextView.setText(convertTime(cell.theTime));


            messageTextView.setLinkTextColor(Color.parseColor("#00aced")); // color whatever u want
            Linkify.addLinks(messageTextView, Linkify.ALL);

//            if(cell.theImageMedia != null){
                    String media_url = cell.theImageMedia;
//            Log.d("h", media_url);
            if(media_url != "") {
                Picasso.with(getContext()).load(media_url).into(main_image);
                main_image.setVisibility(View.VISIBLE);
                final float scale = getContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (150 * scale + 0.5f);
                main_image.getLayoutParams().height = pixels;
            }else{
                main_image.setVisibility(View.INVISIBLE);
                main_image.getLayoutParams().height = 0;
            }
//            }
            // Populate the data into the template view using the data object

            // Return the completed view to render on screen
            return convertView;
        }
    }
    public class FetchAjax extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String ajax = "http://thewotimes.com/Y/user.php?user="+getString(R.string.uniser)+"&type=twitter&next="+nextPageToken;
            String t = sh.makeServiceCall(ajax, ServiceHandler.GET);

            if(t != null){
                try {

                    JSONArray json_dic = new JSONArray(t);
                    nextPageToken = json_dic.getJSONObject(json_dic.length()-1).getJSONObject("entry").getString("id");
                    JSONArray temp = json_dic;
                    //=items
                    JSONArray locallist =  new JSONArray();
                    for (int i = 0; i < temp.length(); i++) {
                        locallist.put(temp.getJSONObject(i));
                    }
                    items = locallist;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONArray result) {

            populateAdapter();
        }
    }
    private String readFromFile() {

        String ret = "";

        try {

            String filename = getArguments().getString("username")+"-twitter.txt";
            File file = new File(filename);

            InputStream inputStream = MainActivity.getAppContext().openFileInput(filename);


            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        catch (NullPointerException e){
            Log.e("null", "null pointer here");
        }

        return ret;
    }
}