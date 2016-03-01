package com.ethfoo.dbphoto.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.Constants;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.MyDBHelper;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.ButterKnife;


public class FragmentTwo extends Fragment {
    private static final String TAG = "FragmentTwo";


//    @InjectView(R.id.circleLayout)
    LinearLayout circleLayout;
    private GridView gridView;
    private TextView tvHint;

    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private List<String> photoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new MyDBHelper(getActivity(), Constants.DB_NAME, null, 1 );
        db = dbHelper.getReadableDatabase();

        photoList = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containter,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, containter, false);
        ButterKnife.inject(this, view);
//        ((GradientDrawable) circleLayout.getBackground())
//                .setColor(getResources().getColor(R.color.material_pink));


        GridViewAdapter adapter = new GridViewAdapter(getActivity(), photoList);
        gridView = (GridView) view.findViewById(R.id.fragment2_gridView);
        gridView.setAdapter(adapter);

        tvHint = (TextView) view.findViewById(R.id.fragment2_hint);

        Cursor cursor = db.query(Constants.DB_NAME, new String[]{Constants.DB_PHOTO_URL},
                null, null, null, null, Constants.DB_CREATE_TIME+" DESC");

        if (cursor != null){
            Log.e(TAG, "cursor != null");
            if(cursor.moveToFirst()){
                do{
                    String photoUrl = cursor.getString(cursor.getColumnIndex(Constants.DB_PHOTO_URL));
                    photoList.add(photoUrl);
                }while (cursor.moveToNext());
            }else {
                tvHint.setVisibility(View.VISIBLE);
                Log.e(TAG, "NO STAR PHOTO");
            }


        }
        cursor.close();

        for(String url : photoList){
            Log.e(TAG, "onCreate--photoList-->"+url);
        }

        return view;
    }

    class GridViewAdapter extends ArrayAdapter<String>{
        LayoutInflater inflater;

        public GridViewAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.fragment_two_grid, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.fragment2_grid_imageView);
            Picasso.with(getContext()).load(getItem(position)).into(imageView);

            return convertView;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }



}
