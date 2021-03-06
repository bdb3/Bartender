package com.example.android.absolutmixr.Model;

import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by melaniekwon on 8/6/17.
 */

public class WishlistContract {
    private WishlistContract() {}

    public static final class WishlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "wishlist";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_SKILL = "skill";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PICTURE_URL = "picture";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_TASTES = "tastes";
        public static final String COLUMN_OCCASSIONS = "occassions";
        public static final String COLUMN_THUMBSUP = "thumpsup";
    }
}
