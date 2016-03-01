package com.ethfoo.dbphoto.ui.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.CommentItem;
import com.ethfoo.dbphoto.data.Constants;
import com.ethfoo.dbphoto.data.MyDBHelper;
import com.ethfoo.dbphoto.ui.CommentsActivity;
import com.ethfoo.dbphoto.ui.LoginActivity;
import com.ethfoo.dbphoto.ui.MainActivity;
import com.ethfoo.dbphoto.ui.PersonActivity;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.ItemProvider;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FragmentOne extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener{
    private static final String TAG = "fragment_one";


    private  static int threadEndCount;

    private List<Item> mItemList;
    private List<Item> mRefreshList;
    private List<Item> mLoadMoreList;
    private Map<String, ArrayList<CommentItem>> mCommentMap;
    public String mAccessToken;
    SharedPreferences mPreference;

    ListHeaderAdapter adapter;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private SwipeRefreshLayout swipeLayout;
    private boolean hadScolledEnd = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemList = ItemProvider.getEveryDayList();
       // mRefreshList = ItemProvider.getRefreshList();
        //mLoadMoreList = ItemProvider.getLoadMoreList();
        mCommentMap = new HashMap<>();

        mPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAccessToken = mPreference.getString(Constants.ACCESS_TOKEN, null);
        dbHelper = new MyDBHelper(getActivity(), Constants.DB_NAME, null, 1 );
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(Constants.DB_NAME, null, null, null,null,null,null);
        while (cursor.moveToNext()){
            Log.e(TAG, "cursor.id-->"+cursor.getString(cursor.getColumnIndex(Constants.DB_PHOTO_ID))+
                    "   cursor.albumId-->"+cursor.getString(cursor.getColumnIndex(Constants.DB_ALBUM_ID))+
                    "   cursor.time-->"+cursor.getString(cursor.getColumnIndex(Constants.DB_CREATE_TIME)));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, containter, false);
        ButterKnife.inject(this, view);

        StickyListHeadersListView stickyList = (StickyListHeadersListView)view.findViewById(R.id.everyday_list);
        stickyList.setOnScrollListener(this);
//        View footer = inflater.inflate(R.layout.fragment1_footer, null);
//        stickyList.addFooterView(footer);

//        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e(TAG, "view.id-->"+view.getId()+"--position-->"+position+"--id-->"+id);
//            }
//        });
//        stickyList.setFocusableInTouchMode(true);
//        View footbar = inflater.inflate(R.layout.listheader_footbar, stickyList, false);
//        stickyList.addFooterView(footbar);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adapter = new ListHeaderAdapter(getActivity());
        stickyList.setAdapter(adapter);


        loadMore(mItemList);
//        for(int i=0; i<mCommentList.size(); i++){
//            Log.e(TAG, "mItemList---->"+i+"---"+mCommentList.get(i).toString());
//        }


        return view;
    }

    private void loadMore(final List<Item> list ) {
            for(int i=0; i<list.size(); i++){
            final Item item = list.get(i);
            final int finalI = i;
            Ion.with(getActivity()).load(item.getJsonUrl()).addHeader("Authorization", "Bearer " + mAccessToken)
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e != null || result == null) {
                        Log.e(TAG, "Error in parsing Json");
                        return;
                    }

                    if (result.get("image") != null) {
                        item.setId(result.get("id").getAsString());
                        item.setAlbumId(result.get("album_id").getAsString());
                        item.setImageUrl(result.get("image").getAsString());
                        item.setUserPhotoUrl(result.getAsJsonObject("author").get("avatar").getAsString());
                        item.setUserName(result.getAsJsonObject("author").get("name").getAsString());
                        item.setUsrId(result.getAsJsonObject("author").get("id").getAsString());
                        item.setLikedCount(result.get("liked_count").getAsLong());
                        item.setRecsCount(result.get("recs_count").getAsLong());
                        item.setDesc(result.get("desc").getAsString());
                        item.setCommentCount(result.get("comments_count").getAsLong());

                        Log.e(TAG, "item" + finalI + "loaded");
                        loadComments(item, 0, 20);
//                        adapter.notifyDataSetChanged();
                        threadEndCount++;
//                        if ( toBeAddedList != null){
//                            toBeAddedList.addAll(0, list);
//                            Log.e(TAG, "toBeAddedList != null");
//                            Toast.makeText(getActivity(), "has load fresh photos", Toast.LENGTH_SHORT).show();
//                            swipeLayout.setRefreshing(false);
//                        }

                    } else {
                        Log.e(TAG, result.toString());
                    }
                }
            });

        }
    }

    void loadComments(Item i, int s, int c){
        final Item item = i;

        Ion.with(getActivity())
                .load(item.getCommentsJsonUrl()+"?start="+s+"&count="+c)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null || result == null) {
                            Log.e(TAG, "ERROR in parsing Json");
                            return;
                        }

                        if (result.get("start") != null) {
//                           Log.e(TAG, "----result---->" + result.toString());
//                            Log.e(TAG, "getAsJsonArray().size()--->"+result.get("comments").getAsJsonArray().size());

                            int start = result.get("start").getAsInt();
                            int count = result.get("count").getAsInt();
                            int total = result.get("total").getAsInt();

                            if(total == 0){
                                return;
                            }

                            ArrayList<CommentItem> list = new ArrayList<>();
                            JsonArray jsonArray = result.get("comments").getAsJsonArray();
                            for (int i=0; i<jsonArray.size() ;i++){
                                JsonObject object = jsonArray.get(i).getAsJsonObject();
                                JsonObject usr = object.get("author").getAsJsonObject();
                                String content = object.get("content").getAsString();
                                String usrImageUrl = usr.get("avatar").getAsString();
                                String usrName = usr.get("name").getAsString();
                                String usrId = usr.get("id").getAsString();

                                CommentItem ci = new CommentItem(usrImageUrl, usrName, content);
                                ci.setUsrId(usrId);
                                list.add(ci);

                            }
                            mCommentMap.put(item.getId(), list);
//                            Log.e(TAG, "mCommentMap" + finalI + "had loaded" + "---" + item.hashCode() + "--");
//                                for(int j=0; j<list.size(); j++){
//                                    Log.e(TAG, "mCommentMap--list"+finalI+"j-->"+j+"--content-->"+list.get(j).getContent());
//                                }

                            adapter.notifyDataSetChanged();


                        } else {
                            Log.e(TAG, "did not return correct Json"+"---"+result.toString());
                        }
                    }
                }) ;
    }

    @Override
    public void onRefresh() {
        //TODO cannot work!
//        Toast.makeText(getActivity(), "onRefresh...", Toast.LENGTH_SHORT).show();
//        Log.e(TAG, "onRefresh start");
//        loadMore(mRefreshList);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e(TAG, "onRefresh thread run..");
//                while (threadEndCount == mRefreshList.size()){
//                    mItemList.addAll(0, mRefreshList);
//                    adapter.notifyDataSetChanged();
//                    Toast.makeText(getActivity(), "has load fresh photos", Toast.LENGTH_SHORT).show();
//                    swipeLayout.setRefreshing(false);
//                    break;
//                }
//            }
//        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.e(TAG, "in run");
                Toast.makeText(getActivity(), "no fresh photos", Toast.LENGTH_SHORT).show();
                swipeLayout.setRefreshing(false);
            }

        }, 2000);


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if (!hadScolledEnd) {
//            if ((firstVisibleItem != 0) && ((firstVisibleItem + visibleItemCount) == totalItemCount)) {
//                Toast.makeText(getActivity(), "end", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, " the end ...  firstVisibleItem+visibleItemCount = " + (firstVisibleItem + visibleItemCount) +
//                        "   totalItemCount = " + totalItemCount);
//                hadScolledEnd = true;
//            }
//        }
    }


    class ListHeaderAdapter extends BaseAdapter implements StickyListHeadersAdapter{
        private LayoutInflater inflater;
        ListHeaderAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final Item item = getItem(position);

            View view = convertView;
            if(view == null){
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listheader_item, parent, false);
                holder.image = (ImageView) view.findViewById(R.id.listHeader_image);
                holder.image.setTag(item.getImageUrl());
                holder.tvRecsCount = (TextView) view.findViewById(R.id.listHeader_item_shareNum);
                holder.ivResc = (ImageView) view.findViewById(R.id.listHeader_item_shareImage);
                holder.tvLikedCount = (TextView) view.findViewById(R.id.listHeader_item_likedNum);
                holder.ivLiked = (ImageView) view.findViewById(R.id.listHeader_item_likedImage);
                holder.tvDesc = (TextView) view.findViewById(R.id.listHeader_item_desc);
                holder.tvCommentCount = (TextView) view.findViewById(R.id.listHeader_item_commentCount);
                holder.commentRootLayout = (LinearLayout) view.findViewById(R.id.listHeader_item_commentLayoutRoot);

                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
            }


            Log.e(TAG, "getView--->" + position);

            Picasso.with(holder.image.getContext()).load(item.getImageUrl()).into(holder.image);

            holder.tvRecsCount.setText(item.getRecsCount() + "");
            holder.tvLikedCount.setText(item.getLikedCount() + "");
            holder.tvDesc.setText(item.getDesc());
            holder.tvCommentCount.setText("all " + item.getCommentCount() + " comments ");

            holder.ivLiked.setClickable(true);
            holder.ivLiked.setImageResource(android.R.drawable.btn_star_big_off);
            //holder.ivLiked.setBackgroundColor(getResources().getColor(R.color.white));
            holder.ivLiked.setTag(false);

            holder.ivResc.setImageResource(android.R.drawable.ic_menu_share);
            holder.ivResc.setClickable(true);
            holder.ivResc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    Uri uri = Uri.parse(getItem(position).getImageUrl());
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                }
            });

            Cursor cursor = db.query(Constants.DB_NAME, new String[]{Constants.DB_PHOTO_ID}, Constants.DB_PHOTO_ID+" = ?", new String[]{item.getId()}, null,null, null);

            //find the same id in database, the photo is liked, click it to unlike it
            if(cursor.moveToFirst()){

                //holder.ivLiked.setBackgroundColor(getResources().getColor(R.color.red_dark));
                holder.ivLiked.setImageResource(android.R.drawable.btn_star_big_on);
                holder.ivLiked.setTag(true);

                final String id = cursor.getString(cursor.getColumnIndex(Constants.DB_PHOTO_ID));

                Log.e(TAG, "find the id in database" + "--of " + position + "id-->" + id);



            }
            //the photo hasn't been liked
            else {
                Log.e(TAG, "cursor == null");


            }


            holder.ivLiked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //the photo is liked, click to unlike
                    if (holder.ivLiked.getTag().equals(true)) {
                        db.delete(Constants.DB_NAME, Constants.DB_PHOTO_ID + " = ?", new String[]{item.getId()});
//                        holder.ivLiked.setBackgroundColor(getResources().getColor(R.color.white));
                        holder.ivLiked.setImageResource(android.R.drawable.btn_star_big_off);
                        Toast.makeText(getActivity(), "You has unlike it", Toast.LENGTH_SHORT).show();
                        holder.ivLiked.setTag(false);

                    }
                    //do not liked it yet, click to like
                    else {
                        Toast.makeText(getActivity(), "You like it", Toast.LENGTH_SHORT).show();

                        ContentValues values = new ContentValues();
                        values.put(Constants.DB_PHOTO_ID, item.getId());
                        values.put(Constants.DB_ALBUM_ID, item.getAlbumId());
                        values.put(Constants.DB_PHOTO_URL, item.getImageUrl());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
                        String time = dateFormat.format(new java.util.Date());

                        values.put(Constants.DB_CREATE_TIME, time);
                        db.insert(Constants.DB_NAME, null, values);

                        holder.ivLiked.setImageResource(android.R.drawable.btn_star_big_on);
                        holder.ivLiked.setTag(true);

                        Log.e(TAG, "HAD INSERT ");
                    }


                }
            });


            cursor.close();




            final ArrayList<CommentItem> list = mCommentMap.get(item.getId());
