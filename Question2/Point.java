package com.GeoJson;

import org.json.JSONArray;
import org.json.JSONException;

public class Point {
    private double longitude;
    private double latitude;
    private int srid = 4326;

    // Construct a point from longitude, latitude, and srid values
    public Point(double longitude, double latitude, int srid) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.srid = srid;
    }

    // Construct a point from the coordinates array
    public Point(JSONArray jsonArray) {
         try {
             longitude = jsonArray.getDouble(0);
             latitude = jsonArray.getDouble(1);
         } catch(JSONException jse) {
             jse.printStackTrace();
         }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
