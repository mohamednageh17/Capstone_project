package com.example.berlin.tvseriesapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrailerModel implements Parcelable {
    private String id;
    private String key;
    private String name;

    public TrailerModel() {
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public static TrailerModel ParsingTrailerData(String JsonData) {
        TrailerModel trailerModel = new TrailerModel();
        try {
            JSONObject jj = new JSONObject(JsonData);
            JSONArray ja = jj.getJSONArray("results");
            JSONObject j = ja.getJSONObject(0);
            trailerModel.setID(j.getString("id"));
            trailerModel.setKey(j.getString("key"));
            trailerModel.setName(j.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return trailerModel;
        }
    }

    protected TrailerModel(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Creator<TrailerModel> CREATOR = new Creator<TrailerModel>() {
        @Override
        public TrailerModel createFromParcel(Parcel in) {
            return new TrailerModel(in);
        }

        @Override
        public TrailerModel[] newArray(int size) {
            return new TrailerModel[size];
        }
    };
}