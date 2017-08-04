package com.example.berlin.tvseriesapp.UI.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berlin.tvseriesapp.Adapters.SeriesAdapter;
import com.example.berlin.tvseriesapp.Models.SeriesModel;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Fragments.DetailedFragment;
import com.example.berlin.tvseriesapp.UI.Fragments.MainFragment;
import com.example.berlin.tvseriesapp.Utils.GetNetworkData;
import com.example.berlin.tvseriesapp.Utils.NetworkOperations;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private ArrayList<SeriesModel> series;
    private SeriesAdapter seriesAdapter;
    RecyclerView seriesRecyclerView;
    MainFragment m_frag_processes;
    Context context;
    boolean IsTablet = false;
    Bundle seriesInfo;
    boolean InstanceState;
    static boolean checkFrag = false;
    TextView textView;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        seriesRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        seriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        textView = (TextView) findViewById(R.id.txtS);
        textView1 = (TextView) findViewById(R.id.txtS1);
        textView1.setVisibility(View.GONE);
        seriesInfo = new Bundle();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            textView.setText("Search Result: " + query);
            //use the query to search
            collectData("search", query);
        }
    }

    public void collectData(String Key, String q) {
        if (MainActivity.NetworkState()) {
            GetNetworkData fetchData = new GetNetworkData(Key, q);
            fetchData.execute();
            fetchData.setNetworkOperations(new NetworkOperations() {

                @Override
                public void OnDataReached(String JsonData) {
                    series = SeriesModel.ParsingTrailerData(JsonData);
                    if (series.size() == 0)
                        textView1.setVisibility(View.VISIBLE);
                    seriesAdapter = new SeriesAdapter(series, SearchResultsActivity.this);
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
            Toast.makeText(this,R.string.NoInternetConnection, Toast.LENGTH_SHORT).show();
            CheckTablet();
            ClickEvent();
        }
    }

    private void CheckTablet() {
        SeriesModel series_model = new SeriesModel();
        if (IsTablet) {
            if (series.size() != 0)
                series_model = series.get(0);
            seriesInfo.putParcelable("seriesModel", series_model);
            if (!InstanceState && !checkFrag) {
                DetailedFragment detailedFragment1 = new DetailedFragment();
                detailedFragment1.setArguments(seriesInfo);
                getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();
                checkFrag = true;
            }
        }
    }

    private void ClickEvent() {
        seriesAdapter.setClickListener(new SeriesAdapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                SeriesModel series_model = new SeriesModel();
                series_model = series.get(position);
                seriesInfo.putParcelable("seriesModel", series_model);
                IsTablet = getResources().getBoolean(R.bool.isTablet);
                if (!IsTablet) {
                    Intent in = new Intent(SearchResultsActivity.this, DetailedActivity.class);
                    in.putExtra("SeriesInfo", seriesInfo);
                    startActivity(in);
                } else {
                    DetailedFragment detailedFragment1 = new DetailedFragment();
                    detailedFragment1.setArguments(seriesInfo);
                    getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment1, detailedFragment1).commit();
                }
            }
        });
    }
}