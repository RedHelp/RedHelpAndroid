package org.redhelp.adapter.items;

/**
 * Created by harshis on 7/13/14.
 */
public class ProfileItem {
    public Long b_p_id;
    public String num_km_str;
    public String name_str;
    public byte[] pic;
    public String blood_grp;
    public boolean showRequestStatus;
    public boolean isRequestAccepted;

    public ProfileItem(Long b_p_id, String num_km_str, String name_str, String blood_grp, byte[] pic) {
        this.b_p_id = b_p_id;
        this.num_km_str = num_km_str;
        this.name_str = name_str;
        this.pic = pic;
        this.blood_grp = blood_grp;
    }

}
