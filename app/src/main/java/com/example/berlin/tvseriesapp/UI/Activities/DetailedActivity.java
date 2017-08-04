package com.example.berlin.tvseriesapp.UI.Activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Fragments.DetailedFragment;


public class DetailedActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        DetailedFragment myDetailed_fragment = new DetailedFragment();
        Bundle MoviesInfo = getIntent().getBundleExtra("SeriesInfo");
        myDetailed_fragment.setArguments(MoviesInfo);
        getSupportFragmentManager().beginTransaction().add(R.id.DetailedFragment, myDetailed_fragment).commit();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}