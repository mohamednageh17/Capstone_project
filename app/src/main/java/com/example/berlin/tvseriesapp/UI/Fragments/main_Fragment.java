package com.example.berlin.tvseriesapp.UI.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berlin.tvseriesapp.UI.Activities.ActivityLogin;
import com.example.berlin.tvseriesapp.Adapters.Series_Adapter;
import com.example.berlin.tvseriesapp.Data.SeriesContract;
import com.example.berlin.tvseriesapp.Models.Series_Model;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Activities.MainActivity;
import com.example.berlin.tvseriesapp.UI.Activities.SearchResultsActivity;
import com.example.berlin.tvseriesapp.UI.Activities.detailed_Activity;
import com.example.berlin.tvseriesapp.Utils.GetNetworkData;
import com.example.berlin.tvseriesapp.Utils.NetworkOperations;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;


public class main_Fragment extends android.support.v4.app.Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    boolean InstanceState;
    Activity CurrentActivity;
    ArrayList<Series_Model> series;
    static boolean checkFrag = false;
    Series_Adapter seriesAdapter;
    Bundle MoviesInfo;
    Context context;
    boolean IsTablet;
    RecyclerView seriesRecyclerView;
    View view;
    mainFragment_processes m_frag_processes;
    TextView type;
    int flag ;

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    public static final int LOADER_ID = 101;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
       getLoaderManager().initLoader(LOADER_ID,null,this);
        flag=0;
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),(GoogleApiClient.OnConnectionFailedListener)getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        view = inflater.inflate(R.layout.main_fragment, container, false);


/*        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);*/


        m_frag_processes = new mainFragment_processes();
        context = view.getContext();
        CurrentActivity = getActivity();
        seriesAdapter = new Series_Adapter();
        IsTablet = getResources().getBoolean(R.bool.isTablet);
        MoviesInfo = new Bundle();
        type = (TextView) view.findViewById(R.id.type);

        if(savedInstanceState!=null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
        }else {
            type.setText("Popular TV series:");
            m_frag_processes.collectData("Popular Movies");
        }
        seriesRecyclerView = (RecyclerView) view.findViewById(R.id.MoviesRecyclerView);
        if (IsTablet)
            seriesRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        else
            seriesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        return view;
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Movies",series);
        outState.putInt(MENU_SELECTED, selected);
    }

    @Override
    public void onResume() {
        super.onResume();
       // m_frag_processes.DisplayFavouriteMovies();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SeriesContract.SeriesEntry.CONTENT_URI, SeriesContract.SeriesEntry.MOVIE_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Series_Model>FavouriteList=new ArrayList<>();
        Series_Model movie_model;
     if(flag==1) {
         if (data != null && data.moveToFirst()) {
             do {
                 String id = data.getString(SeriesContract.SeriesEntry.COL_SERIES_ID);
                 String title = data.getString(SeriesContract.SeriesEntry.COL_SERIES_TITLE);
                 String posterPath = data.getString(SeriesContract.SeriesEntry.COL_SERIES_POSTER_PATH);
                 String overview = data.getString(SeriesContract.SeriesEntry.COL_SERIES_OVERVIEW);
                 String rating = data.getString(SeriesContract.SeriesEntry.COL_SERIES_VOTE_AVERAGE);
                 String releaseDate = data.getString(SeriesContract.SeriesEntry.COL_SERIES_RELEASE_DATE);
                 movie_model = new Series_Model(id, title, posterPath, overview, rating, releaseDate);
                 FavouriteList.add(movie_model);
             } while (data.moveToNext());
         }
         data.close();
         seriesAdapter = new Series_Adapter(FavouriteList, context);
         seriesRecyclerView.setAdapter(seriesAdapter);
         m_frag_processes.CheckTablet();
         m_frag_processes.ClickEvent();
         detailed_Fragment.IsFavouriteSelected(true);
     }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class mainFragment_processes{

    public mainFragment_processes(){}

    public void collectData(String Key){
        if(MainActivity.NetworkState()) {
            GetNetworkData fetchData = new GetNetworkData(Key, "");
            ClickEvent();
            fetchData.execute();
            fetchData.setNetworkResponse(new NetworkOperations()  {

                @Override
                public void OnSuccess(String JsonData) {
                    series = Series_Model.ParsingTrailerData(JsonData);
                    seriesAdapter = new Series_Adapter(series, context);
                    seriesRecyclerView.setAdapter(seriesAdapter);
                    if(JsonData==null)
                        Toast.makeText(MainActivity.ctx," No Internet Connection", Toast.LENGTH_SHORT).show();
                    CheckTablet();
                    ClickEvent();
                }
            });
        }
        else{
            series = new ArrayList<>();
            seriesAdapter = new Series_Adapter(series, context);
            seriesRecyclerView.setAdapter(seriesAdapter);
            Toast.makeText(getActivity()," No Internet Connection", Toast.LENGTH_SHORT).show();
            CheckTablet();
            ClickEvent();
        }
    }

    public void CheckTablet(){
        Series_Model movie =  new Series_Model();
        if (IsTablet ) {
            if (series.size() != 0)
                movie = series.get(0);
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
        seriesAdapter.setClickListener(new Series_Adapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                Series_Model movie=new Series_Model();
                movie=series.get(position);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuInflater inflater_ = getActivity().getMenuInflater();
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate menu to add items to action bar if it is present.
        //inflater.inflate(R.menu.menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
       /* searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));*/

        ComponentName cn = new ComponentName(getActivity(), SearchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (item.getItemId()==R.id.pop)
        {
            selected=id;
            type.setText("Popular TV series:");
            m_frag_processes.collectData("Popular Movies");

        }
        else if(item.getItemId()==R.id.top){
            selected=id;
            type.setText("Top Rated TV series:");
            m_frag_processes.collectData("Top Rated Movies");
        }
        else if(item.getItemId()==R.id.fav){
            selected=id;
            type.setText("Favourite TV series:");
            getLoaderManager().restartLoader(LOADER_ID, null,this);
            flag=1;

        }

        else if(item.getItemId()==R.id.airing_today){
            selected=id;
            type.setText("Airing Today TV series:");
            m_frag_processes.collectData("airing_today");
        }
        else if(item.getItemId()==R.id.on_the_air){
            selected=id;
            type.setText("On The Air TV series:");
            m_frag_processes.collectData("on_the_air");
        }
        else if(item.getItemId()==R.id.sign_out){
            selected=id;
            sign_Out_Method();
        }


        return true;
    }

    private GoogleApiClient mGoogleApiClient;
    private void sign_Out_Method() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });

        startActivity(new Intent(getActivity(), ActivityLogin.class));
        getActivity().finish();
    }


}