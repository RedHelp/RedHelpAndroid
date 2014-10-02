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

    Double distanceFactor;

    LatLng currentLocation;

    LatLng southWestLocation;
    LatLng northEastLocation;

    Set<SearchItemTypes> searchItemTypes;

    public static SearchPrefData getDefaultSearchPrefData() {

        Set<SearchItemTypes> searchItemTypes = new HashSet<SearchItemTypes>();
        searchItemTypes.add(SearchItemTypes.EVENTS);
        searchItemTypes.add(SearchItemTypes.BLOOD_PROFILE);
        searchItemTypes.add(SearchItemTypes.BLOOD_REQUEST);
        return new SearchPrefData(null, 0.22, null, null, null, searchItemTypes );
    }
    public SearchPrefData(SearchRequestType searchRequestType, Double distanceFactor, LatLng currentLocation, LatLng southWestLocation, LatLng northEastLocation, Set<SearchItemTypes> searchItemTypes) {
        this.searchRequestType = searchRequestType;
        this.distanceFactor = distanceFactor;
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

    public void updateLocation(LatLng userCurrentLocation) {
        SearchRequestType searchRequestType;
        LatLng northEast = null, southWest = null;
        if(userCurrentLocation != null) {
            Double nortEastLatitude = userCurrentLocation.latitude + distanceFactor;
            Double nortEastLongitude = userCurrentLocation.longitude +
                    (distanceFactor / Math.cos(Math.toRadians(nortEastLatitude)));

            Double southWestLatitude = userCurrentLocation.latitude - distanceFactor;
            Double southWestLongitude = userCurrentLocation.longitude -
                    (distanceFactor / Math.cos(Math.toRadians(southWestLatitude)));

            northEast = new LatLng(nortEastLatitude, nortEastLongitude);
            southWest = new LatLng(southWestLatitude, southWestLongitude);
            searchRequestType = SearchRequestType.BOUNDS_BASED;
        }
        else{
            searchRequestType = SearchRequestType.CITY_BASED;
        }
        this.southWestLocation = southWest;
        this.northEastLocation = northEast;
        this.searchRequestType = searchRequestType;

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

    public Double getDistanceFactor() {
        return distanceFactor;
    }

    public void setDistanceFactor(Double distanceFactor) {
        this.distanceFactor = distanceFactor;
    }

}
