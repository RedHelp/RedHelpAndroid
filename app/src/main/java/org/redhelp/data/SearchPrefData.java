package org.redhelp.data;

import com.google.android.gms.maps.model.LatLng;

import org.redhelp.common.types.SearchItemTypes;
import org.redhelp.common.types.SearchRequestType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by harshis on 7/17/14.
 */
public class SearchPrefData {
    SearchRequestType searchRequestType;

    Long radius;
    LatLng currentLocation;

    LatLng southWestLocation;
    LatLng northEastLocation;

    Set<SearchItemTypes> searchItemTypes;

    public static SearchPrefData getDefaultSearchPrefData() {
        LatLng northEast = new LatLng(25.0,85.0);
        LatLng southWest = new LatLng(15.0,75.0);
        Set<SearchItemTypes> searchItemTypes = new HashSet<SearchItemTypes>();
        searchItemTypes.add(SearchItemTypes.EVENTS);
        searchItemTypes.add(SearchItemTypes.BLOOD_PROFILE);
        searchItemTypes.add(SearchItemTypes.BLOOD_REQUEST);
        return new SearchPrefData(SearchRequestType.BOUNDS_BASED, null, null,southWest, northEast, searchItemTypes );
    }
    public SearchPrefData(SearchRequestType searchRequestType, Long radius, LatLng currentLocation, LatLng southWestLocation, LatLng northEastLocation, Set<SearchItemTypes> searchItemTypes) {
        this.searchRequestType = searchRequestType;
        this.radius = radius;
        this.currentLocation = currentLocation;
        this.southWestLocation = southWestLocation;
        this.northEastLocation = northEastLocation;
        this.searchItemTypes = searchItemTypes;
    }

    public SearchRequestType getSearchRequestType() {
        return searchRequestType;
    }

    public void setSearchRequestType(SearchRequestType searchRequestType) {
        this.searchRequestType = searchRequestType;
    }

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getSouthWestLocation() {
        return southWestLocation;
    }

    public void setSouthWestLocation(LatLng southWestLocation) {
        this.southWestLocation = southWestLocation;
    }

    public LatLng getNorthEastLocation() {
        return northEastLocation;
    }

    public void setNorthEastLocation(LatLng northEastLocation) {
        this.northEastLocation = northEastLocation;
    }

    public Set<SearchItemTypes> getSearchItemTypes() {
        return searchItemTypes;
    }

    public void setSearchItemTypes(Set<SearchItemTypes> searchItemTypes) {
        this.searchItemTypes = searchItemTypes;
    }
}
