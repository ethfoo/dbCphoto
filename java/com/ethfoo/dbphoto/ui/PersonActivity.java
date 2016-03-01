package com.ethfoo.dbphoto.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethfoo.dbphoto.R;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

public class PersonActivity extends ActionBarActivity {
    private static final String TAG = "PersonActivity";

    public static final String USR_ID = "usr id";

    private ActionBar mActionBar;
    private String mId;

    private ImageView avatar;
    private TextView locationName;
    private ImageView locationImage;
    private TextView desc;
    private TextView usrName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        mActionBar = getSupportActionBar();
        mActionBar.show();

        Intent intent = getIntent();
        mId = intent.getStringExtra(USR_ID);

        avatar = (ImageView) findViewById(R.id.person_image);
        locationImage = (ImageView) findViewById(R.id.person_locate_image);
        locationName = (TextView) findViewById(R.id.person_locate_name);
        desc = (TextView) findViewById(R.id.person_desc);
        usrName = (TextView) findViewById(R.id.person_name);

//        locationImage.setImageResource(android.R.drawable.ic_menu_mylocation);



        Ion.with(this).load("https://api.douban.com/v2/user/"+mId)
           .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if( e!=null || result == null){
                    Log.e(TAG, "ERROR in parsing json");
                    return;
                }

                Log.e(TAG, result.toString());
                if(result.get("id") != null){
                    String avatarUrl = result.get("large_avatar").getAsString();
                    Picasso.with(avatar.getContext()).load(avatarUrl).into(avatar);
                    String loc = result.get("signature").getAsString();
                    if(loc != null){
                        locationName.setText(loc);
                    }

                    usrName.setText(result.get("name").getAsString());
                    desc.setText(result.get("desc").getAsString());
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person, menu);
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
