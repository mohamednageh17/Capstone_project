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

import com.example.berlin.tvseriesapp.Adapters.Series_Adapter;
import com.example.berlin.tvseriesapp.Models.Series_Model;
import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Fragments.detailed_Fragment;
import com.example.berlin.tvseriesapp.UI.Fragments.main_Fragment;
import com.example.berlin.tvseriesapp.Utils.GetNetworkData;
import com.example.berlin.tvseriesapp.Utils.NetworkOperations;
import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private ArrayList<Series_Model> series;
    private Series_Adapter seriesAdapter;
    RecyclerView seriesRecyclerView;
    main_Fragment m_frag_processes;
    Context context;
    boolean IsTablet=false;
    Bundle seriesInfo;
    boolean InstanceState;
    static boolean checkFrag = false;
    TextView textView;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        seriesRecyclerView=(RecyclerView)findViewById(R.id.RecyclerView);
        seriesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        textView=(TextView)findViewById(R.id.txtS);
        textView1=(TextView)findViewById(R.id.txtS1);
        textView1.setVisibility(View.GONE);
        seriesInfo=new Bundle();
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
            textView.setText("Search Result: "+query);
            //use the query to search
            collectData("search",query);
        }
    }

    public void collectData(String Key,String q){
        if(MainActivity.NetworkState()) {
            GetNetworkData fetchData = new GetNetworkData(Key, q);
            fetchData.execute();
            fetchData.setNetworkOperations(new NetworkOperations()  {

                @Override
                public void OnDataReached(String JsonData) {
                    series = Series_Model.ParsingTrailerData(JsonData);
                    if(series.size()==0)
                        textView1.setVisibility(View.VISIBLE);
                    seriesAdapter = new Series_Adapter(series, SearchResultsActivity.this);
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
            Toast.makeText(this," No Internet Connection", Toast.LENGTH_SHORT).show();
            CheckTablet();
            ClickEvent();
        }
    }

    private void CheckTablet() {

        Series_Model series_model =  new Series_Model();
        if (IsTablet ) {
            if (series.size() != 0)
                series_model = series.get(0);
            seriesInfo.putParcelable("seriesModel", series_model);
            if (!InstanceState && !checkFrag ) {
                detailed_Fragment detailedFragment1 = new detailed_Fragment();
                detailedFragment1.setArguments(seriesInfo);
                getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment, detailedFragment1).commit();
                checkFrag = true;
            }
        }
    }

    private void ClickEvent() {
        seriesAdapter.setClickListener(new Series_Adapter.RecyclerViewClickListener() {
            @Override
            public void ItemClicked(View v, int position) {
                Series_Model series_model=new Series_Model();
                series_model=series.get(position);
                seriesInfo.putParcelable("seriesModel",series_model);
                IsTablet=getResources().getBoolean(R.bool.isTablet);
                if (!IsTablet) {
                    Intent in = new Intent( SearchResultsActivity.this , detailed_Activity.class);
                    in.putExtra("SeriesInfo", seriesInfo);
                    startActivity(in);
                } else {
                    detailed_Fragment  detailedFragment1=new detailed_Fragment();
                    detailedFragment1.setArguments(seriesInfo);
                    getSupportFragmentManager().beginTransaction().replace(R.id.DetailedFragment1,detailedFragment1).commit();
                }
            }
        });
    }
}
