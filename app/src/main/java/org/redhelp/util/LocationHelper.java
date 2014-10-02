package org.redhelp.util;

import android.location.Location;

import java.math.BigDecimal;

/**
 * Created by harshis on 7/5/14.
 */
public class LocationHelper {


    public static Double userCurrentLocationLat = null, userCurrentLocationLng = null;
    public static Float calculateDistance(Double location_lat_1, Double location_long_1,
                                           Double location_lat_2, Double location_long_2) {

        if(location_lat_1 == null || location_long_1 == null ||
        location_lat_2 == null || location_long_2 == null)
            return null;

        float[] distance_result = new float[3];
        Location.distanceBetween(location_lat_1, location_long_1, location_lat_2, location_long_2, distance_result);

        if(distance_result == null)
            return null;
        Float distance_in_km = (Math.round(distance_result[0])/1000f);
        distance_in_km = precision(1, distance_in_km);
        return distance_in_km;
    }

    public static Float precision(int decimalPlace, Float d) {

        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
        return bd.floatValue();
    }


    public static String calculateDistanceString(Double location_lat_1, Double location_long_1,
                                           Double location_lat_2, Double location_long_2) {
        Float distanceFloatValue = calculateDistance(location_lat_1, location_long_1, location_lat_2, location_long_2);
        if(distanceFloatValue != null) {
            return String.format("%.1f", distanceFloatValue);
        }
        return "-";
    }
}
