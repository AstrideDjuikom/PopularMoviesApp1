package com.example.astri.popularmovies.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.astri.popularmovies.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoadingFragment extends Fragment {
    public static final String FRAGMENT_TAG = LoadingFragment.class.getSimpleName();
    //private static final String LOG_TAG = LoadingFragment.class.getSimpleName();

    public LoadingFragment() {
        // Required empty public constructor
    }

    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        return view;
    }
}
