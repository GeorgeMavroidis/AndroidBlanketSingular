package com.georgemavroidis.feed.AdapterPackage;

/**
 * Created by george on 14-10-20.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.georgemavroidis.feed.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BlogFragment extends Fragment {

    private ProgressDialog pDialog;
    private String url = "";

    // Tab titles
    JSONArray contacts = null;
    JSONObject credentials;
    JSONArray items = null;
    JSONObject json_dic = null;
    // JSON Node names
    private static final String BLOG = "blog";
    String res;
    String nextPageToken;
    Boolean fetching = false;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    public static BlogFragment newInstance(String username) {
        BlogFragment f = new BlogFragment();
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
        String newurl = "http://thewotimes.com/Y/current.php?user=zoella&type=blog&get=true";

//        new GetContacts("http://www.google.ca").execute();
//        fetching = true;

        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("http://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });
//        ListView listView = (ListView) getView().findViewById(R.id.sampleListView);
        myWebView.loadUrl("http://www.zoella.co.uk");
//        new GetContacts(newurl).execute();

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TableLayout table = (TableLayout) getView().findViewById(R.id.row);
//

    }









}
