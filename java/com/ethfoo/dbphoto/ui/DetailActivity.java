/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ethfoo.dbphoto.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.DetailFragmentAdapter;
import com.ethfoo.dbphoto.data.Item;
import com.ethfoo.dbphoto.data.ItemProvider;


import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Our secondary Activity which is launched from {@link MainActivity}. Has a simple detail UI
 * which has a large banner image, title and body text.
 */
public class DetailActivity extends FragmentActivity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail_id";
    public static final String EXTRA_RECOMMEND_LIST = "recommend_list";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;

    private Item mItem;

    ViewPager viewPager;
    DetailFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        viewPager= (ViewPager) findViewById(R.id.pager);

        int index = getIntent().getIntExtra(EXTRA_PARAM_ID, 0);
        // Retrieve the correct Item instance, using the ID provided in the Intent

        ArrayList<Item> recommendList = (ArrayList<Item>) getIntent().getSerializableExtra(EXTRA_RECOMMEND_LIST);

        adapter = new DetailFragmentAdapter(this, getSupportFragmentManager(), recommendList);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(index);



//        ImageView icon = new ImageView(this);
//        icon.setImageResource(R.drawable.isliked);
//        FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();
//        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
//        ImageView starIcon = new ImageView(this);
//        starIcon.setImageResource(R.drawable.star_full);
//        ImageView shareIcon = new ImageView(this);
//        shareIcon.setImageResource(R.drawable.isliked);
//        ImageView commentIcon = new ImageView(this);
//        commentIcon.setImageResource(R.drawable.star_full);
//
//        SubActionButton btnStar = itemBuilder.setContentView(starIcon).build();
//        btnStar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(DetailActivity.this, "btnStar on clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//        SubActionButton btnShare = itemBuilder.setContentView(shareIcon).build();
//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DetailActivity.this, "on btnShare clicked", LENGTH_SHORT).show();
//            }
//        });
//        SubActionButton btnComment = itemBuilder.setContentView(commentIcon).build();
//        btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DetailActivity.this, "on btnComment clicked", LENGTH_SHORT).show();
//            }
//        });
//        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
//                .addSubActionView(btnStar)
//                .addSubActionView(btnShare)
//                .addSubActionView(btnComment)
//                .attachTo(actionButton)
//                .build();


//        mHeaderImageView = (ImageView) findViewById(R.id.imageview_header);
//        mHeaderTitle = (TextView) findViewById(R.id.textview_title);

        // BEGIN_INCLUDE(detail_set_view_name)
        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */

//        mHeaderTitle.setText(getString(R.string.image_header, mItem.getName(), mItem.getAuthor()));
//        loadFullSizeImage();
//        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
//        ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE);
        // END_INCLUDE(detail_set_view_name)

//
    }




    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
//    private boolean addTransitionListener() {
//        final Transition transition = getWindow().getSharedElementEnterTransition();
//
//        if (transition != null) {
//            // There is an entering shared element transition so add a listener to it
//            transition.addListener(new Transition.TransitionListener() {
//                @Override
//                public void onTransitionEnd(Transition transition) {
//                    // As the transition has ended, we can now load the full-size image
//
//                    // Make sure we remove ourselves as a listener
//                    transition.removeListener(this);
//                }
//
//                @Override
//                public void onTransitionStart(Transition transition) {
//                    // No-op
//                }
//
//                @Override
//                public void onTransitionCancel(Transition transition) {
//                    // Make sure we remove ourselves as a listener
//                    transition.removeListener(this);
//                }
//
//                @Override
//                public void onTransitionPause(Transition transition) {
//                    // No-op
//                }
//
//                @Override
//                public void onTransitionResume(Transition transition) {
//                    // No-op
//                }
//            });
//            return true;
//        }
//
//        // If we reach here then we have not added a listener
//        return false;
//    }



}
