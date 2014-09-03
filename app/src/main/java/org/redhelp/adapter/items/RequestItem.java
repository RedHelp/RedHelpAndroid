package org.redhelp.adapter.items;

/**
 * Created by harshis on 7/12/14.
 */
public class RequestItem {
    public Long b_r_id;
    public String num_km_str;
    public String title_str;
    public String blood_grps_str;

    public RequestItem(Long b_r_id, String num_km_str,
                       String title_str,
                       String blood_grps_str) {
        this.b_r_id = b_r_id;
        this.num_km_str = num_km_str;
        this.title_str = title_str;
        this.blood_grps_str = blood_grps_str;
    }
}
