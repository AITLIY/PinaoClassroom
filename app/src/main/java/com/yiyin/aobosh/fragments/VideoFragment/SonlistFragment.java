package com.yiyin.aobosh.fragments.VideoFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yiyin.aobosh.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SonlistFragment extends Fragment {


    public SonlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sonlist, container, false);
    }

}
