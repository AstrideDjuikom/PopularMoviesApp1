package com.example.astri.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailsFragment extends Fragment {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "arg_movie";
    private Movie movie;

    //empty constructor

    public DetailsFragment() {
    }

    //nouvelle instance du fragment details

    public static DetailsFragment newInstance(Movie selectedMovie) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, selectedMovie);
        detailsFragment.setArguments(args);
        return detailsFragment;

    }

    public void onCreate(Bundle savedInstancedState) {
        super.onCreate(savedInstancedState);

        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        if (movie
                != null) {

            ImageView posterView = view.findViewById(R.id.poster);
            Glide.with(getActivity()).load(movie
                    .getPosterUri()).into(posterView);

            TextView titleView = view.findViewById(R.id.title_content);
            titleView.setText(movie
                    .getTitle());

            TextView releaseDateView = view.findViewById(R.id.release_date_content);
            releaseDateView.setText(movie
                    .getReleaseDate());

            TextView averageView = view.findViewById(R.id.vote_average_content);
            averageView.setText(movie
                    .getVoteAverage());

            TextView overviewView = view.findViewById(R.id.overview_content);

            // default text: @string/overview_not_available
            if (!TextUtils.isEmpty(movie
                    .getOverview())) {
                overviewView.setText(movie
                        .getOverview());
            }
        }

        return view;
    }


}
