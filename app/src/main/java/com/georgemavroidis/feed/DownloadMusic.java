package com.georgemavroidis.feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by george on 14-11-19.
 */
public class DownloadMusic extends AsyncTask <String, Integer, String> {
    private Context mContext;
    Boolean download;

    ProgressDialog progressDialog;
    String[] paths;



    public DownloadMusic(Context context) {


        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching PsychoBabble!");
        progressDialog.setMessage("This might take a minute...");
        progressDialog.show();


    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return downloadIt(strings);
        } catch (IOException e) {
            return null;
        }
    }
    protected void onProgressUpdate(Integer... progress) {
        Log.v("onProgressUpdate", "Progress so far: " + progress[0]);

    }
    private String downloadIt(String... urls) throws IOException {
        ServiceHandler sh = new ServiceHandler();
//        String music = sh.makeServiceCall(urls[0], ServiceHandler.GET);
        paths = urls[0].split("/");

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/"+paths[paths.length-1]);
        if(file.exists()){
            Log.d("exists", "exists");
        }else{
            download = downloadFile(urls[0], file);
            Log.d("a", "downloaded?" + download.booleanValue() + " " + paths[paths.length-1]);

        }

        return null;
    }
    private static Boolean downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();


            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();

            return true;
        } catch(FileNotFoundException e) {
            return false; // swallow a 404
        } catch (IOException e) {
            return false; // swallow a 404
        }
    }

    protected void onPostExecute(String result) {

        Log.d("d", "finishedddd");
        progressDialog.dismiss();

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/"+paths[paths.length-1]);
        MainActivity.playMusic(paths[paths.length-1]);

    }
}
