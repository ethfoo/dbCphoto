package com.ethfoo.dbphoto.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.CommentItem;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.ItemProvider;
import com.ethfoo.dbphoto.ui.PersonActivity;
import com.ethfoo.dbphoto.ui.misc.HorizontalListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    private static final String INDEX_PARAM = "indexOfList";
    private static final String ITEM_PARAM = "itemFromList";
    private static final String TAG = "DetailFragment";

//    // View name of the header image. Used for activity scene transitions
//    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
//
//    // View name of the header title. Used for activity scene transitions
//    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private LayoutInflater mInflater;
    private int mIndex;
    private Item mItem;
    private List<CommentItem> mCommentItemList;
    private ArrayList<Item> mRecommendList;

    private ImageView imageViewHeader;
    private TextView tvDesc;
    private ImageView ivUser;
    private TextView tvUser;
    private ImageView ivLike;
    private TextView tvLikeCnt;
    private LinearLayout commentRootLayout;
    private View commentBar;
    private TextView tvCommentsTotal;
    private ImageView ivAlbum;
    private TextView tvAlbumName;
    private TextView tvAlbumTime;

    private int hadLoad;
//    private OnFragmentInteractionListener mListener;

    public static DetailFragment newInstance(int index, Item item) {

        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX_PARAM, index);
        args.putSerializable(ITEM_PARAM, item);
//        args.putString(ARG_PARAM2, headerText);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(INDEX_PARAM);
            mItem = (Item) getArguments().get(ITEM_PARAM);
        }

        mCommentItemList = new ArrayList<>();




    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView"+mIndex);
        mInflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageViewHeader = (ImageView) rootView.findViewById(R.id.imageview_header);
        tvDesc = (TextView)rootView.findViewById(R.id.textview_desc);
        ivUser = (ImageView) rootView.findViewById(R.id.detail_imageView_user);
        tvUser = (TextView) rootView.findViewById(R.id.detail_textView_user);
        ivLike = (ImageView) rootView.findViewById(R.id.detail_imageView_isliked);
        tvLikeCnt = (TextView) rootView.findViewById(R.id.detail_textView_liked_count);
        tvCommentsTotal = (TextView) rootView.findViewById(R.id.fragment_detail_comments_total);
        ivAlbum = (ImageView) rootView.findViewById(R.id.fragment_detail_album);
        tvAlbumName = (TextView) rootView.findViewById(R.id.fragment_detail_album_name);
        tvAlbumTime = (TextView) rootView.findViewById(R.id.fragment_detail_album_create_time);

        ivLike.setImageResource(android.R.drawable.btn_star_big_off);

        commentRootLayout = (LinearLayout) rootView.findViewById(R.id.comment_rootView);
        commentBar = mInflater.inflate(R.layout.comment_bar, null);

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra(PersonActivity.USR_ID, mItem.getUsrId());
                Log.e(TAG, "mItem.getUsrId()-->" + mItem.getUsrId());
                startActivity(intent);
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


        /**download album json, and get every image url to array*/
//        String albumName = mItem.getAlbumName();
//        String albumJsonUrl = "http://10.5.104.86:8080/myweb/json/"+albumName+".json";

//        mAlbumItemList = new ArrayList<>();
//
//        Ion.with(getActivity())
//                .load(albumJsonUrl)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        if (e != null || result == null) {
//                            Log.e(TAG, "ERROR in parsing json");
//                            return;
//                        }
//
//                        for (int i = 0; i < result.getAsJsonArray("photos").size(); i++) {
//                            mAlbumItemList.add(result.getAsJsonArray("photos").get(i).getAsJsonObject().get("image").getAsString());
//                        }
//
//                    }
//                });

