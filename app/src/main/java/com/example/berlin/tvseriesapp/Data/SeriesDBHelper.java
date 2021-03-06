/*
 * Copyright 2016.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.berlin.tvseriesapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies data.
 */
public class SeriesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "series.db";

    public SeriesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + SeriesContract.SeriesEntry.TABLE_NAME
                + " (" +
                SeriesContract.SeriesEntry._ID + " INTEGER PRIMARY KEY ," +
                SeriesContract.SeriesEntry.COLUMN_SERIES_ID + " INTEGER NOT NULL, " +
                SeriesContract.SeriesEntry.COLUMN_SERIES_TITLE + " TEXT NOT NULL, " +
                SeriesContract.SeriesEntry.COLUMN_SERIES_POSTER_PATH + " TEXT NOT NULL, " +
                SeriesContract.SeriesEntry.COLUMN_SERIES_OVERVIEW + " TEXT NOT NULL, " +
                SeriesContract.SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE + " TEXT NOT NULL, " +
                SeriesContract.SeriesEntry.COLUMN_SERIES_RELEASE_DATE + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SeriesContract.SeriesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
