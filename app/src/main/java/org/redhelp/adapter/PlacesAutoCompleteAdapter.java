package org.redhelp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.redhelp.adapter.items.PlacesAutoCompleteItem;
import org.redhelp.app.R;

/**
 * Created by harshis on 6/9/14.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<PlacesAutoCompleteItem> {

    public PlacesAutoCompleteAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_plain_text_view, null);
        }
        TextView description = (TextView) convertView.findViewById(R.id.tv_plain_text_row_layout);
        description.setTextSize(10);
        description.setTextColor(Color.BLACK);
        description.setText(getItem(position).description);
        return convertView;
    }

}
