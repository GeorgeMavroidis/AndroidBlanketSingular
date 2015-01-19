package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import com.georgemavroidis.feed.R;
import com.georgemavroidis.feed.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TumblrFragment extends Fragment {

    private ProgressDialog pDialog;
    private String url = "";

    // Tab titles
    JSONArray contacts = null;
    JSONObject credentials;
    JSONArray items = null;
    JSONObject json_dic = null;
    // JSON Node names
    private static final String TUMBLR = "tumblr";
    String res;
    String nextPageToken;
    Boolean fetching = false;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    TumblrAdapter adapter;
    public static TumblrFragment newInstance(String username) {
        TumblrFragment f = new TumblrFragment();
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


        View rootView = inflater.inflate(R.layout.tumblr_layout, container, false);
        Log.d("t",getArguments().getString("username"));
        String newurl = "http://thewotimes.com/Y/current.php?user=tyleroakley&type=tumblr&get=true";

//        new GetContacts("http://www.google.ca").execute();
//        fetching = true;

        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
//        ListView listView = (ListView) getView().findViewById(R.id.sampleListView);
        myWebView.loadUrl("http://taylorswift.tumblr.com");
//        new GetContacts(newurl).execute();

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TableLayout table = (TableLayout) getView().findViewById(R.id.row);
//

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
            String tumblr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("posts", tumblr);
            if (tumblr != null) {
                try {
                    JSONArray t = new JSONArray(tumblr);

//                    nextPageToken = t.getJSONObject(t.length()-1).getJSONObject("entry").getString("id");
                    nextPageToken = "none";
//                    Log.d("nexPAgeToken", nextPageToken);
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
            if (pDialog.isShowing())
                pDialog.dismiss();
//                System.out.println(result);
//                res= result;
            ArrayList<TumblrCell> arrayOfUsers = new ArrayList<TumblrCell>();
// Create the adapter to convert the array to views
            adapter = new TumblrAdapter(getView().getContext(), arrayOfUsers);
// Attach the adapter to a ListView


//            populateAdapter();
            fetching = false;
        }


    }
    public void populateAdapter(){
        for(int i = 0; i < items.length(); i ++){

            try {
                JSONObject temp_items = items.getJSONObject(i).getJSONObject("entry");
                String username = temp_items.getString("blog_name");
                String caption = temp_items.getString("caption");
                JSONObject photos = temp_items.getJSONObject("photos");
                JSONArray tags = temp_items.getJSONArray("tags");
                String note_count = temp_items.getString("note_count");
                String likes = temp_items.getString("liked");
                Log.d("a",temp_items.toString());


                TumblrCell temp = new TumblrCell("1", "", "", "", "", "img", "", "", "", "");
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
        long secondsBetweenDates = different / secondsInMilli;
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
    public class TumblrCell {
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

        public TumblrCell(String theId, String pp, String user, String atUser, String msg, String img, String ret, String fav, String time, String main_image) {
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

        }
    }
    public class TumblrAdapter extends ArrayAdapter<TumblrCell> {
        public TumblrAdapter(Context context, ArrayList<TumblrCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TumblrFragment.TumblrCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            Log.d("here", "ABOVE");
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.tumblr_layout, parent, false);
            }
            WebView myWebView = (WebView) convertView.findViewById(R.id.webview);
            myWebView.loadUrl("http://zoella.tumblr.com");
            Log.d("here", "here");

            return convertView;
        }
    }
    public class FetchAjax extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String ajax = "http://thewotimes.com/Y/user.php?user=zoella&type=tumblr&next="+nextPageToken;
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

}