//            && holder.commentRootLayout.getTag().equals(position)
            if(list != null ){
//                Log.e(TAG, "comments of" + position + "---" + mCommentMap.get(item.getId()).toString());

//                for(int j=0; j<list.size(); j++){
//                    Log.e(TAG, "mCommentMap--list"+position+"j-->"+j+"--content-->"+list.get(j).getContent());
//                }

                int n = Math.min(3, list.size());
//                Log.e(TAG, "getView--" + position +"--min-->"+n);


                holder.commentRootLayout.removeAllViews();
                for(int i=0; i<n; i++){
                    Log.e(TAG, "getView--in for loop add view--i-->"+i+"--start--position-->"+position);
                    View v = inflater.inflate(R.layout.listheader_item_comment, holder.commentRootLayout, false);
                    TextView name = (TextView) v.findViewById(R.id.listHeader_item_comment_usrName);
                    TextView content = (TextView) v.findViewById(R.id.listHeader_item_comment_content);

                    final String usrName = list.get(i).getUserName();
                    final String usrId = list.get(i).getUsrId();
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "comment item --name-->" + usrName + "  is clicked");
                            Intent intent = new Intent(getActivity(), PersonActivity.class);
                            intent.putExtra(PersonActivity.USR_ID, usrId);
                            startActivity(intent);
                        }
                    });
                    name.setText(usrName);
                    content.setText(list.get(i).getContent());
                    holder.commentRootLayout.addView(v);
                    Log.e(TAG, "getView--in for loop add view--i-->" + i + "--end--position-->" + position+"--name-->"+list.get(i).getUserName()+"--content-->"+list.get(i).getContent());

                }

                holder.commentRootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "commentRootLayout on click");

                        Intent intent = new Intent(getActivity(), CommentsActivity.class);
                        intent.putExtra(CommentsActivity.COMMENTS_LIST, (Serializable) list);
