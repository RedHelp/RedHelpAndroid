package org.redhelp.app;

/**
 * Created by harshis on 7/21/14.
 */import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;


public abstract class RESTfulListActivity extends ListActivity {

    private Button mRefreshButton;
    private ProgressBar mRefreshProgressIndicator;
    private int mContentResId;
    private boolean mRefreshable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(mContentResId);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_restfullist_actionbar_layout);
        super.onCreate(savedInstanceState);

        ViewGroup v = (ViewGroup) findViewById(R.id.action_bar);
        v.setPadding(0, 0, 0, 0);

        mRefreshButton = (Button) findViewById(R.id.refresh_button);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mRefreshProgressIndicator.setVisibility(View.VISIBLE);
                mRefreshButton.setVisibility(View.INVISIBLE);
                refresh();
            }
        });

        mRefreshProgressIndicator = (ProgressBar) findViewById(R.id.action_bar_progress);
        mRefreshProgressIndicator.setIndeterminate(true);
        mRefreshProgressIndicator.setVisibility(View.GONE);

        if (!mRefreshable) {
            mRefreshButton.setVisibility(View.GONE);
            mRefreshProgressIndicator.setVisibility(View.GONE);
        }
    }

    protected abstract void refresh();

    protected void setContentResId(int id) {
        mContentResId = id;
    }

    protected void setRefreshable(boolean refreshable) {
        mRefreshable = refreshable;
    }

    protected void setRefreshing(boolean refreshing) {

        mRefreshProgressIndicator.setVisibility(refreshing ? View.VISIBLE
                : View.INVISIBLE);
        mRefreshButton
                .setVisibility(refreshing ? View.INVISIBLE : View.VISIBLE);

    }

}