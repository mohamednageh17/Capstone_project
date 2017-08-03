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

import com.example.berlin.tvseriesapp.Adapters.Series_Adapter;
import com.example.berlin.tvseriesapp.Data.SeriesContract;
import com.example.berlin.tvseriesapp.Models.Series_Model;
import com.example.berlin.tvseriesapp.Models.review_Model;
import com.example.berlin.tvseriesapp.Models.trailer_Model;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Activities.MainActivity;
import com.example.berlin.tvseriesapp.Utils.GetNetworkData;
import com.example.berlin.tvseriesapp.Utils.NetworkOperations;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class detailed_Fragment extends android.support.v4.app.Fragment {

    final public  String img_String= "http://image.tmdb.org/t/p/w185/";
    static boolean Favourite_Selected=false;
    ImageView Poster_Img;
    TextView Title;
    TextView Overview;
    TextView Release_Date;
    TextView Vote_Rating;
    TextView Review_Author;
    TextView Review_Content;
    TextView Trailer_Name;
    ImageView Favourite;
    ImageView button;
    detailedFragment_processes d_Frag_processes;
    Bundle series_Info ;
    Series_Model series ;
    View view;
    FloatingActionButton actionButton;


    private ArrayList<Series_Model> Movies;
    Series_Adapter movieAdapter;
     RecyclerView MoviesRecyclerView;
    private ArrayList<Series_Model> Movies1;
    Series_Adapter movieAdapter1;
    RecyclerView MoviesRecyclerView1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.detailed_fragment,container,false);
        view=v;
        series_Info=new Bundle();
        series=new Series_Model();
        d_Frag_processes=new detailedFragment_processes();
        Poster_Img=(ImageView)v.findViewById(R.id.Poster_Image);
        Title=(TextView)v.findViewById(R.id.Title);
        Release_Date=(TextView)v.findViewById(R.id.Release_Date);
        Overview=(TextView)v.findViewById(R.id.Overview);
        Vote_Rating=(TextView)v.findViewById(R.id.Vote_Rating);
        Trailer_Name=(TextView)v.findViewById(R.id.TrailerName);
        Favourite=(ImageView)v.findViewById(R.id.Favourite);
        button=(ImageView)v.findViewById(R.id.button);
        series_Info=this.getArguments();

        actionButton=(FloatingActionButton)v.findViewById(R.id.share_fab);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody="";
                if(series!=null){
                    shareBody=series.getTitle();
                }

                if(shareBody.equals(""))
                    shareBody = "Here is the share content body";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        if(savedInstanceState!=null){
            series= savedInstanceState.getParcelable("movie_Model");
           /* d_Frag_processes.setReviewDetails();*/
            d_Frag_processes.setTrailerDetails();
        }
        else{
            series=series_Info.getParcelable("movie_Model");
            if(!Favourite_Selected) {
               /* d_Frag_processes.FetchReview();*/
                d_Frag_processes.FetchTrailer();
            }
            else {
               /* d_Frag_processes.setReviewDetails();*/
                d_Frag_processes.setTrailerDetails();
            }
        }
        d_Frag_processes.setMovieDetails();
        series.Favourite=isFavorite();
        if(series.Favourite)
            Favourite.setImageResource(R.drawable.staron);
        else
            Favourite.setImageResource(R.drawable.staroff);
        Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!series.Favourite){
                    series.Favourite=true;
                    markAsFavorite();
                    Toast.makeText(getActivity(),"Save in Favourite Movies", Toast.LENGTH_SHORT).show();
                }
                else {
                    series.Favourite=false;
                    removeFromFavorites();
                    Toast.makeText(getActivity(),"Remove From Favourite Movies", Toast.LENGTH_SHORT).show();

                }
            }
        });

        getRecommindations(v);
        getSimilarTV(v);
        return v;
    }

    private void getSimilarTV(View v) {

        MoviesRecyclerView=(RecyclerView)v.findViewById(R.id.SimilarRecyclerView);
        MoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        GetNetworkData getNetwork_data =new GetNetworkData("s",series.getId());
        getNetwork_data.execute();
        getNetwork_data.setNetworkResponse(new NetworkOperations() {
            @Override
            public void OnSuccess(String JsonData) {
                Movies = Series_Model.ParsingTrailerData(JsonData);
                movieAdapter = new Series_Adapter(Movies, getContext());
                MoviesRecyclerView.setAdapter(movieAdapter);
                if(JsonData==null)
                    Toast.makeText(MainActivity.ctx," No Internet Connection", Toast.LENGTH_SHORT).show();
                /*CheckTablet();
                ClickEvent();*/
            }
        });

    }

    private void getRecommindations(View v) {

        MoviesRecyclerView1=(RecyclerView)v.findViewById(R.id.RecommendationsRecyclerView);
        MoviesRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        GetNetworkData getNetwork_data =new GetNetworkData("r",series.getId());
        getNetwork_data.execute();
        getNetwork_data.setNetworkResponse(new NetworkOperations() {
            @Override
            public void OnSuccess(String JsonData) {
                Movies1 = Series_Model.ParsingTrailerData(JsonData);
                movieAdapter1 = new Series_Adapter(Movies1, getContext());
                MoviesRecyclerView1.setAdapter(movieAdapter1);
                if(JsonData==null)
                    Toast.makeText(MainActivity.ctx," No Internet Connection", Toast.LENGTH_SHORT).show();
                /*CheckTablet();
                ClickEvent();*/
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie_Model",series);
        super.onSaveInstanceState(outState);
    }

    public class detailedFragment_processes{

        public detailedFragment_processes(){
        }

        public void FetchReview() {
            if(MainActivity.NetworkState()){
                GetNetworkData fetchData = new GetNetworkData("review_Model",series.getId());
                fetchData.execute();
                fetchData.setNetworkResponse(new NetworkOperations()  {
                    @Override
                    public void OnSuccess(String JsonData) {
                        review_Model review = new review_Model();
                        review =  review_Model.ParsingReviewData(JsonData);
                        series.setReview(review);
                        setReviewDetails();
                    }


                });
            }
            else {
                review_Model review = new review_Model();
                series.setReview(review);
                setReviewDetails();
                Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        public void FetchTrailer() {
            if(MainActivity.NetworkState()) {
                GetNetworkData fetchData = new GetNetworkData("trailer_Model", series.getId());
                fetchData.execute();
                fetchData.setNetworkResponse(new NetworkOperations()  {
                    @Override
                    public void OnSuccess(String JsonData) {
                        trailer_Model trailer = new trailer_Model();
                        trailer = trailer_Model.ParsingTrailerData(JsonData);
                        series.setTrailer(trailer);
                        setTrailerDetails();
                    }


                });
            }
            else{
                trailer_Model trailer = new trailer_Model();
                series.setTrailer(trailer);
                setTrailerDetails();
                Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        public void setMovieDetails(){
            Picasso.with(view.getContext()).load(img_String+ series.getPoster_ImageUrl())
                    .placeholder(R.drawable.loadingicon).error(R.drawable.error).into(Poster_Img);
            Title.setText( series.getTitle());
            Overview.setText(series.getOverview());
            Release_Date.setText(series.getRelease_Date());
            Vote_Rating.setText(series.getVote_average()+"/10");
        }
        public void setTrailerDetails(){
            Trailer_Name.setText(series.getTrailerName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+ series.getTrailerKey())));
                }
            });
        }
        public void setReviewDetails(){
            Review_Author.setText(series.getReviewAuthor());
            Review_Content.setText(series.getReviewContent());
        }
    }

    public static void IsFavouriteSelected(boolean isSelected) {
        Favourite_Selected=isSelected;
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_ID,
                            series.getId());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_TITLE,
                            series.getTitle());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_POSTER_PATH,
                            series.getPoster_ImageUrl());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_OVERVIEW,
                            series.getOverview());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE,
                            series.getVote_average());
                    movieValues.put(SeriesContract.SeriesEntry.COLUMN_SERIES_RELEASE_DATE,
                            series.getRelease_Date());

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
                            SeriesContract.SeriesEntry.COLUMN_SERIES_ID + " = " + series.getId(), null);
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
                SeriesContract.SeriesEntry.COLUMN_SERIES_ID + " = " + series.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }


   /* public void CheckTablet(){
        movie_Model movie =  new movie_Model();
        if (IsTablet ) {
            if (Movies.size() != 0)
                movie = Movies.get(0);
            MoviesInfo.putParcelable("movie_Model", movie);
            if (!InstanceState && !checkFrag ) {
                detailed_Fragment detailedFragment1 = new detailed_Fragment();
                detailedFragment1.setArguments(MoviesInfo);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();
                checkFrag = true;
            }
        }
    }

    public void ClickEvent(){
        movieAdapter.setClickListener(new Series_Adapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                movie_Model movie=new movie_Model();
                movie=Movies.get(position);
                MoviesInfo.putParcelable("movie_Model",movie);
                if (!IsTablet) {
                    Intent in = new Intent( CurrentActivity , detailed_Activity.class);
                    in.putExtra("MoviesInfo", MoviesInfo);
                    startActivity(in);
                } else {
                    detailed_Fragment  detailedFragment1=new detailed_Fragment();
                    detailedFragment1.setArguments(MoviesInfo);
                    getFragmentManager().beginTransaction().replace(R.id.DetailedFragment,detailedFragment1).commit();

                }
            }
        });

    }
}
*/

}
