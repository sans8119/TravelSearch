package com.uiSearch.data.entity;

import android.graphics.Point;

import com.google.gson.annotations.SerializedName;

public class SearchSuggestionsEntity extends BaseEntity{
    @SerializedName("_id")
    private int id;
    private String key;
    private String name;
    private String fullName;
    @SerializedName("iataAirportCode")
    private String iataAirportCode;
    private String type;
    private String country;
    private GeoPosition geo_position;
    private int locationId;
    private boolean inEurope;
    private String countryCode;
    private boolean coreCountry;
    private double distance;
    private Point poinOnCanvas;

    public Point getPoinOnCanvas() {
        return poinOnCanvas;
    }

    public void setPoinOnCanvas(Point poinOnCanvas) {
        this.poinOnCanvas = poinOnCanvas;
    }

    public String getIataAirportCode() {
        return iataAirportCode;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public GeoPosition getGeoPosition() {
        return geo_position;
    }

    public int getLocationId() {
        return locationId;
    }

    public boolean isInEurope() {
        return inEurope;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public boolean isCoreCountry() {
        return coreCountry;
    }

    public double getDistance() {
        return distance;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("***** User Entity Details *****\n");
        stringBuilder.append("ModelObject [id=").append(id).append("\n").append(",key=").append(key).append("\n").append(", name=").append(name).append("\n")
                .append(", fullName=").append(fullName).append("\n").append(", AirportCode:").append(iataAirportCode).append("\n")
                .append(", type=").append(type).append("\n").append(", country=").append(country).append("\n").append(", geo_position: lat=").append(geo_position.getLatitude()).append("\n")
                .append(", locationId").append(locationId).append("\n").append(",inEurope=").append(inEurope).append("\n").append(",countryCode=").append(",coreCountry=")
                .append(countryCode).append("\n").append(",coreCountry=").append(coreCountry).append("\n").append(",distance=").append(distance).append("]");
        stringBuilder.append("*******************************");
        return stringBuilder.toString();
    }

}
