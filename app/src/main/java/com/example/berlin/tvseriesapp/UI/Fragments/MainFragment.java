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

import com.example.berlin.tvseriesapp.Adapters.SeriesAdapter;
import com.example.berlin.tvseriesapp.UI.Activities.ActivityLogin;
import com.example.berlin.tvseriesapp.Data.SeriesContract;
import com.example.berlin.tvseriesapp.Models.SeriesModel;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Activities.DetailedActivity;
import com.example.berlin.tvseriesapp.UI.Activities.MainActivity;
import com.example.berlin.tvseriesapp.UI.Activities.SearchResultsActivity;
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


public class MainFragment extends android.support.v4.app.Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    boolean InstanceState;
    Activity CurrentActivity;
    ArrayList<SeriesModel> series;
    static boolean checkFrag = false;
    SeriesAdapter seriesAdapter;
    Bundle SeriesInfo;
    Context context;
    boolean IsTablet;
    RecyclerView seriesRecyclerView;
    View view;
    mainFragment_processes m_frag_processes;
    TextView type;
    int flag;

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    public static final int LOADER_ID = 101;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        flag = 0;
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
                .enableAutoManage(getActivity(), (GoogleApiClient.OnConnectionFailedListener) getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        view = inflater.inflate(R.layout.main_fragment, container, false);
        m_frag_processes = new mainFragment_processes();
        context = view.getContext();
        CurrentActivity = getActivity();
        seriesAdapter = new SeriesAdapter();
        IsTablet = getResources().getBoolean(R.bool.isTablet);
        SeriesInfo = new Bundle();
        type = (TextView) view.findViewById(R.id.type);

        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
        } else {
            type.setText(R.string.PopularTVseries);
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
        outState.putParcelableArrayList("series", series);
        outState.putInt(MENU_SELECTED, selected);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SeriesContract.SeriesEntry.CONTENT_URI, SeriesContract.SeriesEntry.Series_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<SeriesModel> FavouriteList = new ArrayList<>();
        SeriesModel series_model;
        if (flag == 1) {
            if (data != null && data.moveToFirst()) {
                do {
                    String id = data.getString(SeriesContract.SeriesEntry.COL_SERIES_ID);
                    String title = data.getString(SeriesContract.SeriesEntry.COL_SERIES_TITLE);
                    String posterPath = data.getString(SeriesContract.SeriesEntry.COL_SERIES_POSTER_PATH);
                    String overview = data.getString(SeriesContract.SeriesEntry.COL_SERIES_OVERVIEW);
                    String rating = data.getString(SeriesContract.SeriesEntry.COL_SERIES_VOTE_AVERAGE);
                    String releaseDate = data.getString(SeriesContract.SeriesEntry.COL_SERIES_RELEASE_DATE);
                    series_model = new SeriesModel(id, title, posterPath, overview, rating, releaseDate);
                    FavouriteList.add(series_model);
                } while (data.moveToNext());
            }
            data.close();
            series = FavouriteList;
            seriesAdapter = new SeriesAdapter(FavouriteList, context);
            seriesRecyclerView.setAdapter(seriesAdapter);
            m_frag_processes.CheckTablet();
            m_frag_processes.ClickEvent();
            DetailedFragment.IsFavouriteSelected(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public class mainFragment_processes {

        public mainFragment_processes() {
        }

        public void collectData(String Key) {
            if (MainActivity.NetworkState()) {
                GetNetworkData getNetworkData = new GetNetworkData(Key, "");
                ClickEvent();
                getNetworkData.execute();
                getNetworkData.setNetworkOperations(new NetworkOperations() {

                    @Override
                    public void OnDataReached(String JsonData) {
                        series = SeriesModel.ParsingTrailerData(JsonData);
                        seriesAdapter = new SeriesAdapter(series, context);
                        seriesRecyclerView.setAdapter(seriesAdapter);
                        if (JsonData == null)
                            Toast.makeText(MainActivity.ctx, R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();
                        CheckTablet();
                        ClickEvent();
                    }
                });
            } else {
                series = new ArrayList<>();
                seriesAdapter = new SeriesAdapter(series, context);
                seriesRecyclerView.setAdapter(seriesAdapter);
                Toast.makeText(getActivity(),R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();
                CheckTablet();
                ClickEvent();
            }
        }

        public void CheckTablet() {
            SeriesModel seriesModel = new SeriesModel();
            if (IsTablet) {
                if (series.size() != 0)
                    seriesModel = series.get(0);
                SeriesInfo.putParcelable("seriesModel", seriesModel);
                if (!InstanceState && !checkFrag) {
                    DetailedFragment detailedFragment1 = new DetailedFragment();
                    detailedFragment1.setArguments(SeriesInfo);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();
                    checkFrag = true;
                }
            }
        }

        public void ClickEvent() {
            seriesAdapter.setClickListener(new SeriesAdapter.RecyclerViewClickListener() {
                @Override
                public void ItemClicked(View v, int position) {
                    SeriesModel seriesModel = new SeriesModel();
                    seriesModel = series.get(position);
                    SeriesInfo.putParcelable("seriesModel", seriesModel);
                    if (!IsTablet) {
                        Intent in = new Intent(CurrentActivity, DetailedActivity.class);
                        in.putExtra("SeriesInfo", SeriesInfo);
                        startActivity(in);
                    } else {
                        DetailedFragment detailedFragment1 = new DetailedFragment();
                        detailedFragment1.setArguments(SeriesInfo);
                        getFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();

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
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        ComponentName cn = new ComponentName(getActivity(), SearchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.pop) {
            selected = id;
            type.setText(R.string.PopularTVseries);
            m_frag_processes.collectData("Popular Movies");

        } else if (item.getItemId() == R.id.top) {
            selected = id;
            type.setText(R.string.TopRatedTVseries);
            m_frag_processes.collectData("Top Rated Movies");
        } else if (item.getItemId() == R.id.fav) {
            selected = id;
            type.setText(R.string.FavouriteTVseries);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            flag = 1;

        } else if (item.getItemId() == R.id.airing_today) {
            selected = id;
            type.setText(R.string.AiringTodayTVseries);
            m_frag_processes.collectData("airing_today");
        } else if (item.getItemId() == R.id.on_the_air) {
            selected = id;
            type.setText(R.string.OnTheAirTVseries);
            m_frag_processes.collectData("on_the_air");
        } else if (item.getItemId() == R.id.sign_out) {
            selected = id;
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