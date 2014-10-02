package org.redhelp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.redhelp.adapter.items.ProfileItem;
import org.redhelp.app.R;

import java.io.ByteArrayInputStream;

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
        ImageView iv_pro_pic = (ImageView) convertView.findViewById(R.id.iv_pic_blood_profile_layout);

        if(item.showRequestStatus == true) {
            TextView tv_status = (TextView) convertView.findViewById(R.id.tv_request_status_blood_profile_layout);
            String statusString = (item.isRequestAccepted == true) ? "Request Accepted" : "Request Pending";
            tv_status.setText(statusString);
            tv_status.setVisibility(View.VISIBLE);
        }
        iv_pro_pic.setImageBitmap(null);
        if(item.pic != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(item.pic);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            iv_pro_pic.setImageBitmap(bitmap);
        } else {
            iv_pro_pic.setImageResource(R.drawable.my_account_grey);
        }

        tv_kms.setText(item.num_km_str);
        tv_name.setText(item.name_str);
        tv_blood_grp.setText(item.blood_grp);

        return convertView;
    }

}
