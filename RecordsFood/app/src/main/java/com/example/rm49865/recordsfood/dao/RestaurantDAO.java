package com.example.rm49865.recordsfood.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 09/02/2016.
 */
public class RestaurantDAO {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String TEL = "tel";
    public static final String TYPE = "type";
    public static final String AVERAGE_COST = "average_cost";
    public static final String OBSERVATION = "observation";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IMAGE_PATH = "image_path";

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public RestaurantDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    public Boolean insert(Restaurant restaurant){
        Boolean result;
        ContentValues values = getContentValues(restaurant);

        db = dbHelper.getWritableDatabase();
        if(db.insert(DBHelper.TABLE_RESTAURANT, null,values) != -1){
            result = true;
        } else {
            result = false;
        }

        db.close();

        return result;
    }

    public Boolean update(Restaurant restaurant){
        Boolean result;
        ContentValues values = getContentValues(restaurant);

        db = dbHelper.getWritableDatabase();
        if(db.update(DBHelper.TABLE_RESTAURANT, values, ID + " = ?", new String[]{restaurant.getId().toString()}) != -1){
            result = true;
        } else {
            result = false;
        }

        db.close();

        return result;
    }

    public Boolean delete(Integer id){
        Boolean result;

        db = dbHelper.getReadableDatabase();
        if(db.delete(DBHelper.TABLE_RESTAURANT, ID + " = ? ", new String[]{id.toString()}) != -1){
            result = true;
        } else {
            result = false;
        }
        db.close();
        return result;
    }

    public List<Restaurant> getRestaurants(){
        List<Restaurant> restaurants = new ArrayList<>();
        Cursor cursor;

        db = dbHelper.getReadableDatabase();
        cursor = db.query(DBHelper.TABLE_RESTAURANT, null, null, null, null, null, null, null);

        if(cursor != null){
            while(cursor.moveToNext()){
                restaurants.add(getRestaurantFromCursor(cursor));
            }
        }

        db.close();

        return restaurants;
    }

    public List<Restaurant> findByFilter(String filter){
        List<Restaurant> restaurants = new ArrayList<>();
        Cursor cursor;

        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT * FROM ")
                .append(DBHelper.TABLE_RESTAURANT)
                .append(" WHERE ")
                .append(NAME)
                .append(" LIKE '%")
                .append(filter)
                .append("%' OR ")
                .append(TYPE)
                .append(" LIKE '%")
                .append(filter)
                .append("%' OR ")
                .append(AVERAGE_COST)
                .append(" = ?");
        cursor = db.rawQuery(query.toString(), new String[]{filter});

        if(cursor != null){
            while (cursor.moveToNext()){
                restaurants.add(getRestaurantFromCursor(cursor));
            }
        }

        db.close();

        return restaurants;
    }

    public Restaurant findById(Integer id){
        Restaurant restaurant = null;
        Cursor cursor;

        db = dbHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(DBHelper.TABLE_RESTAURANT)
                .append(" WHERE ")
                .append(ID + " = ?");
        cursor = db.rawQuery(sql.toString(), new String[]{id.toString()});

        if(cursor != null){
            cursor.moveToFirst();
            restaurant = getRestaurantFromCursor(cursor);
        }

        db.close();

        return restaurant;
    }

    public List<Restaurant> findByName(String name){
        List<Restaurant> restaurants = new ArrayList<>();
        Cursor cursor;

        db = dbHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(DBHelper.TABLE_RESTAURANT)
                .append(" WHERE ")
                .append(NAME)
                .append(" LIKE ")
                .append("'%")
                .append(name)
                .append("%'");
        cursor = db.rawQuery(sql.toString(), new String[]{});

        if(cursor != null){
            while (cursor.moveToNext()){
                restaurants.add(getRestaurantFromCursor(cursor));
            }
        }

        db.close();

        return restaurants;
    }

    private ContentValues getContentValues(Restaurant restaurant){
        ContentValues values = new ContentValues();
        values.put(NAME, restaurant.getName());
        values.put(TEL, restaurant.getTel());
        values.put(TYPE, restaurant.getType());
        values.put(AVERAGE_COST, restaurant.getAverageCost());
        values.put(OBSERVATION, restaurant.getObservation());
        values.put(LATITUDE, restaurant.getLatitude());
        values.put(LONGITUDE, restaurant.getLongitude());
        values.put(IMAGE_PATH, restaurant.getImagePath());
        return values;
    }

    private Restaurant getRestaurantFromCursor(Cursor cursor){
        return new Restaurant(cursor.getInt(cursor.getColumnIndex(ID)),
                cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(TEL)),
                cursor.getString(cursor.getColumnIndex(TYPE)),
                cursor.getDouble(cursor.getColumnIndex(AVERAGE_COST)),
                cursor.getString(cursor.getColumnIndex(OBSERVATION)),
                cursor.getString(cursor.getColumnIndex(LATITUDE)),
                cursor.getString(cursor.getColumnIndex(LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(IMAGE_PATH)));
    }
}