//        AlbumItemAdapter adapter = new AlbumItemAdapter(getActivity(), mAlbumItemList);
//        horizonListView.setAdapter(adapter);
//        CommentAdapter commentAdapter = new CommentAdapter(getActivity(), mCommentItemList);
//        commentList.setAdapter(commentAdapter);

        final Item item = mItem;

        if (item.getDesc() == null){

            Ion.with(getActivity())
                    .load(item.getJsonUrl())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if(e != null || result== null){
                                Log.e(TAG, "ERROR when parsing json");
                                return;
                            }

                            Log.e(TAG, "*Item"+mIndex+"*result**>>>" + result.toString());

                            if( result.get("image") != null  ){
//                                    Log.e(TAG, "getItem set ...");

                                item.setId(result.get("id").getAsString());

                                item.setImageUrl(result.get("image").getAsString());
                                item.setDesc(result.get("desc").getAsString());
                                item.setUserPhotoUrl(result.getAsJsonObject("author").get("avatar").getAsString());
                                item.setUserName(result.getAsJsonObject("author").get("name").getAsString());
                                item.setLikedCount(result.get("liked_count").getAsLong());
                                item.setAlbumId(result.get("album_id").getAsString());



                                Picasso.with(getActivity())
                                        .load(mItem.getImageUrl())
                                        .noFade()
                                        .noPlaceholder()
                                        .into(imageViewHeader);
                                tvDesc.setText(mItem.getDesc());
                                Picasso.with(getActivity())
                                        .load(mItem.getUserPhotoUrl())
                                        .noFade()
                                        .noPlaceholder()
                                        .into(ivUser);
                                tvUser.setText(mItem.getUserName());
                                tvLikeCnt.setText(mItem.getLikedCount() + "");

                            } else{
                                Log.e(TAG, result.toString());

                            }

                        }
                    });
        }else {
            Picasso.with(getActivity())
                    .load(mItem.getImageUrl())
                    .noFade()
                    .noPlaceholder()
                    .into(imageViewHeader);
            tvDesc.setText(mItem.getDesc());
            Picasso.with(getActivity())
                    .load(mItem.getUserPhotoUrl())
                    .noFade()
                    .noPlaceholder()
                    .into(ivUser);
            tvUser.setText(mItem.getUserName());
            tvLikeCnt.setText(mItem.getLikedCount() + "");
        }

        Ion.with(this).load("https://api.douban.com/v2/album/"+item.getAlbumId()).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {

                if(e != null || result== null){
                    Log.e(TAG, "ERROR when parsing json");
                    return;
                }

                if( result.get("image") != null  ){

                    String album = result.get("icon").getAsString();
                    String albumName = result.get("title").getAsString();
                    String albumTime = result.get("created").getAsString();

                    Picasso.with(ivAlbum.getContext()).load(album).into(ivAlbum);
                    tvAlbumName.setText(albumName);
                    tvAlbumTime.setText(albumTime);
                } else{
                    Log.e(TAG, result.toString());

                }
            }
        });


//        hadLoad = 0;
        loadComments(0, 8);



//        ViewCompat.setTransitionName(imageViewHeader, DetailActivity.VIEW_NAME_HEADER_IMAGE);
//        ViewCompat.setTransitionName(tvDesc, DetailActivity.VIEW_NAME_HEADER_TITLE);


        return rootView;
    }

    void loadComments(int s, int c){
        Log.e(TAG, "loadComments start..."+mIndex+"hadLoad-->"+hadLoad);
        Ion.with(getActivity())
                .load(mItem.getCommentsJsonUrl()+"?start="+s+"&count="+c)
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

                            tvCommentsTotal.setText("("+total+")");

                            if(total == 0){
                                commentRootLayout.addView(commentBar);
                                return;
                            }



                            JsonArray jsonArray = result.get("comments").getAsJsonArray();
                            for (int i=0; i<jsonArray.size() ;i++){
                                JsonObject object = jsonArray.get(i).getAsJsonObject();
                                JsonObject usr = object.get("author").getAsJsonObject();
                                String content = object.get("content").getAsString();
                                String usrImageUrl = usr.get("avatar").getAsString();
                                String usrName = usr.get("name").getAsString();
                                mCommentItemList.add(new CommentItem(usrImageUrl, usrName, content));

                                View itemView = mInflater.inflate(R.layout.comment_item, null);
                                ImageView image = (ImageView) itemView.findViewById(R.id.comment_usr_image);
                                Picasso.with(image.getContext())
                                        .load(usrImageUrl)
                                        .resize(50, 50)
                                        .into(image);
                                TextView tvName = (TextView) itemView.findViewById(R.id.comment_user_tv);
                                tvName.setText(usrName);
                                TextView tvContent = (TextView) itemView.findViewById(R.id.comment_content);
                                tvContent.setText(content);
                                commentRootLayout.addView(itemView);
                            }

                            hadLoad+=8;
//                            Log.e(TAG, "on result-->"+"hadLoad-->"+hadLoad+"total-->"+total);

                            if(total-hadLoad>0){
                                Log.e(TAG, "total-hadLoad--->"+"total-->"+total+"--hadLoad-->"+hadLoad);
                                commentRootLayout.addView(commentBar);
                                TextView tv = (TextView) commentBar.findViewById(R.id.comment_bar_text);
                                tv.setText("load more...");
                                commentBar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        commentRootLayout.removeView(commentBar);
                                        loadComments(hadLoad, 8);
//                                        Log.e(TAG, "hadLoad--->"+hadLoad);
                                    }
                                });

                            }else {
                                commentRootLayout.addView(commentBar);
                                TextView tv = (TextView) commentBar.findViewById(R.id.comment_bar_text);
                                tv.setText("no more comments..");
                                commentBar.setEnabled(false);
                            }


                        } else {
                            Log.e(TAG, "did not return correct Json"+"---"+result.toString());
                        }
                    }
                }) ;


    }




    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
