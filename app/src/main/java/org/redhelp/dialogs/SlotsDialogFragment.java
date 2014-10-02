package org.redhelp.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.redhelp.app.R;
import org.redhelp.common.AddEventRequest;
import org.redhelp.common.AddEventResponse;
import org.redhelp.common.SlotsCommonFields;
import org.redhelp.common.types.AddEventResponseType;
import org.redhelp.common.types.EventRequestType;
import org.redhelp.session.SessionManager;
import org.redhelp.task.AttendAsyncTask;
import org.redhelp.util.AndroidVersion;
import org.redhelp.util.DateHelper;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by harshis on 7/6/14.
 */
public  class SlotsDialogFragment extends DialogFragment implements AttendAsyncTask.IAttendAsyncTaskHandler{
    public static String BUNDLE_CLASS_OBJ = "bundleClass";

    @Override
    public void handleAttendResult(AddEventResponse addEventResponse) {
        if(addEventResponse.getResponse_types().equals(AddEventResponseType.SUCCESSFUL)) {

        } else if(addEventResponse.getResponse_types().equals(AddEventResponseType.ALREADY_DONE)) {

        } else if(addEventResponse.getResponse_types().equals(AddEventResponseType.MAX_LIMIT_REACHED)) {

        } else {

        }
        dismiss();

    }

    private static class bundle_class implements Serializable {
        public bundle_class(Set<SlotsCommonFields> slots, EventRequestType requestType) {
            this.slots = slots;
            this.requestType = requestType;

        }
        public Set<SlotsCommonFields> slots;
        public EventRequestType requestType;
    }
    /**
     * Create a new instance of MyDialogFragment, providing slots and requestType
     * as argument.
     */
    public static SlotsDialogFragment newInstance(Set<SlotsCommonFields> slots, EventRequestType requestType) {
        SlotsDialogFragment f = new SlotsDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_CLASS_OBJ, new bundle_class(slots, requestType));
        f.setArguments(args);

        return f;
    }



    private LinearLayout ll_slots_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mNum = 1;

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    private void showData(View v,LayoutInflater inflater, Set<SlotsCommonFields> slots, final EventRequestType requestType) {
        if(slots != null) {
            int i = 0;
            LinearLayout slot_linear_layout;
            for(SlotsCommonFields slot : slots) {
                i++;
                ll_slots_content = (LinearLayout) v.findViewById(R.id.ll_slots_dialog_layout);
                View singleSlotContainerView = inflater.inflate(R.layout.row_slot_view_in_dialog, null);
                TextView tv_slot = (TextView) singleSlotContainerView.findViewById(R.id.tv_slot_text_row_slot_view_dialog);
                Button bt_action = (Button) singleSlotContainerView.findViewById(R.id.bt_action_row_slot_view_dialog);

                String slot_string = DateHelper.createSlotString(slot, i);
                tv_slot.setText(slot_string);

                if(requestType.equals(EventRequestType.ATTEND))
                    bt_action.setText("Attend");
                else if(requestType.equals(EventRequestType.VOLUNTEER))
                    bt_action.setText("Volunteer");
                bt_action.setTag(slot.getS_id());

                bt_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleActionButtonOnClick((Long) view.getTag(),requestType );
                    }
                });
                if(!AndroidVersion.isBeforeHoneycomb())
                    singleSlotContainerView.setTop(10);
                ll_slots_content.addView(singleSlotContainerView);

              /*
                slot_linear_layout = new LinearLayout(getActivity());
                slot_linear_layout.setOrientation(LinearLayout.HORIZONTAL);


                TextView tv_slot = new TextView(getActivity());
                tv_slot.setWidth(0);
                tv_slot.setText(slot_string);
                tv_slot.setGravity(Gravity.CENTER);
                ll_slots_content.addView(tv_slot);

                Button bt_attend = new Button(getActivity());
                bt_attend.setBackground(getResources().getDrawable(R.drawable.button_red_white_selector));
                bt_attend.setMinWidth(0);
                bt_attend.setMinHeight(0);
                bt_attend.setPadding(0, UiHelper.convertDpToPixel(getActivity(), 5f),
                        0, UiHelper.convertDpToPixel(getActivity(), 5f));
                bt_attend.setGravity(Gravity.CENTER);
                bt_attend.setText("Attend");

                bt_attend.setWidth(UiHelper.convertDpToPixel(getActivity(), 120f));

                ll_slots_content.addView(bt_attend, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT));*/
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_slots_dialog_layout, container, false);
        ll_slots_content = (LinearLayout) v.findViewById(R.id.ll_slots_dialog_layout);
        if(ll_slots_content == null)
            Log.e("slots_content", "is null :(");

        Bundle data_received = getArguments();
        if(data_received != null) {
            bundle_class bdl_class = (bundle_class) data_received.getSerializable(BUNDLE_CLASS_OBJ);
            if(bdl_class != null) {
                showData(v, inflater, bdl_class.slots, bdl_class.requestType);
                if(bdl_class.requestType.equals(EventRequestType.ATTEND))
                    getDialog().setTitle("Choose Slot (Attending)");
                else
                    getDialog().setTitle("Choose Slot (Volunteer)");
            }
        }
        return v;
    }

    public void handleActionButtonOnClick(Long s_id, EventRequestType eventRequestType) {
        if(s_id == null)
            return;

        Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
        AddEventRequest request = new AddEventRequest();
        request.setB_p_id(b_p_id);
        request.setRequest_type(eventRequestType);
        request.setS_id(s_id);

        AttendAsyncTask attendAsyncTask = new AttendAsyncTask(this);
        attendAsyncTask.execute(request);
    }

}