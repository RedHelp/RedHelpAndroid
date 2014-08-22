package org.redhelp.clusterkarf;

/**
 * Created by harshis on 7/9/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.redhelp.common.types.SearchItemTypes;

/**
 *
 */
public class MarkerData implements Parcelable {

    private SearchItemTypes itemType;
    private Long id;
    private final LatLng latLng;
    private final String label;

    public MarkerData(LatLng latLng, String label, Long id, SearchItemTypes itemType) {
        this.latLng = latLng;
        this.label = label;
        this.id = id;
        this.itemType = itemType;
    }

    /**
     * @return the latLng
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeParcelable(latLng, 0);
        dest.writeString(label);
    }

    public SearchItemTypes getItemType() {
        return itemType;
    }

    public void setItemType(SearchItemTypes itemType) {
        this.itemType = itemType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}