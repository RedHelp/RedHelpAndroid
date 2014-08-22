package org.redhelp.fagment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by harshis on 8/4/14.
 */
public class DatePickerFragment extends DialogFragment {
    public static int _month = 0;
    public static int _day = 0;
    public static int _year = 0;
    private static int _buttonId = 0;

    public DatePickerFragment(){
    }

    public static final DatePickerFragment newInstance(int buttonId, int year, int month, int day) {
        DatePickerFragment f = new DatePickerFragment();
        _buttonId = buttonId;
        _year = year;
        _month = month;
        _day = day;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),  mDateSetListener, _year, _month, _day);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //update the control whose press opened the DatePpickerFragment
            Button _button = (Button)getActivity().findViewById (_buttonId);
            _year = year;
            _month = month;
            _day = day;
            if(_button != null) {
                _button.setText(month+1 + "/" + day + "/" + year);
            }
        }
    };
}