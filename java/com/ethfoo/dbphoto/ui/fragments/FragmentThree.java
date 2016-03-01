package com.ethfoo.dbphoto.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.Constants;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.ItemProvider;
import com.ethfoo.dbphoto.data.MyDBHelper;
import com.ethfoo.dbphoto.ui.DetailActivity;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FragmentThree extends Fragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "--FragmentThree--";
//    @InjectView(R.id.circleLayout)

    private StaggeredGridView mGridView;
    private GridAdapter mAdapter;
    private Set<String> albumSet;
    private List<Item> recommendList;

    private MyDBHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "on Created");

        dbHelper = new MyDBHelper(getActivity(), Constants.DB_NAME, null, 1 );
        db = dbHelper.getReadableDatabase();

        recommendList = new ArrayList<>();

        recommendList.add(new Item("1153023561"));
        recommendList.add(new Item("2247240981"));
        recommendList.add(new Item("2164810818"));
        recommendList.add(new Item("2207523208"));
        recommendList.add(new Item("2171113819"));
        recommendList.add(new Item("2183043876"));


        albumSet = new LinkedHashSet<>();
        Cursor cursor = db.query(Constants.DB_NAME, new String[]{Constants.DB_ALBUM_ID},
                                null, null, null, null, Constants.DB_CREATE_TIME+" DESC");

        if (cursor != null){
            if(cursor.moveToFirst()){
                albumSet.add(cursor.getString(cursor.getColumnIndex(Constants.DB_ALBUM_ID)));
                for ( int i=0; (i<5) && cursor.moveToNext(); i++){
                    albumSet.add(cursor.getString(cursor.getColumnIndex(Constants.DB_ALBUM_ID)));
                }
            }else {
                Toast.makeText(getActivity(), getString(R.string.no_star_yet), Toast.LENGTH_SHORT).show();
            }


        }
        cursor.close();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, containter, false);
        Log.e(TAG, "on CreateView");
        ButterKnife.inject(this, view);
