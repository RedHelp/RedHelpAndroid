package org.redhelp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.redhelp.adapter.items.EventItem;
import org.redhelp.app.R;

/**
 * Created by harshis on 7/7/14.
 */

public class EventListViewAdapter extends ArrayAdapter<EventItem>{

    public EventListViewAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_event_list_layout, null);
        }
        EventItem item = getItem(position);
        if(item == null)
            return convertView;

        TextView tv_kms = (TextView) convertView.findViewById(R.id.tv_num_kms_row_event_list_layout);
        TextView tv_event_name = (TextView) convertView.findViewById(R.id.tv_event_name_row_event_list_layout);
        TextView tv_venue = (TextView) convertView.findViewById(R.id.tv_venue_row_event_list_layout);
        TextView tv_scheduled_time = (TextView) convertView.findViewById(R.id.tv_scheduled_time_row_event_list_layout);

        tv_kms.setText(item.num_km_str);
        tv_event_name.setText(item.event_name_str);
        tv_venue.setText(item.venue_str);
        tv_scheduled_time.setText(item.date_str);

        return convertView;
    }

}
