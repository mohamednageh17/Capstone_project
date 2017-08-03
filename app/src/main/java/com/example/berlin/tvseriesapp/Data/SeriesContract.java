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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SeriesContract {

    public static final String CONTENT_AUTHORITY = "mnageh.tvseries";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SERIES = "movie";

    public static final class SeriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SERIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SERIES;

        public static final String TABLE_NAME = "series";
        public static final String COLUMN_SERIES_ID = "series_id";
        public static final String COLUMN_SERIES_TITLE = "original_title";
        public static final String COLUMN_SERIES_POSTER_PATH = "poster_path";
        public static final String COLUMN_SERIES_OVERVIEW = "overview";
        public static final String COLUMN_SERIES_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_SERIES_RELEASE_DATE = "release_date";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIE_COLUMNS = {
                COLUMN_SERIES_ID,
                COLUMN_SERIES_TITLE,
                COLUMN_SERIES_POSTER_PATH,
                COLUMN_SERIES_OVERVIEW,
                COLUMN_SERIES_VOTE_AVERAGE,
                COLUMN_SERIES_RELEASE_DATE,

        };

        public static final int COL_SERIES_ID = 0;
        public static final int COL_SERIES_TITLE = 1;
        public static final int COL_SERIES_POSTER_PATH = 2;
        public static final int COL_SERIES_OVERVIEW = 3;
        public static final int COL_SERIES_VOTE_AVERAGE = 4;
        public static final int COL_SERIES_RELEASE_DATE = 5;

    }
}
