package com.ethfoo.dbphoto.data;

import android.support.v4.app.Fragment;

import com.ethfoo.dbphoto.ui.fragments.FragmentAbout;
import com.ethfoo.dbphoto.ui.fragments.FragmentOne;
import com.ethfoo.dbphoto.ui.fragments.FragmentThree;
import com.ethfoo.dbphoto.ui.fragments.FragmentTwo;


public enum Fragments {

//    ONE(FragmentOne.class), TWO(FragmentTwo.class), THREE(FragmentThree.class), ABOUT(
//            FragmentAbout.class);
    Popular(FragmentOne.class), Liked(FragmentTwo.class), Recommend(FragmentThree.class), ABOUT(
                FragmentAbout.class);

    final Class<? extends Fragment> fragment;

    private Fragments(Class<? extends Fragment> fragment) {
        this.fragment = fragment;
    }

    public String getFragment() {
        return fragment.getName();
    }
}