//                        ArrayList<CommentItem> ls = mCommentMap.get(item.getId());
                        startActivity(intent);

                    }
                });



            }


            return view;
        }

        @Override
        public View getHeaderView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolderHeader holderHeader;
            final Item item = getItem(i);
            if(view == null){
                holderHeader = new ViewHolderHeader();
                view = inflater.inflate(R.layout.listheader_header, viewGroup, false);
                holderHeader.imageView = (ImageView) view.findViewById(R.id.listHeader_usrImage);
                holderHeader.textView = (TextView) view.findViewById(R.id.listHeader_usrName);
                view.setTag(holderHeader);
            }else {
                holderHeader = (ViewHolderHeader) view.getTag();
            }

            Log.e(TAG, "getHeaderView--->"+i);

            Picasso.with(holderHeader.imageView.getContext()).load(item.getUserPhotoUrl())
                    .into(holderHeader.imageView);
                   holderHeader.textView.setText(item.getUserName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "header -->" + item.getUserName() + "  is clicked");
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra(PersonActivity.USR_ID, item.getUsrId());
                    startActivity(intent);
                }
            });


            return view;
        }


        @Override
        public long getHeaderId(int i) {
            return i;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Item getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        class ViewHolder{
            ImageView image;
            ImageView ivResc;
            TextView tvRecsCount;
            ImageView ivLiked;
            TextView tvLikedCount;
            TextView tvDesc;
            TextView tvCommentCount;
            LinearLayout commentRootLayout;

        }
        class ViewHolderHeader{
            ImageView imageView;
            TextView textView;
        }

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}


