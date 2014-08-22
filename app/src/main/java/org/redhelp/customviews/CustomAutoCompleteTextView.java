package org.redhelp.customviews;

/**
 * Created by harshis on 6/9/14.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import org.redhelp.adapter.items.PlacesAutoCompleteItem;

import java.util.HashMap;

/** Customizing AutoCompleteTextView to return Place Description
 * corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    private  static final String TAG = "RedHelp:CustomAutoCompleteTextView";
    public PlacesAutoCompleteItem itemSelected;
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the place description corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */


            HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
           // placesAutoCompleteItem = (PlacesAutoCompleteItem) selectedItem;
        if(hm!=null) {
            itemSelected = new PlacesAutoCompleteItem(hm.get("description"), hm.get("reference"));
        }

            //return hm.get("description");

        return itemSelected.description;
    }


    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // nothing, block the default auto complete behavior
    }

}