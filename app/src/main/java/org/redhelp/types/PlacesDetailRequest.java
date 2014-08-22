package org.redhelp.types;

/**
 * Created by harshis on 6/9/14.
 */
public class PlacesDetailRequest {
    private final String httpUrl = "https://maps.googleapis.com/maps/api/place/details/json";

    private String key;
    private String reference;
    private boolean sensor;

    public String generateUrl() {
        String sensorString = sensor ? "true" : "false";
        return httpUrl + "?" + "key=" + key + "&" + "reference=" + reference + "&" + "sensor=" + sensorString;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isSensor() {
        return sensor;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    @Override
    public String toString() {
        return "PlacesDetailRequest{" +
                "httpUrl='" + httpUrl + '\'' +
                ", key='" + key + '\'' +
                ", reference='" + reference + '\'' +
                ", sensor=" + sensor +
                '}';
    }
}
