package org.redhelp.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.redhelp.app.R;
import org.redhelp.common.GetBloodRequestResponse;
import org.redhelp.common.UpdateBloodRequest;
import org.redhelp.common.UpdateBloodRequestResponse;
import org.redhelp.common.UserProfileCommonFields;
import org.redhelp.common.types.UpdateBloodRequestState;
import org.redhelp.task.UpdateBloodRequestTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by harshis on 9/23/14.
 */
public class UpdateBRStateDialogFragment extends android.support.v4.app.DialogFragment
                        implements UpdateBloodRequestTask.IUpdateBloodRequestTaskListener{

    @Override
    public void handleUpdateBloodRequestError() {
        getDialog().dismiss();
    }

    @Override
    public void handleUpdateBloodRequestResponse(UpdateBloodRequestResponse response) {
        Log.i("UPDATE", "response:"+response.toString());
        Intent intent = new Intent();
        intent.putExtra("b_r_id", cached_b_r_id);
        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
        getDialog().dismiss();
    }

    private static class UpdateBloodRequestData implements Serializable {

        public Long b_r_id;
        public UpdateBloodRequestState state;
        public HashMap<Long, String> donorsHashMap;

    }

    private static final String BUNDLE_CLASS_OBJ = "bundle_data";

    public static UpdateBRStateDialogFragment getInstance(GetBloodRequestResponse bloodRequest) {

        UpdateBloodRequestData updateBloodRequestData = new UpdateBloodRequestData();
        updateBloodRequestData.b_r_id  = bloodRequest.getB_r_id();
        updateBloodRequestData.state = bloodRequest.getBloodRequestState();

        HashMap<Long, String> donorsHashMap = new HashMap<Long, String>();
        Set<UserProfileCommonFields> bloodRequestReceivers = bloodRequest.getBlood_request_receivers_profiles();
        if(bloodRequestReceivers != null) {
            for(UserProfileCommonFields receiver : bloodRequestReceivers) {
                donorsHashMap.put(receiver.getB_p_id(), receiver.getName());
            }
            updateBloodRequestData.donorsHashMap = donorsHashMap;
        }
        UpdateBRStateDialogFragment f = new UpdateBRStateDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_CLASS_OBJ, updateBloodRequestData);
        f.setArguments(args);

        return f;
    }


    RadioGroup rgBloodRequestState;
    RadioButton rbInactivate;
    RadioButton rbExtend;
    RadioButton rbCompleted;
    private LinearLayout llCheckboxes;
    private Button btDoneUpdating;
    private Button btCancel;

    private final int RADIO_INACTIVATE = R.id.rb_state_inactivate_update_blood_request_layout;
    private final int RADIO_EXTEND = R.id.rb_extend_update_blood_request_layout;
    private final int RADIO_COMPLETED = R.id.rb_completed_update_blood_request_layout;

    private Long cached_b_r_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_blood_request_state_dailog, container, false);
        rgBloodRequestState = (RadioGroup) v.findViewById(R.id.rg_state_update_blood_request_layout);
        rbInactivate = (RadioButton) v.findViewById(R.id.rb_state_inactivate_update_blood_request_layout);
        rbExtend = (RadioButton) v.findViewById(R.id.rb_extend_update_blood_request_layout);
        rbCompleted = (RadioButton) v.findViewById(R.id.rb_completed_update_blood_request_layout);
        llCheckboxes = (LinearLayout) v.findViewById(R.id.ll_checkboxes_update_state);
        btDoneUpdating = (Button) v.findViewById(R.id.bt_done_update_request_state);
        btDoneUpdating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDoneButton();
            }
        });
        btCancel = (Button) v.findViewById(R.id.bt_cancel_update_request_state);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        getDialog().setTitle("Tell us what happened with this request");

        Bundle data_received = getArguments();
        if(data_received != null) {
            UpdateBloodRequestData updateData = (UpdateBloodRequestData) data_received.getSerializable(BUNDLE_CLASS_OBJ);
            if(updateData != null) {
               cached_b_r_id = updateData.b_r_id;
               HashMap<Long, String> receivers = updateData.donorsHashMap;
               UpdateBloodRequestState state = updateData.state;

               if(state != null && state.equals(UpdateBloodRequestState.EXTENDED)
                       || state.equals(UpdateBloodRequestState.INACTIVE_EXTENDED_EXPIRED)) {
                   rbExtend.setVisibility(View.GONE);
               }

                if(receivers != null) {
                    for(Map.Entry<Long, String> entry : receivers.entrySet()) {
                        Long b_p_id = entry.getKey();
                        String name = entry.getValue();


                        CheckBox checkBox = new CheckBox(getActivity());
                        checkBox.setText(name);
                        checkBox.setTag(b_p_id);
                        llCheckboxes.addView(checkBox);
                    }
                }
            }
        }


        rgBloodRequestState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checked_seach_type = radioGroup.getCheckedRadioButtonId();

                if(checked_seach_type == RADIO_COMPLETED) {
                    llCheckboxes.setVisibility(View.VISIBLE);
                } else
                    llCheckboxes.setVisibility(View.GONE);

            }
        });
        return v;
    }

    private void handleDoneButton() {
        UpdateBloodRequest request = new UpdateBloodRequest();
        UpdateBloodRequestState bloodRequestState = null;
        int checked_seach_type = rgBloodRequestState.getCheckedRadioButtonId();
        if(checked_seach_type == RADIO_INACTIVATE)
            bloodRequestState = UpdateBloodRequestState.INACTIVE_MANUALLY;
        else if(checked_seach_type == RADIO_EXTEND)
            bloodRequestState = UpdateBloodRequestState.EXTENDED;
        else if(checked_seach_type == RADIO_COMPLETED) {
            bloodRequestState = UpdateBloodRequestState.INACTIVE_MANUALLY;
            ArrayList<View> checkBoxList = getAllChildren(llCheckboxes);
            List<Long> verified_b_p_ids = new LinkedList<Long>();
            for(View checkbox : checkBoxList) {
                CheckBox ch;
                if(checkbox instanceof CheckBox)
                    ch = (CheckBox)checkbox;
                else
                    continue;

                if(ch.isChecked() == false)
                    continue;
                Long b_p_id = (Long) checkbox.getTag();
                if(b_p_id != null)
                    verified_b_p_ids.add(b_p_id);
            }
            request.setVerified_b_p_ids(verified_b_p_ids);
        }
        request.setUpdateState(bloodRequestState);
        request.setB_r_id(cached_b_r_id);


        Log.e("UPDATE_REQUESt", "request :"+request.toString());

        UpdateBloodRequestTask updateBloodRequestTask = new UpdateBloodRequestTask(getActivity(), this);
        updateBloodRequestTask.execute(request);

    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }
}
