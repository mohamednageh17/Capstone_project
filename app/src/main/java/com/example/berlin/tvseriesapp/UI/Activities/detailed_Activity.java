package com.example.berlin.tvseriesapp.UI.Activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.berlin.tvseriesapp.R;
import com.example.berlin.tvseriesapp.UI.Fragments.detailed_Fragment;


public class detailed_Activity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        detailed_Fragment myDetailed_fragment=new detailed_Fragment();
        Bundle MoviesInfo= getIntent().getBundleExtra("MoviesInfo");
        myDetailed_fragment.setArguments(MoviesInfo);
        getSupportFragmentManager().beginTransaction().add(R.id.DetailedFragment,myDetailed_fragment).commit();
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