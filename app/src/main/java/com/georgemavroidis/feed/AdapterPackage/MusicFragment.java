package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.georgemavroidis.feed.DownloadMusic;
import com.georgemavroidis.feed.R;
import com.georgemavroidis.feed.ServiceHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicFragment extends Fragment {

    private ProgressDialog pDialog;
    private String url = "";
    private static final String ns = null;

    // Tab titles
    JSONArray contacts = null;
    JSONObject credentials;
    JSONArray items = null;
    JSONObject json_dic = null;
    ListView listView;
    String type = "album";
    // JSON Node names
    private static final String INSTAGRAM = "instagram";
    String res;
    String nextPageToken;
    Boolean fetching = false;
    ArrayList<HashMap<String, String>> result;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    MusicAdapter adapter;
    ArrayList<String> singleTitle = new ArrayList();
    ArrayList<String> singleLink = new ArrayList();
    ProgressDialog progressDialog;
    public static MusicFragment newInstance(String username) {
        MusicFragment f = new MusicFragment();
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


        View rootView = inflater.inflate(R.layout.music_layout, container, false);
        Log.d("t",getArguments().getString("username"));
        String newurl = "http://thewotimes.com/Y/current.php?user="+R.string.uniser+"&type="+type+"&get=true";
        ImageView singleView = (ImageView) rootView.findViewById(R.id.cover_image);
        String getImage = "https://s3.amazonaws.com/georgem/Y/artists/troyesivan/trxye.jpg";
        Picasso.with( rootView.getContext()).load(getImage).into(singleView);

        WindowManager windowManager = (WindowManager) rootView.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display =  windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        singleView.getLayoutParams().height = size.x;
        singleView.getLayoutParams().width = size.x;

        listView = (ListView) rootView.findViewById(R.id.sampleListView);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) listView
                .getLayoutParams();
        if(size.x > size.y){
            mlp.setMargins(0, size.y-100, 0, 0);

        }else{
            mlp.setMargins(0, size.x-100, 0, 0);
        }

//        new GetContacts("http://www.google.ca").execute();
//        fetching = true;

        new GetContacts().execute();

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TableLayout table = (TableLayout) getView().findViewById(R.id.row);
//

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        private GetContacts(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String url = "http://thewotimes.com/Y/user.php?user="+getString(R.string.uniser)+"&type="+type;
            String music = sh.makeServiceCall(url, ServiceHandler.GET);

            if(type == "album"){
                Log.d("music", "Music " +music);
                try {
                    JSONArray t = new JSONArray(music);
                    JSONArray alb = t.getJSONObject(0).getJSONArray("album");

                    Log.d("a", alb.toString());



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else {
                XMLParser parser = new XMLParser(); // the parser create as seen in the Gist from GitHub
                String xml = parser.getXmlFromUrl(url); // getting XML from URL
                Document doc = parser.getDomElement(xml); // getting DOM element
//            Log.d("d", xml);
                NodeList nl = doc.getElementsByTagName("item");
                Log.d("a", nl.getLength() + "");
//
//            // looping through all song nodes <venue>
                for (int i = 0; i < nl.getLength(); i++) {
                    Log.d("a", i + "");
                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    Element e = (Element) nl.item(i);
                    // adding each child node to HashMap key => value
                    String KEY_ID = i + "";
                    String KEY_TITLE = "title";
                    String GUID = "guid";

                    map.put(KEY_ID, parser.getValue(e, KEY_ID));
                    map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
                    map.put(GUID, parser.getValue(e, GUID));
                    singleTitle.add((nl.getLength() - i) + ". " + parser.getValue(e, KEY_TITLE));
                    singleLink.add(parser.getValue(e, GUID));


                    // adding HashList to ArrayList
//                result.add(map);
                }
            }
//            List<Entry> entries = null;
//            InputStream stream = new ByteArrayInputStream(xml.getBytes());
//            try {
//                entries = parse(stream);
//                Log.d("e", entries.toString());
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return exampleList;

            return null;
        }

        public List parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }
        private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
            List entries = new ArrayList();

            parser.require(XmlPullParser.START_TAG, ns, "rss");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("item")) {
                    Log.d("a", "aster");
//                    entries.add(readEntry(parser));
                } else {
//                    skip(parser);
                }
            }
            return entries;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            Log.d("t", result.toString());

            ArrayList<MusicCell> arrayOfUsers = new ArrayList<MusicCell>();
//// Create the adapter to convert the array to views
            adapter = new MusicAdapter(getView().getContext(), arrayOfUsers);
//// Attach the adapter to a ListView
            listView = (ListView) getView().findViewById(R.id.sampleListView);
            listView.setAdapter(adapter);
//
//
//
            populateAdapter();
//            fetching = false;
        }


    }
    public class MusicCell {
        public String theId;
        public String theImage;
        public String theTitle;

        public MusicCell(String theId, String theImage, String theTitle) {
            this.theId = theId;
            this.theImage = theImage;
            this.theTitle = theTitle;
        }
    }

    public class MusicAdapter extends ArrayAdapter<MusicCell> {
        public MusicAdapter(Context context, ArrayList<MusicCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            MusicCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_music_layout, parent, false);
            }
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
            titleTextView.setText(singleTitle.get(position));

            if(position == 1){
                titleTextView.setBackgroundResource(R.drawable.rounded_corner);
                GradientDrawable drawable = (GradientDrawable) titleTextView.getBackground();

            }



            // Populate the data into the template view using the data object

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private DownloadMusic diTask;
    public void populateAdapter(){
        for(int i = 0; i < singleTitle.size(); i ++){

            MusicCell temp = new MusicCell("1", singleTitle.get(i), singleLink.get(i));
            adapter.add(temp);

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                if (diTask != null) {
                    AsyncTask.Status diStatus = diTask.getStatus();
                    Log.v("doClick", "diTask status is " + diStatus);
                    if (diStatus != AsyncTask.Status.FINISHED) {
                        Log.v("doClick", "... no need to start a new task");
                        return;
                    }
                }

                diTask = new DownloadMusic(getView().getContext());
                diTask.execute(singleLink.get(position));



            }
        });
    }





    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;

        private Entry(String title, String summary, String link) {
            this.title = title;
            this.summary = summary;
            this.link = link;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(title, summary, link);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
