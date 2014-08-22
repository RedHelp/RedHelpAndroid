package org.redhelp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.redhelp.adapter.items.ProfileItem;
import org.redhelp.app.R;

/**
 * Created by harshis on 7/13/14.
 */
public class BloodProfileListViewAdapter extends ArrayAdapter<ProfileItem> {

    public BloodProfileListViewAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_blood_profile_list_layout, null);
        }
        ProfileItem item = getItem(position);
        if(item == null)
            return convertView;

        TextView tv_kms = (TextView) convertView.findViewById(R.id.tv_num_kms_row_blood_profile_layout);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv__name_row_blood_profile_layout);
        TextView tv_blood_grp = (TextView) convertView.findViewById(R.id.tv_blood_grp_row_blood_profile_layout);



        tv_kms.setText(item.num_km_str);
        tv_name.setText(item.name_str);
        tv_blood_grp.setText(item.blood_grp);

        return convertView;
    }

}
