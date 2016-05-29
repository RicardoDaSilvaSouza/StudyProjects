package com.example.rm49865.recordsfood.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ricardo on 09/02/2016.
 */
public class SynchronizeDataService extends IntentService{

    private final String TAG = SynchronizeDataService.class.getName();
    private final String FIELD_JSON_NAME_RESTAURANT = "NOMERESTAURANTE";
    private final String FIELD_JSON_TEL_RESTAURANT = "TELEFONE";
    private final String FIELD_JSON_TYPE_RESTAURANT = "TIPO";
    private final String FIELD_JSON_AVG_RESTAURANT = "CustoMedio";
    private final String FIELD_JSON_OBS_RESTAURANT = "OBSERVACAO";
    private final String FIELD_JSON_LOCAL_RESTAURANT = "LOCALIZACAO";

    public static final int DOWNLAOD_SUCESS = 0;
    public static final int DOWNLOAD_FAIL = -1;
    public static final String PARAM_RESULT_RECEIVER = "resultReceiver";
    public static final String PARAM_RESTAURANT_LIST = "restaurantList";
    public static final String URL_CONTENT = "http://heiderlopes.com.br/restaurantes/restaurantes.json";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SynchronizeDataService() {
        super(SynchronizeDataService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(PARAM_RESULT_RECEIVER);
        try {
            URL downloadUrl = new URL(URL_CONTENT);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();

            switch (connection.getResponseCode()){
                case 200:
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null){
                        json.append(line);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(PARAM_RESTAURANT_LIST, (ArrayList<? extends Parcelable>) getRestaurantsFromJson(json.toString()));
                    receiver.send(DOWNLAOD_SUCESS, bundle);
                    break;
                default:
                    throw new Exception(getString(R.string.message_download_error));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            receiver.send(DOWNLOAD_FAIL, Bundle.EMPTY);
        }
    }

    private List<Restaurant> getRestaurantsFromJson(String json) throws JSONException {
        List<Restaurant> restaurants = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        for(int i = 0; i < jsonArray.length(); i++){
            restaurants.add(getRestaurantFromJson(jsonArray.getJSONObject(i)));
        }

        return restaurants;
    }

    private Restaurant getRestaurantFromJson(JSONObject jsonObject) throws JSONException {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(jsonObject.getString(FIELD_JSON_NAME_RESTAURANT));
        restaurant.setTel(jsonObject.getString(FIELD_JSON_TEL_RESTAURANT));
        restaurant.setType(jsonObject.getString(FIELD_JSON_TYPE_RESTAURANT));
        restaurant.setAverageCost(jsonObject.getDouble(FIELD_JSON_AVG_RESTAURANT));
        restaurant.setObservation(jsonObject.getString(FIELD_JSON_OBS_RESTAURANT));
        String location = jsonObject.getString(FIELD_JSON_LOCAL_RESTAURANT);
        int commaIndex = location.indexOf(",");
        restaurant.setLatitude(location.substring(0, commaIndex));
        restaurant.setLongitude(location.substring(commaIndex+1, location.length()));
        return restaurant;
    }
}
