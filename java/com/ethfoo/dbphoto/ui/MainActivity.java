package com.ethfoo.dbphoto.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.Constants;
import com.ethfoo.dbphoto.data.Fragments;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.model.NavigationDrawerItem;
import com.ethfoo.dbphoto.ui.fragments.FragmentAbout;
import com.ethfoo.dbphoto.ui.fragments.FragmentOne;
import com.ethfoo.dbphoto.ui.fragments.FragmentThree;
import com.ethfoo.dbphoto.ui.fragments.FragmentTwo;
import com.ethfoo.dbphoto.ui.navigationdrawer.NavigationDrawerView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "main_activity";

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final int REQUEST_CODE = 0X11;

    public String mAccessToken;
    private String mMyUsrId;
    private SharedPreferences mPreference;

    private int currentSelectedPosition = 0;

    @InjectView(R.id.navigationDrawerListViewWrapper)
    NavigationDrawerView mNavigationDrawerListViewWrapper;

    @InjectView(R.id.linearDrawer)
    LinearLayout mLinearDrawerLayout;

    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.leftDrawerListView)
    ListView leftDrawerListView;

    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;

    private CharSequence mDrawerTitle;

    private List<NavigationDrawerItem> navigationItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mTitle = mDrawerTitle = getTitle();
        getSupportActionBar().setIcon(R.drawable.ic_action_ab_transparent);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame,
                    Fragment.instantiate(MainActivity.this, Fragments.Popular.getFragment())).commit();
        } else {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        navigationItems = new ArrayList<>();

        navigationItems.add(new NavigationDrawerItem(getString(R.string.fragment_one), android.R.drawable.ic_menu_today, false));
        navigationItems.add(new NavigationDrawerItem(getString(R.string.fragment_two), android.R.drawable.btn_star_big_off,false));
        navigationItems.add(new NavigationDrawerItem(getString(R.string.fragment_three), android.R.drawable.ic_menu_gallery, false));
        navigationItems.add(new NavigationDrawerItem(getString(R.string.fragment_about),
                android.R.drawable.ic_menu_info_details, false));
//        R.drawable.ic_action_about
        mNavigationDrawerListViewWrapper.replaceWith(navigationItems);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mPreference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        if( (mAccessToken = mPreference.getString(Constants.ACCESS_TOKEN, null)) == null){
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//        }else {
//
//        }

        selectItem(currentSelectedPosition);


//        downloadJson();


        String name, url;
        if( (name = mPreference.getString(Constants.USR_ME_NAME, "Me")) != null && (url = mPreference.getString(Constants.USR_ME_AVATAR, null)) != null){
            final String id = mPreference.getString(Constants.USR_ID, null);
            TextView tvName = (TextView) findViewById(R.id.drawerUserName);
            tvName.setText(name);
            ImageView ivUsr = (ImageView) findViewById(R.id.drawerUserImage);
            Picasso.with(this).load(url).into(ivUsr);

            Log.e(TAG, "preferences != null");



        }


//        View drawHeader = findViewById(R.id.drawerLayout);
//        drawHeader.setClickable(true);
////        drawHeader.setFocusable();
//        drawHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "drawHeader on click");
////                Intent intent = new Intent(MainActivity.this, PersonActivity.class);
////                intent.putExtra(PersonActivity.USR_ID, id);
////                startActivity(intent);
//            }
//        });
//
//        mNavigationDrawerListViewWrapper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "mNavigationDrawerListViewWrapper on click");
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.leftDrawerListView)
    public void OnItemClick(int position, long id) {
        if (mDrawerLayout.isDrawerOpen(mLinearDrawerLayout)) {
            mDrawerLayout.closeDrawer(mLinearDrawerLayout);
            onNavigationDrawerItemSelected(position);

            selectItem(position);
        }
    }

    private void selectItem(int position) {

        if (leftDrawerListView != null) {
            leftDrawerListView.setItemChecked(position, true);

            navigationItems.get(currentSelectedPosition).setSelected(false);
            navigationItems.get(position).setSelected(true);

            currentSelectedPosition = position;
            getSupportActionBar()
                    .setTitle(navigationItems.get(currentSelectedPosition).getItemName());
        }

        if (mLinearDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mLinearDrawerLayout);
        }
    }

    private void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                if (!(getSupportFragmentManager().getFragments()
                        .get(0) instanceof FragmentOne)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, Fragment
                                    .instantiate(MainActivity.this, Fragments.Popular.getFragment()))
                            .commit();
                }
                break;
            case 1:
                if (!(getSupportFragmentManager().getFragments().get(0) instanceof FragmentTwo)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, Fragment
                                    .instantiate(MainActivity.this, Fragments.Liked.getFragment()))
                            .commit();
                }
                break;
            case 2:
                if (!(getSupportFragmentManager().getFragments().get(0) instanceof FragmentThree)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, Fragment
                                    .instantiate(MainActivity.this, Fragments.Recommend.getFragment()))
                            .commit();
                }
                break;
            case 3:
                if (!(getSupportFragmentManager().getFragments().get(0) instanceof FragmentAbout)) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, Fragment
                                    .instantiate(MainActivity.this, Fragments.ABOUT.getFragment()))
                            .commit();
                }
                break;
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

                                    TextView tvName = (TextView) findViewById(R.id.drawerUserName);
                                    ImageView ivMe = (ImageView) findViewById(R.id.drawerUserImage);

                                    Picasso.with(MainActivity.this).load(avatar).into(ivMe);
                                    tvName.setText(myName);

                                    selectItem(0);

                                } else {

                                }

                            }
                        });

            }
        }
    }



}
