package com.example.finalrestaurant.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class YelpSearchResults {

    private ArrayList<Restaurant> businesses;
    private Region region;

    public ArrayList<Restaurant> getBusinesses() {
        return businesses;

    }
    public void setBusinesses(ArrayList<Restaurant> businesses) {
        this.businesses = businesses;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public class Region{
        Center center;

        public Center getCenter() {
            return center;
        }

        public void setCenter(Center center) {
            this.center = center;
        }

        public class Center{
            Double longitude;
            Double latitude;

            public Double getLongitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }

            public Double getLatitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }
        }
    }
}
