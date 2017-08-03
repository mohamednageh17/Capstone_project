package com.example.berlin.tvseriesapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Berlin on 8/4/2017.
 */

public class Series_Model implements Parcelable {
    public boolean Favourite=false;
    private String Title;
    private String Overview ;
    private String Poster_ImageUrl;
    private String Release_Date;
    private String id;
    private String Vote_Average;
    private String Trailer_Name;
    private String Trailer_Key;
    private String Review_Author;
    private String Review_Content;

    public Series_Model() {

    }

    public Series_Model(String id, String title, String poster, String overview, String userRating,
                       String releaseDate) {
        this.id = id;
        this.Title = title;
        Poster_ImageUrl = poster;
        Overview = overview;
        Vote_Average = userRating;
        Release_Date = releaseDate;
    }

    public static ArrayList<Series_Model> ParsingTrailerData(String JsonData)
    {
        int counter = 0;
        ArrayList<Series_Model> movieModelList = new ArrayList<Series_Model>();
        try {
            JSONObject jj = new JSONObject(JsonData);
            JSONArray ja = jj.getJSONArray("results");
            while (counter < ja.length()) {
                JSONObject j = ja.getJSONObject(counter);
                Series_Model movieModel = new Series_Model();
                movieModel.setTitle(j.getString("name"));
                movieModel.setOverview(j.getString("overview"));
                movieModel.setRelease_Date(j.getString("first_air_date"));
                movieModel.setPoster_ImageUrl(j.getString("poster_path"));
                movieModel.setId(j.getString("id"));
                movieModel.setVote_average(j.getString("vote_average"));
                movieModelList.add(movieModel);
                counter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return movieModelList;
        }
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String overview) {
        Overview = overview;
    }

    public String getPoster_ImageUrl() {
        return Poster_ImageUrl;
    }

    public void setPoster_ImageUrl(String poster_ImageUrl) {
        Poster_ImageUrl = poster_ImageUrl;
    }

    public String getRelease_Date() {
        return Release_Date;
    }

    public void setRelease_Date(String release_Date) {
        Release_Date = release_Date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVote_average() {
        return Vote_Average;
    }

    public void setVote_average(String vote_average) {
        this.Vote_Average = vote_average;
    }

    public String getTrailerName() {
        return Trailer_Name;
    }

    public String getTrailerKey() {
        return Trailer_Key;
    }

    public String getReviewAuthor() {
        return Review_Author;
    }

    public String getReviewContent() {
        return Review_Content;
    }

    public void setReview(review_Model reviewModel) {
        Review_Author = reviewModel.getAuthor();
        Review_Content= reviewModel.getContent();
    }

    public void setTrailer(trailer_Model trailerModel) {
        Trailer_Key = trailerModel.getKey();
        Trailer_Name= trailerModel.getName();
    }

    protected Series_Model(Parcel in) {
        Favourite = in.readByte() != 0x00;
        Title = in.readString();
        Overview = in.readString();
        Poster_ImageUrl = in.readString();
        Release_Date = in.readString();
        id = in.readString();
        Vote_Average = in.readString();
        Trailer_Key = in.readString();
        Trailer_Name = in.readString();
        Review_Author = in.readString();
        Review_Content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (Favourite ? 0x01 : 0x00));
        dest.writeString(Title);
        dest.writeString(Overview);
        dest.writeString(Poster_ImageUrl);
        dest.writeString(Release_Date);
        dest.writeString(id);
        dest.writeString(Vote_Average);
        dest.writeString(Trailer_Key);
        dest.writeString(Trailer_Name);
        dest.writeString(Review_Author);
        dest.writeString(Review_Content);
    }

    @SuppressWarnings("unused")
    public static final Creator<Series_Model> CREATOR = new Creator<Series_Model>() {
        @Override
        public Series_Model createFromParcel(Parcel in) {
            return new Series_Model(in);
        }
        @Override
        public Series_Model[] newArray(int size) {
            return new Series_Model[size];
        }
    };
}