//        ((GradientDrawable) circleLayout.getBackground())
//                .setColor(getResources().getColor(R.color.material_purple));


        mGridView = (StaggeredGridView) view.findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);

        getInitPhotos();

        Iterator<String> iterator = albumSet.iterator();
        while (iterator.hasNext()){
            Ion.with(FragmentThree.this).load("https://api.douban.com/v2/album/"+iterator.next()+"/photos"+"?start=0"+"&count=5")
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if( e!= null || result == null){
                        Log.e(TAG, "error in parsing json");
                        return;
                    }

                    if (result.get("start") != null) {

//                        Log.e(TAG, result.toString());
                        JsonArray photosArray = result.get("photos").getAsJsonArray();
                        for(int i=0; i<photosArray.size(); i++){
                            JsonObject photo = photosArray.get(i).getAsJsonObject();
                            String id = photo.get("id").getAsString();
                            String image = photo.get("image").getAsString();
                            Long likedCount = photo.get("liked_count").getAsLong();
                            String desc = photo.get("desc").getAsString();
                            String usrName = photo.get("author").getAsJsonObject().get("name").getAsString();
                            String avatar = photo.get("author").getAsJsonObject().get("avatar").getAsString();
                            String albumId = photo.get("album_id").getAsString();
                            String usrId = photo.get("author").getAsJsonObject().get("id").getAsString();

                            Item item = new Item();
                            item.setId(id);
                            item.setImageUrl(image);
                            item.setLikedCount(likedCount);
                            item.setDesc(desc);
                            item.setUserName(usrName);
                            item.setUserPhotoUrl(avatar);
                            item.setAlbumId(albumId);
                            item.setUsrId(usrId);

                            recommendList.add(item);

                        }
                        mAdapter.notifyDataSetChanged();
                    }


                }
            });
        }



        return view;
    }

    void getInitPhotos(){
        for( int i=0; i<recommendList.size(); i++){
            final Item item = recommendList.get(i);
            Ion.with(this).load(item.getJsonUrl()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e != null || result == null) {
                        Log.e(TAG, "Error in parsing Json");
                        return;
                    }

                    if (result.get("image") != null) {
                        item.setUsrId(result.get("author").getAsJsonObject().get("id").getAsString());
                        item.setImageUrl(result.get("image").getAsString());
                        item.setLikedCount(result.get("liked_count").getAsLong());
                        item.setDesc(result.get("desc").getAsString());
                        item.setUserName(result.get("author").getAsJsonObject().get("name").getAsString());
                        item.setUserPhotoUrl(result.get("author").getAsJsonObject().get("avatar").getAsString());
                        item.setAlbumId(result.get("album_id").getAsString());

                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, result.toString());
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Item item = (Item) adapterView.getItemAtPosition(position);

        // Construct an Intent as normal
        Intent intent = new Intent(getActivity(), DetailActivity.class);
//        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId());
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, recommendList.indexOf(item));
        intent.putExtra(DetailActivity.EXTRA_RECOMMEND_LIST, (Serializable) recommendList);


        // BEGIN_INCLUDE(start_activity)
        /**
         * Now create an {@link android.app.ActivityOptions} instance using the
         * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
         * method.
         */
//        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                getActivity(),
//
////                 Now we provide a list of Pair items which contain the view we can transitioning
////                 from, and the name of the view it is transitioning to, in the launched activity
//                new Pair<View, String>(view.findViewById(R.id.imageview_item),
//                        DetailActivity.VIEW_NAME_HEADER_IMAGE),
//                new Pair<View, String>(view.findViewById(R.id.detail_textView_user),
//                        DetailActivity.VIEW_NAME_HEADER_TITLE));


        // Now we can start the Activity, providing the activity options as a bundle
//        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
        startActivity(intent);
        Log.e(TAG, "START DetailActivity");
        // END_INCLUDE(start_activity)
    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            Log.e(TAG, "getCount--->"+  itemList.size()+"");
            return recommendList.size();
        }

        @Override
        public Item getItem(int position) {

//            Log.e(TAG, "getItem ImageUrl---->" + itemList.get(position));
            return recommendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            Item item = getItem(position);

            ImageView imageItem = (ImageView) view.findViewById(R.id.imageview_item);
            ImageView imageUser = (ImageView) view.findViewById(R.id.imageView_user);
            TextView textUser = (TextView) view.findViewById(R.id.textView_user);
            ImageView imageIsLiked = (ImageView) view.findViewById(R.id.imageView_isliked);
            TextView textCount = (TextView) view.findViewById(R.id.textView_liked_count);
            TextView desc = (TextView) view.findViewById(R.id.grid_item_desc);

            Picasso.with(getActivity()).load(item.getImageUrl()).into(imageItem);
            Picasso.with(getActivity()).load(item.getUserPhotoUrl()).into(imageUser);
            textUser.setText(item.getUserName());
            imageIsLiked.setImageResource(android.R.drawable.btn_star_big_off);
            textCount.setText(item.getLikedCount() + "");
            desc.setText(item.getDesc());

//            if(item.getImageUrl() == null) {
//                Ion.with(FragmentThree.this)
//                        .load(item.getJsonUrl())
////                        .addHeader("Authorization", "Bearer " + mAccessToken)
//                        .asJsonObject()
//                        .setCallback(new FutureCallback<JsonObject>() {
//                            @Override
//                            public void onCompleted(Exception e, JsonObject result) {
//                                if(e != null || result == null){
////                                    Log.e(TAG, "error when parsing json");
//                                    return;
//                                }
////                                Log.e(TAG, "result---->"+result.toString());
//                                if(result.get("image") != null){
//
//
//                                    String imageUrl = result.get("image").getAsString();
//                                    item.setImageUrl(imageUrl);
//                                    Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.ncut)
//                                            .into(imageItem);
//
//                                    item.setUserPhotoUrl(result.getAsJsonObject("author").get("avatar").getAsString());
//                                    Picasso.with(getActivity()).load(item.getUserPhotoUrl()).into(imageUser);
//
//                                    item.setUserName(result.getAsJsonObject("author").get("name").getAsString());
//                                    textUser.setText(item.getUserName());
//
//                                    item.setLikedCount(result.get("liked_count").getAsLong());
//                                    textCount.setText(item.getLikedCount()+"");
//
//                                }
//                                else {
//                                    String msg = result.get("msg").getAsString();
//                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        });
//            }else{
//                Picasso.with(getActivity()).load(item.getImageUrl()).placeholder(R.drawable.ncut)
//                        .into(imageItem);
////                Picasso.with(getActivity()).load(item.getUserPhotoUrl()).into(imageUser);
////                textUser.setText(item.getUserName());
////                textCount.setText(item.getLikedCount() + "");
//
//            }

            return view;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
