package org.redhelp.adapter.items;

/**
 * Created by harshis on 7/7/14.
 */
public class EventItem {
    public Long e_id;
    public String num_km_str;
    public String event_name_str;
    public String venue_str;
    public String date_str;

    public EventItem(Long e_id, String km_str, String event_name, String venue_str, String date_str) {
        this.e_id = e_id;
        this.num_km_str = km_str;
        this.event_name_str = event_name;
        this.venue_str = venue_str;
        this.date_str = date_str;
    }

}
