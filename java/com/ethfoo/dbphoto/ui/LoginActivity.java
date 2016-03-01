package com.ethfoo.dbphoto.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends ActionBarActivity {

    private WebView webView;
    private static final String TAG = "---LoginActivity---";
    public static final int RESULT_OK = 0x22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = (WebView) findViewById(R.id.login_webView);

        WebViewClient webViewClient = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("oauthflow")){

                    Uri uri = Uri.parse(url);
                    Log.e(TAG, "URL---->"+ url);
//                    String query = uri.getQuery();

//                    if(query.equals("code")){

                        String code = uri.getQueryParameter("code");
                    Log.e(TAG, "code -->"+code);
                        String str = "https://www.douban.com/service/auth2/token?" +
                                "client_id=0fefb8e9cfcb2c9b2fcf2abd6f6185d1&" +
                                "client_secret=a77feed85bf4353b&" +
                                "redirect_uri=oauthflow://callback&" +
                                "grant_type=authorization_code&" +
                                "code="+code;
                        new MyTask().execute(str);
//                    }
//                    else {
//
//                        Log.e(TAG, "finish with error--- "+ "query-->"+query+"--"+uri.getQueryParameter(query));
//                        finish();
//                    }


                    return true;

                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);

        String url = "https://www.douban.com/service/auth2/auth?" +
                "client_id=0fefb8e9cfcb2c9b2fcf2abd6f6185d1&" +
                "redirect_uri=oauthflow://callback&" +
                "response_type=code&"+
                "scope=community_basic_photo,douban_basic_common";
        webView.loadUrl(url);

    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder str = new StringBuilder();
                String line = "";
                while ( (line=reader.readLine()) != null){
                    str.append(line);
                }

                return str.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                String accessToken = object.getString("access_token");
                String userId = object.getString("douban_user_id");

                Log.e(TAG, "accessToken---->"+accessToken);

//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                preferences.edit().putString(Constants.ACCESS_TOKEN, accessToken).apply();

                Intent intent = new Intent();

                intent.putExtra(Constants.ACCESS_TOKEN, accessToken);
                intent.putExtra(Constants.USR_ID, userId);

                setResult(RESULT_OK, intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
