package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.georgemavroidis.feed.MainActivity;
import com.georgemavroidis.feed.ServiceHandler;
import com.georgemavroidis.feed.R;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;



public class InstagramFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

    private ProgressDialog pDialog;
    private String url = "";

    // Tab titles
    JSONArray contacts = null;
    JSONObject credentials;
    JSONArray items = null;
    JSONObject json_dic = null;
    // JSON Node names
    private static final String INSTAGRAM = "instagram";
    String res;
    String nextPageToken;
    Boolean fetching = false;
    ListView listView;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    InstagramAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static InstagramFragment newInstance(String username) {
        InstagramFragment f = new InstagramFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        f.setArguments(args);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        f.getView().setBackgroundColor(Color.WHITE);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_top_rated, container, false);
        Log.d("t",getArguments().getString("username"));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.black);
        listView = (ListView) rootView.findViewById(R.id.sampleListView);

        String tem = readFromFile();

        if(tem != ""){
            try {
                items = new JSONArray(tem);

                nextPageToken =items.getJSONObject(items.length()-1).getJSONObject("entry").getString("id");
                setAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            String newurl = "http://thewotimes.com/Y/current.php?user="+getString(R.string.uniser)+"&type=instagram&get=true";
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
        String newurl = "http://thewotimes.com/Y/current.php?user="+getString(R.string.uniser)+"&type=instagram&get=true";
        new GetContacts(newurl).execute();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        private GetContacts(String giveurl){
            url = giveurl;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            fetching = true;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Zoella...");
            pDialog.setCancelable(false);
//            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String instagram = sh.makeServiceCall(url, ServiceHandler.GET);

            if (instagram != null) {
                try {
                    String filename = getArguments().getString("username")+"-instagram.txt";
                    String string = instagram;
                    FileOutputStream outputStream;

                    try {
                        outputStream = MainActivity.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(string.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONArray t = new JSONArray(instagram);

//                    nextPageToken = [[[items objectAtIndex:[items count]-1] objectForKey:@"entry"] objectForKey:@"id"];
                    nextPageToken = t.getJSONObject(t.length()-1).getJSONObject("entry").getString("id");
//                    Log.d("nexPAgeToken", nextPageToken);
                    items = t;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                try {
                    items = new JSONArray(readFromFile());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        ArrayList<InstagramCell> arrayOfUsers = new ArrayList<InstagramCell>();
// Create the adapter to convert the array to views
        adapter = new InstagramAdapter(getActivity().getBaseContext(), arrayOfUsers);
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
                    Log.i("SCROLLING DOWN",totalItemCount-visibleItemCount+"TRUE "+mLastFirstVisibleItem + " ");
                    if(mLastFirstVisibleItem == totalItemCount-visibleItemCount-5){
                        new FetchAjax().execute();

                    }

                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
//                    getActivity().getActionBar().show();
                    Log.i("SCROLLING UP","TRUE");
                }
                mLastFirstVisibleItem=firstVisibleItem;


            }
        });

        populateAdapter();

        mSwipeRefreshLayout.setRefreshing(false);
        fetching = false;
    }
    public void populateAdapter(){
        for(int i = 0; i < items.length(); i ++){

            try {
                JSONObject temp_items = items.getJSONObject(i).getJSONObject("entry");
                String std_res = temp_items.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                String profile_pic = temp_items.getJSONObject("user").getString("profile_picture");
                String name = temp_items.getJSONObject("user").getString("username");
                String msg = name +" "+ temp_items.getJSONObject("caption").getString("text");
                String likes = temp_items.getJSONObject("likes").getString("count");
                JSONObject comments = temp_items.getJSONObject("comments");
                String c = "";
                String time = convertTime(temp_items.getString("created_time"));

                if(comments != null) {
                    c = comments.getString("count");

                }
                InstagramCell temp = new InstagramCell("1", profile_pic, "", name, msg, "img", c, likes, time, std_res, comments);
                adapter.add(temp);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String convertTime(String timestamp){

        long date = System.currentTimeMillis()/1000;

        long different = date - Long.parseLong(timestamp);
//        Log.d("a", date +" " + Long.parseLong(timestamp) +" "+different);

        long secondsInMilli = 1000;
        long minutesInMilli = 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long weeksBetweenDates = different/weeksInMilli;
        long daysBetweenDates = different / daysInMilli;
        long hoursBetweenDates = different / hoursInMilli;
        long minutesBetweenDates = different / minutesInMilli;
        long secondsBetweenDates = different;
//        Log.d("stiff", daysBetweenDates +" " + minutesBetweenDates + " " + secondsBetweenDates);

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
                timestamp = weeksBetweenDates +"w";
    //                            timestamp = weeksBetweenDates +"w";
            }


        return timestamp;


    }
    public class InstagramCell {
        public String theId;
        public String theProfilePicture;
        public String theUsername;
        public String theAtUsername;
        public String theMessage;
        public String theImage;
        public String theRetweets;
        public String theFavs;
        public String theTime;
        public String theMainImage;
        public JSONObject theComments;

        public InstagramCell(String theId, String pp, String user, String atUser, String msg, String img, String ret, String fav, String time, String main_image, JSONObject com) {
            this.theId = theId;
            this.theProfilePicture = pp;
            this.theUsername = user;
            this.theAtUsername = atUser;
            this.theMessage = msg;
            this.theImage = img;
            this.theRetweets = ret;
            this.theFavs = fav;
            this.theTime = time;
            this.theMainImage = main_image;
            this.theComments = com;

        }
    }
    public class InstagramAdapter extends ArrayAdapter<InstagramCell> {
        public InstagramAdapter(Context context, ArrayList<InstagramCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            InstagramFragment.InstagramCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.instagram_layout, parent, false);
            }

            TextView atUsernameTextView = (TextView) convertView.findViewById(R.id.at_username);
            ImageView profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
            ImageView main_picture = (ImageView) convertView.findViewById(R.id.main_image);
            TextView messageTextView = (TextView) convertView.findViewById(R.id.message);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.time);
            TextView likesTextView = (TextView)convertView.findViewById(R.id.like_textview);
            TextView vmTV = (TextView)convertView.findViewById(R.id.viewmore);
            TextView comments = (TextView)convertView.findViewById(R.id.comments);

            DecimalFormat df = new DecimalFormat("#,###");
            String as = df.format(Double.parseDouble(cell.theFavs));
            as += " likes";
            SpannableString liketext = new SpannableString(as);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            liketext.setSpan(new ForegroundColorSpan(Color.parseColor("#517fa4")), 0,as.length(), 0);
            liketext.setSpan(boldSpan, 0, as.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            likesTextView.setText(liketext);

            atUsernameTextView.setText(cell.theAtUsername);
            Picasso.with(getContext()).load(cell.theProfilePicture)
                    .transform(new RoundedTransformation(80, 10))
                    .into(profile_picture);

            // make "Lorem" (characters 0 to 5) red

            SpannableString text = new SpannableString(cell.theMessage);
            text.setSpan(new ForegroundColorSpan(Color.parseColor("#517fa4")), 0, cell.theAtUsername.length(), 0);
            text.setSpan(boldSpan, 0, cell.theAtUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            messageTextView.setText(text);
            timeTextView.setText(cell.theTime);
            WindowManager windowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display =  windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            main_picture.getLayoutParams().height = size.x;
            main_picture.getLayoutParams().width = size.x;

            Picasso.with(getContext())
                    .load(cell.theMainImage)
                    .resize(size.x, size.x)
                    .placeholder(R.drawable.back)
                    .into(main_picture);



            vmTV.setText("view "+cell.theRetweets+" more comments");
            String displayedC = "";
            ArrayList<SpannableString> uniList = new ArrayList<SpannableString>();
            try {
                if(cell.theComments.getString("count") != "0"){
                    JSONArray cData = cell.theComments.getJSONArray("data");

                    for(int i = 0 ; i < cData.length(); i++){
                        String tName = cData.getJSONObject(i).getJSONObject("from").getString("username");
                        String temp = cData.getJSONObject(i).getString("text");

                        SpannableString t = new SpannableString(tName + " " +temp+"\n");

                        StyleSpan bSpan = new StyleSpan(Typeface.BOLD);
                        t.setSpan(new ForegroundColorSpan(Color.parseColor("#517fa4")), 0, tName.length(), 0);
                        t.setSpan(bSpan, 0, tName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        uniList.add(t);

                        displayedC += tName + " " +temp+"\n";

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            CharSequence d = "";
            for(int a = 0; a < uniList.size(); a++){
                d = TextUtils.concat(d,uniList.get(a));
            }
            comments.setText(d);
            // Populate the data into the template view using the data object

            // Return the completed view to render on screen
            return convertView;
        }
    }
    public class FetchAjax extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String ajax = "http://thewotimes.com/Y/user.php?user="+getString(R.string.uniser)+"&type=instagram&next="+nextPageToken;
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

            String filename = getArguments().getString("username")+"-instagram.txt";
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
