package com.uiSearch.utils;

import android.location.Location;
import com.uiSearch.data.entity.BaseEntity;
import com.uiSearch.data.entity.SearchSuggestionsEntity;

import java.util.Comparator;



public class SearchSuggestionsEntityComparator implements Comparator<SearchSuggestionsEntity> {
    private Location location;

    public SearchSuggestionsEntityComparator(Location location) {
        this.location=location;
    }

    @Override
    public int compare(SearchSuggestionsEntity BaseEntity1, SearchSuggestionsEntity BaseEntity2) {
        Location loc1 = new Location("");
        loc1.setLatitude(((SearchSuggestionsEntity) BaseEntity1).getGeoPosition().getLatitude());
        loc1.setLongitude(((SearchSuggestionsEntity)BaseEntity1).getGeoPosition().getLongitude());
        Location loc2 = new Location("");
        loc2.setLatitude(((SearchSuggestionsEntity)BaseEntity2).getGeoPosition().getLatitude());
        loc2.setLongitude(((SearchSuggestionsEntity)BaseEntity2).getGeoPosition().getLatitude());
        float distanceInMeters1 = location.distanceTo(loc1);
        float distanceInMeters2 = location.distanceTo(loc2);
        if (distanceInMeters1 < distanceInMeters2) {
            return 1;
        } else {
            return -1;
        }
    }

}
