package com.example.rm49865.recordsfood.model;

import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ricardo on 09/02/2016.
 */
public class Restaurant implements Parcelable{

    private Integer id;
    private String name;
    private String tel;
    private String type;
    private Double averageCost;
    private String observation;
    private String latitude;
    private String longitude;
    private String imagePath;

    public Restaurant(){}

    public Restaurant(String name,
                      String tel,
                      String type,
                      Double averageCost,
                      String observation,
                      String latitude,
                      String longitude,
                      String imagePath){
        fill(null, name, tel, type, averageCost, observation, latitude, longitude, imagePath);
    }

    public Restaurant(Integer id,
                      String name,
                      String tel,
                      String type,
                      Double averageCost,
                      String observation,
                      String latitude,
                      String longitude,
                      String imagePath){
        fill(id, name, tel, type, averageCost, observation, latitude, longitude, imagePath);
    }

    public Restaurant(Parcel source){
        fill(source.readInt(), source.readString(), source.readString(), source.readString(), source.readDouble(), source.readString(), source.readString(), source.readString(), source.readString());
    }

    private void fill(Integer id,
                      String name,
                      String tel,
                      String type,
                      Double averageCost,
                      String observation,
                      String latitude,
                      String longitude,
                      String imagePath){
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.type = type;
        this.averageCost = averageCost;
        this.observation = observation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(type);
        dest.writeDouble(averageCost);
        dest.writeString(observation);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(imagePath);
    }

    public static  final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}