package com.example.berlin.tvseriesapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.berlin.tvseriesapp.Data.SeriesContract;
import com.example.berlin.tvseriesapp.Models.Series_Model;
import com.example.berlin.tvseriesapp.R;

import java.util.ArrayList;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}
    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        ArrayList<Series_Model> series_modelArrayList=new ArrayList<>();

        Context context;

        public ListRemoteViewsFactory(Context ctx){

            context=ctx;
        }
        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            series_modelArrayList=getFavouriteMovies();

        }

        public ArrayList<Series_Model> getFavouriteMovies(){
            ArrayList<Series_Model>FavouriteList=new ArrayList<>();
            Series_Model movie_model;
            Cursor cursor;
            cursor=context.getContentResolver().query(SeriesContract.SeriesEntry.CONTENT_URI,
                    SeriesContract.SeriesEntry.Series_COLUMNS,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_ID);
                    String title = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_TITLE);
                    String posterPath = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_POSTER_PATH);
                    String overview = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_OVERVIEW);
                    String rating = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_VOTE_AVERAGE);
                    String releaseDate = cursor.getString(SeriesContract.SeriesEntry.COL_SERIES_RELEASE_DATE);
                    movie_model  = new Series_Model(id, title, posterPath, overview, rating, releaseDate);
                    FavouriteList.add(movie_model);
                } while (cursor.moveToNext());
            }
            return FavouriteList;
        }
        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return series_modelArrayList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.series_row_item_widget);

            if(series_modelArrayList==null)
                return views;

            views.setTextViewText(R.id.ingredient_name_widget,series_modelArrayList.get(position).getTitle());
            views.setTextViewText(R.id.ingredient_measure_widget,series_modelArrayList.get(position).getRelease_Date());

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

}
