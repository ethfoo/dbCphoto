package com.ethfoo.dbphoto.data;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.ethfoo.dbphoto.ui.DetailActivity;
import com.ethfoo.dbphoto.ui.fragments.DetailFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;


public class DetailFragmentAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "DetailFragmentAdapter";
    private List<Item> mItemList;
    private Context mContext;

    public DetailFragmentAdapter(Context context, FragmentManager fm, List<Item> itemList) {
        super(fm);
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public Fragment getItem(final int position) {

//        Item thisItem = mItemList.get(position);

//        View view = fragment.onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState);

//        for(int i = position; i<position+5&&i<getCount(); i++){


//        }

        Log.e(TAG, "getItem---->position" + position);
        return DetailFragment.newInstance(position, mItemList.get(position));
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

}
