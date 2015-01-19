package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.georgemavroidis.feed.YoutubeActivity;
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
import java.util.ArrayList;
import java.util.HashMap;

public class YoutubeFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener  {

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
    String playlistID;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    YoutubeAdapter adapter;
    ListView listView;

    public static YoutubeFragment newInstance(String username) {
        YoutubeFragment f = new YoutubeFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        f.setArguments(args);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        return f;
    }

    public static final String API_KEY = "AIzaSyB0oGK4Ozx7-hAbRRIZXr5xLFnL9rpNGKs";
    public static final String VIDEO_ID = "o7VVHhK9zf0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_toplevel_youtube, container, false);
        Log.d("t",getArguments().getString("username"));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.black);

        listView = (ListView) rootView.findViewById(R.id.sampleListView);

        String tem = readFromFile();


        if(tem != ""){
            try {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                String playlist = sharedPref.getString("playlist-"+getArguments().getString("username")+"-youtube", "");
                playlistID = playlist;
                JSONArray t = new JSONArray(tem);

                    Log.d("length", ""+tem);
                json_dic = new JSONObject();
                for (int i = 0 ; i < t.length(); i++) {
                    JSONObject playlist_dict = t.getJSONObject(i);

                    String p = playlist_dict.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("playlistId");
                    Log.d("psasd" ,p +" " +playlist);
                    if(p.equals(playlist)){
                        json_dic = playlist_dict;
                    }

                }

                items = json_dic.getJSONArray("items");
                setAdapter();
                String nextToken = json_dic.get("nextPageToken").toString();
                nextPageToken = nextToken;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            String newurl = "http://www.thewotimes.com/Y/dig.php?user="+getString(R.string.uniser)+"&dig=playlist&type=youtube&username="+getArguments().getString("username");
            new GetContacts(newurl).execute();

        }

//
        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.youtubeplayerview, mYoutubePlayerFragment);
//        fragmentTransaction.commit();


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
        String newurl = "http://www.thewotimes.com/Y/dig.php?user="+getString(R.string.uniser)+"&dig=playlist&type=youtube&username="+getArguments().getString("username");
        Log.d("a", getArguments().getString("username"));
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


        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String playlist = sh.makeServiceCall(url, ServiceHandler.GET);
            if(playlist != null){
                playlistID = playlist;
                Log.d("pla", playlistID);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("playlist-"+getArguments().getString("username")+"-youtube",playlist);
                editor.commit();

                String posts = sh.makeServiceCall("http://thewotimes.com/Y/current.php?user="+getString(R.string.uniser)+"&type=youtube&get=true", ServiceHandler.GET);

                res = posts;
                Log.d("stuff", posts);
                if (posts != null) {
                    try {
                        String filename = getArguments().getString("username")+"-youtube.txt";
                        String string = posts;
                        FileOutputStream outputStream;

                        try {
                            outputStream = MainActivity.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(string.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JSONArray t = new JSONArray(posts);
//                    Log.d("length", ""+t.length());
                        for (int i = 0 ; i < t.length(); i++) {
                            JSONObject playlist_dict = t.getJSONObject(i);

//                        NSString *p =[[[[playlist_dict objectForKey:@"items"] objectAtIndex:0] objectForKey:@"snippet"] valueForKey:@"playlistId"];
                            String p = playlist_dict.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("playlistId");
//                        Log.d("p", p);
                            if(p.equals(playlist)){
                                json_dic = playlist_dict;
                            }
                            // do some work here on intValue
                        }

                        items = json_dic.getJSONArray("items");

                        String nextToken = json_dic.get("nextPageToken").toString();
                        nextPageToken = nextToken;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
            }
            else {
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


        ArrayList<YoutubeCell> arrayOfUsers = new ArrayList<YoutubeCell>();

        adapter = new YoutubeAdapter(getActivity().getBaseContext(), arrayOfUsers);
        listView.setAdapter(adapter);
        listView.setClickable(true);


        populateAdapter();

        mSwipeRefreshLayout.setRefreshing(false);

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
                    Log.i("SCROLLING UP","TRUE");
//                    getActivity().getActionBar().show();
                }
                mLastFirstVisibleItem=firstVisibleItem;


            }
        });
    }

    public void populateAdapter(){
        if(items != null) {
            int l = 0;
            if(items.length() > 10){
                l = items.length() -10;
            }
            for (int i = l; i < items.length(); i++) {

                try {
                    JSONObject temp_items = items.getJSONObject(i);
                    JSONObject thumbnail_array = temp_items.getJSONObject("snippet").getJSONObject("thumbnails");
                    String youtube_image = "";
                    if (thumbnail_array.getJSONObject("standard") != null) {
                        youtube_image = thumbnail_array.getJSONObject("standard").getString("url");
                    } else {
                        youtube_image = thumbnail_array.getJSONObject("default").getString("url");
                    }
                    String ytitle = temp_items.getJSONObject("snippet").getString("title");

                    YoutubeCell temp = new YoutubeCell("1", youtube_image, ytitle);
                    adapter.add(temp);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("test", items.length() +"");
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // When clicked, show a toast with the TextView text or do whatever you need.
                    try {
                        JSONObject temp_items = items.getJSONObject(position);
                        String ida = temp_items.getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
//                        MainActivity.setYoutubeVideo(ida);
//                        getActivity().getActionBar().hide();
                        Intent intent = new Intent(MainActivity.getAppContext(), YoutubeActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("vidid", ida);
                        Log.d("A", position + "");
                        intent.putExtras(extras);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
    public class YoutubeCell {
        public String theId;
        public String theImage;
        public String theTitle;

        public YoutubeCell(String theId, String theImage, String theTitle) {
            this.theId = theId;
            this.theImage = theImage;
            this.theTitle = theTitle;
        }
    }

    public class YoutubeAdapter extends ArrayAdapter<YoutubeCell> {
        public YoutubeAdapter(Context context, ArrayList<YoutubeCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            YoutubeCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.youtuber_layout, parent, false);
            }
            // Lookup view for data population
            ImageView yimg = (ImageView)convertView.findViewById(R.id.youtube_image);
            TextView t = (TextView) convertView.findViewById(R.id.youtube_title);
            Picasso.with(getContext()).load(cell.theImage).into(yimg);
            t.setText(cell.theTitle);

            // Populate the data into the template view using the data object

            // Return the completed view to render on screen
            return convertView;
        }
    }
    public class FetchAjax extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            String ajax = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=10&playlistId="+playlistID+"&key=AIzaSyDHOIJeg4dXJIBs2pu9aco3zF42ilgosxs&pageToken="+nextPageToken;
            String t = sh.makeServiceCall(ajax, ServiceHandler.GET);
            Log.d("test", ajax);
            if(t != null){
                try {


                    JSONObject json_dic = new JSONObject(t);
                    nextPageToken = json_dic.getString("nextPageToken");
                    JSONArray temp = json_dic.getJSONArray("items");
                    //=items
                    JSONArray locallist =  new JSONArray();
                    for (int i = 0; i <items.length(); i++) {
                        locallist.put(items.getJSONObject(i));
                    }
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

            String filename = getArguments().getString("username")+"-youtube.txt";
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