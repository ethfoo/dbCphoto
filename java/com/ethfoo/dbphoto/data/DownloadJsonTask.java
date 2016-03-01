package com.ethfoo.dbphoto.data;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/5/15.
 */
public class DownloadJsonTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "DownloadJsonTask";

    private Item item;

    public DownloadJsonTask(Item item){
        this.item = item;
    }

    @Override
    protected String doInBackground(String... urls) {
        String url = urls[0];
        String result;

        try{
            result = downloadFromNetWork(url);

            return result;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



    private String downloadFromNetWork(String url) throws IOException{
        InputStream inputStream = null;
        String str = "";

        try{
            inputStream = downloadUrl(url);
            str = readIn(inputStream);
        }finally {
            if (inputStream != null){
                inputStream.close();
            }
        }

        return str;

    }

    private String readIn(InputStream inputStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String result = "";
        String line = "";
        while ( (line=reader.readLine()) != null){
            result += line;
        }
        return result;
    }

    private InputStream downloadUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();

    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
        Log.e(TAG, "onPostExecute" + str);
        JSONObject object = null;

        try {
            object = new JSONObject(str);
            Log.e(TAG, "onPostExecute" + object.getLong("id") + object.getString("desc"));

            item.setId(object.getString("id"));
            item.setImageUrl(object.getString("image"));
            item.setDesc(object.getString("desc"));
            item.setIsLiked(object.getBoolean("liked"));
            item.setLikedCount(object.getLong("liked_count"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
