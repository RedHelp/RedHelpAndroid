package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.redhelp.app.R;

/**
 * Created by harshis on 7/13/14.
 */
public class ErrorHandlerFragment extends Fragment {
    public static final String BUNDLE_ERROR_HANDLER = "error_handler_data";

    public static ErrorHandlerFragment createErrorHandlerFragmentInstance(String msg, IErrorHandlerFragment iErrorHandlerFragment) {
        ErrorHandlerFragment errorHandlerFragment = new ErrorHandlerFragment();
        errorHandlerFragment.setiErrorHandlerFragment(iErrorHandlerFragment);
        Bundle data = new Bundle();
        data.putString(BUNDLE_ERROR_HANDLER, msg);
        errorHandlerFragment.setArguments(data);
        return  errorHandlerFragment;
    }

    public void setiErrorHandlerFragment(IErrorHandlerFragment iErrorHandlerFragment) {
        this.iErrorHandlerFragment = iErrorHandlerFragment;
    }

    public interface IErrorHandlerFragment {
        public void onRefreshButtonClickHandler();
    }

    private IErrorHandlerFragment iErrorHandlerFragment;

    private Button bt_refresh;
    private TextView tv_msg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error_refresh_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
    }

    private void initializeViews() {
        bt_refresh = (Button) getActivity().findViewById(R.id.bt_refresh_error_refresh_layout);
        tv_msg = (TextView) getActivity().findViewById(R.id.tv_msg_error_refresh_layout);

        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, homeFragment);
                transaction.commit();
            }
        });
    }
}
