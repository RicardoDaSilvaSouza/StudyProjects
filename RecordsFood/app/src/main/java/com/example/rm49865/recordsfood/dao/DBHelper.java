package com.example.rm49865.recordsfood.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ricardo on 09/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static final String TABLE_RESTAURANT = "restaurant";
    public static final String DB_NAME = "restaurant.db";
    public static final Integer DB_VERSION = 1;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ")
                .append(TABLE_RESTAURANT)
                .append("(")
                .append(RestaurantDAO.ID)
                .append(" integer primary key autoincrement, ")
                .append(RestaurantDAO.NAME)
                .append(" text, ")
                .append(RestaurantDAO.TEL)
                .append(" text, ")
                .append(RestaurantDAO.TYPE)
                .append(" text, ")
                .append(RestaurantDAO.AVERAGE_COST)
                .append(" real, ")
                .append(RestaurantDAO.OBSERVATION)
                .append(" text, ")
                .append(RestaurantDAO.LATITUDE)
                .append(" text, ")
                .append(RestaurantDAO.LONGITUDE)
                .append(" text, ")
                .append(RestaurantDAO.IMAGE_PATH)
                .append(" text)");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT);
        onCreate(db);
    }
}
