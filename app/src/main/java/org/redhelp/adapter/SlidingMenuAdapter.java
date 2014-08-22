package org.redhelp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.redhelp.adapter.items.SlidingItem;
import org.redhelp.app.R;

/**
 * Created by harshis on 5/31/14.
 */

public class SlidingMenuAdapter extends ArrayAdapter<SlidingItem> {

    public SlidingMenuAdapter(Context context) {
        super(context, 0);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_sliding_menu_list, null);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
        icon.setImageResource(getItem(position).iconRes);
        TextView title = (TextView) convertView.findViewById(R.id.row_title);
        title.setTextSize(15);
        title.setText(getItem(position).tag);

        return convertView;
    }

}