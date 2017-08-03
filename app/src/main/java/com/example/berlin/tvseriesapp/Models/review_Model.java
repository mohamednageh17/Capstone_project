package com.example.berlin.tvseriesapp.Models;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class review_Model implements Parcelable {
    private String url;
    private String id;
    private String author;
    private String content;
    public review_Model(){ }

    public static review_Model ParsingReviewData(String JsonData )
    {
        review_Model reviewModel =new review_Model();
        try {
            JSONObject jj=new JSONObject(JsonData);
            JSONArray ja=jj.getJSONArray("results");
            JSONObject j=ja.getJSONObject(0);
            reviewModel.setID(j.getString("id"));
            reviewModel.setAuthor(j.getString("author"));
            reviewModel.setContent(j.getString("content"));
        } catch (JSONException e) {
            e.  printStackTrace();
        }
        finally {
            return reviewModel;
        }
    }
    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        url = url;
    }

    protected review_Model(Parcel in) {
        url = in.readString();
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    @SuppressWarnings("unused")
    public static final Creator<review_Model> CREATOR = new Creator<review_Model>() {
        @Override
        public review_Model createFromParcel(Parcel in) {
            return new review_Model(in);
        }

        @Override
        public review_Model[] newArray(int size) {
            return new review_Model[size];
        }
    };
}