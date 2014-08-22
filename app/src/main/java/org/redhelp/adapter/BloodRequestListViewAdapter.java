package org.redhelp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.redhelp.adapter.items.RequestItem;
import org.redhelp.app.R;


/**
 * Created by harshis on 7/12/14.
 */
public class BloodRequestListViewAdapter  extends ArrayAdapter<RequestItem>{
    public BloodRequestListViewAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_blood_request_list_layout, null);
        }
        RequestItem item = getItem(position);
        if(item == null)
            return convertView;

        TextView tv_kms = (TextView) convertView.findViewById(R.id.tv_num_kms_row_blood_request_layout);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title_row_blood_request_layout);
        TextView tv_venue = (TextView) convertView.findViewById(R.id.tv_venue_row_blood_request_layout);
        TextView tv_blood_grp = (TextView) convertView.findViewById(R.id.tv_blood_grps_row_blood_request_layout);


        tv_kms.setText(item.num_km_str);
        tv_title.setText(item.title_str);
        tv_venue.setText(item.venue_str);
        tv_blood_grp.setText(item.blood_grps_str);

        return convertView;
    }

}
