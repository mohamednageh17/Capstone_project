package com.example.berlin.tvseriesapp.UI.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berlin.tvseriesapp.Adapters.SeriesAdapter;
import com.example.berlin.tvseriesapp.Data.SeriesContract;
import com.example.berlin.tvseriesapp.Models.SeriesModel;
/*import com.example.berlin.tvseriesapp.Models.review_Model;*/
import com.example.berlin.tvseriesapp.Models.TrailerModel;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Activities.MainActivity;
import com.example.berlin.tvseriesapp.Utils.GetNetworkData;
import com.example.berlin.tvseriesapp.Utils.NetworkOperations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailedFragment extends android.support.v4.app.Fragment {

    final public String img_String = "http://image.tmdb.org/t/p/w185/";
    static boolean Favourite_Selected = false;
    ImageView Poster_Img;
    TextView Title;
    TextView Overview;
    TextView Release_Date;
    TextView Vote_Rating;
    TextView Trailer_Name;
    ImageView Favourite;
    ImageView button;
    detailedFragment_processes d_Frag_processes;
    Bundle series_Info;
    SeriesModel seriesModel;
    View view;
    FloatingActionButton actionButton;

    private ArrayList<SeriesModel> series_modelArrayList;
    SeriesAdapter seriesAdapter;
    RecyclerView seriesRecyclerView;
    private ArrayList<SeriesModel> series_modelArrayList1;
    SeriesAdapter seriesAdapter1;
    RecyclerView seriesRecyclerView1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detailed_fragment, container, false);
        view = v;
        series_Info = new Bundle();
        seriesModel = new SeriesModel();
        d_Frag_processes = new detailedFragment_processes();
        Poster_Img = (ImageView) v.findViewById(R.id.Poster_Image);
        Title = (TextView) v.findViewById(R.id.Title);
        Release_Date = (TextView) v.findViewById(R.id.Release_Date);
        Overview = (TextView) v.findViewById(R.id.Overview);
        Vote_Rating = (TextView) v.findViewById(R.id.Vote_Rating);
        Trailer_Name = (TextView) v.findViewById(R.id.TrailerName);
        Favourite = (ImageView) v.findViewById(R.id.Favourite);
        button = (ImageView) v.findViewById(R.id.button);
        series_Info = this.getArguments();

        actionButton = (FloatingActionButton) v.findViewById(R.id.share_fab);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "";
                if (seriesModel != null) {
                    shareBody = seriesModel.getTitle();
                }

                if (shareBody.equals(""))
                    shareBody = "Here is the share content body";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        if (savedInstanceState != null) {
            seriesModel = savedInstanceState.getParcelable("seriesModel");
            d_Frag_processes.setTrailerDetails();
        } else {
            seriesModel = series_Info.getParcelable("seriesModel");
            if (!Favourite_Selected) {
                d_Frag_processes.FetchTrailer();
            } else {
                d_Frag_processes.setTrailerDetails();
            }
        }
        d_Frag_processes.setMovieDetails();
        seriesModel.Favourite = isFavorite();
        if (seriesModel.Favourite)
            Favourite.setImageResource(R.drawable.staron);
        else
            Favourite.setImageResource(R.drawable.staroff);
        Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!seriesModel.Favourite) {
                    seriesModel.Favourite = true;
                    markAsFavorite();
                    Toast.makeText(getActivity(), R.string.SaveAsFavourite, Toast.LENGTH_SHORT).show();
                } else {
                    seriesModel.Favourite = false;
                    removeFromFavorites();
                    Toast.makeText(getActivity(), R.string.RemoveFromFavourite, Toast.LENGTH_SHORT).show();

                }
            }
        });

        getRecommindations(v);
        getSimilarTV(v);

        return v;
    }

    private void getSimilarTV(View v) {

        seriesRecyclerView = (RecyclerView) v.findViewById(R.id.SimilarRecyclerView);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        GetNetworkData getNetwork_data = new GetNetworkData("s", seriesModel.getId());
        getNetwork_data.execute();
        getNetwork_data.setNetworkOperations(new NetworkOperations() {
            @Override
            public void OnDataReached(String JsonData) {
                series_modelArrayList = SeriesModel.ParsingTrailerData(JsonData);
                seriesAdapter = new SeriesAdapter(series_modelArrayList, getContext());
                seriesRecyclerView.setAdapter(seriesAdapter);
                if (JsonData == null)
                    Toast.makeText(MainActivity.ctx, R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getRecommindations(View v) {

        seriesRecyclerView1 = (RecyclerView) v.findViewById(R.id.RecommendationsRecyclerView);
        seriesRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        GetNetworkData getNetwork_data = new GetNetworkData("r", seriesModel.getId());
        getNetwork_data.execute();
        getNetwork_data.setNetworkOperations(new NetworkOperations() {
            @Override
            public void OnDataReached(String JsonData) {
                series_modelArrayList1 = SeriesModel.ParsingTrailerData(JsonData);
                seriesAdapter1 = new SeriesAdapter(series_modelArrayList1, getContext());
                seriesRecyclerView1.setAdapter(seriesAdapter1);
                if (JsonData == null)
                    Toast.makeText(MainActivity.ctx, R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("seriesModel", seriesModel);
        super.onSaveInstanceState(outState);
    }

    public class detailedFragment_processes {

        public detailedFragment_processes() {
        }

        public void FetchTrailer() {
            if (MainActivity.NetworkState()) {
                GetNetworkData fetchData = new GetNetworkData("TrailerModel", seriesModel.getId());
                fetchData.execute();
                fetchData.setNetworkOperations(new NetworkOperations() {
                    @Override
                    public void OnDataReached(String JsonData) {
                        TrailerModel trailer = new TrailerModel();
                        trailer = TrailerModel.ParsingTrailerData(JsonData);
                        seriesModel.setTrailer(trailer);
                        setTrailerDetails();
                    }


                });
            } else {
                TrailerModel trailer = new TrailerModel();
                seriesModel.setTrailer(trailer);
                setTrailerDetails();
                Toast.makeText(getActivity(),R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();
            }
        }

        public void setMovieDetails() {
            Picasso.with(view.getContext()).load(img_String + seriesModel.getPoster_ImageUrl())
                    .placeholder(R.drawable.loadingicon).error(R.drawable.error).into(Poster_Img);
            Title.setText(seriesModel.getTitle());
            Overview.setText(seriesModel.getOverview());
            Release_Date.setText(seriesModel.getRelease_Date());
            Vote_Rating.setText(seriesModel.getVote_average() + "/10");
        }

        public void setTrailerDetails() {
            Trailer_Name.setText(seriesModel.getTrailerName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + seriesModel.getTrailerKey())));
                }
            });
        }
    }

    public static void IsFavouriteSelected(boolean isSelected) {
        Favourite_Selected = isSelected;
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_ID,
                            seriesModel.getId());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_TITLE,
                            seriesModel.getTitle());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_POSTER_PATH,
                            seriesModel.getPoster_ImageUrl());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_OVERVIEW,
                            seriesModel.getOverview());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE,
                            seriesModel.getVote_average());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_RELEASE_DATE,
                            seriesModel.getRelease_Date());

                    getActivity().getContentResolver().insert(
                            SeriesContract.SeriesEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Favourite.setImageResource(R.drawable.staron);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getActivity().getContentResolver().delete(SeriesContract.SeriesEntry.CONTENT_URI,
                            SeriesContract.SeriesEntry.COLUMN_SERIES_ID + " = " + seriesModel.getId(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Favourite.setImageResource(R.drawable.staroff);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean isFavorite() {
        Cursor movieCursor = getActivity().getContentResolver().query(
                SeriesContract.SeriesEntry.CONTENT_URI,
                new String[]{SeriesContract.SeriesEntry.COLUMN_SERIES_ID},
                SeriesContract.SeriesEntry.COLUMN_SERIES_ID + " = " + seriesModel.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

}
