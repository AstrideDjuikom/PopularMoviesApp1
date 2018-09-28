package com.example.astri.popularmovies.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.listener.OnNoInternetFragmentListener;

public class NoInternetFragment extends Fragment {

    private static final String LOG_TAG = NoInternetFragment.class.getSimpleName();

    private OnNoInternetFragmentListener mOnNoInternetFragmentListener;

    public NoInternetFragment() {
        // Required empty public constructor
    }

    public static NoInternetFragment newInstance() {
        NoInternetFragment fragment = new NoInternetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_internet,container, false);
        Button retryButton = view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed();
            }
        });
        return view;
    }

    // Calls listener implemented by host Activity
    public void onRetryButtonPressed() {
        if (mOnNoInternetFragmentListener != null) {
            mOnNoInternetFragmentListener.onRetry();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoInternetFragmentListener) {
            mOnNoInternetFragmentListener = (OnNoInternetFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoInternetFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNoInternetFragmentListener = null;
    }

    /*public interface OnRetryInteractionListener {
        void onRetryInteraction();
    }*/
}
