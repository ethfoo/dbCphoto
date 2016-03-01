package com.ethfoo.dbphoto.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.Constants;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private SharedPreferences mPreference;
    public String mAccessToken;
    private String mMyUsrId;
    private static final int REQUEST_CODE = 0X11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if( (mAccessToken = mPreference.getString(Constants.ACCESS_TOKEN, null)) == null){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Log.e(TAG, "in run" );
                    Intent mainIntent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }

            }, 2000);
        }




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE){
            if(resultCode == LoginActivity.RESULT_OK){
                mAccessToken = data.getStringExtra(Constants.ACCESS_TOKEN);
                mMyUsrId = data.getStringExtra(Constants.USR_ID);

                Log.e(TAG, "ACCESS_TOKEN----->" + mAccessToken);

                final SharedPreferences.Editor editor = mPreference.edit();
                editor.putString(Constants.ACCESS_TOKEN, mAccessToken).apply();
                editor.putString(Constants.USR_ID, mMyUsrId).apply();

                Ion.with(this)
                        .load("https://api.douban.com/v2/user/~me")
                        .addHeader("Authorization", "Bearer " + mAccessToken)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null || result == null) {
                                    Log.e(TAG, "error in parsing json -- usr");
                                    return;
                                }
                                Log.e(TAG, "usr--me-->" + result.toString());
                                if (result.get("id") != null) {
                                    String myName = result.get("name").getAsString();
                                    String avatar = result.get("avatar").getAsString();
                                    String id = result.get("id").getAsString();

                                    editor.putString(Constants.USR_ME_NAME, myName).apply();
                                    editor.putString(Constants.USR_ME_AVATAR, avatar).apply();
                                    editor.putString(Constants.USR_ID, id).apply();



                                } else {

                                }

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        Log.e(TAG, "in run" );
                                        Intent mainIntent = new Intent(SplashActivity.this,
                                                MainActivity.class);
                                        SplashActivity.this.startActivity(mainIntent);
                                        SplashActivity.this.finish();
                                    }

                                }, 1000);

                            }
                        });

            }
        }
    }


}
