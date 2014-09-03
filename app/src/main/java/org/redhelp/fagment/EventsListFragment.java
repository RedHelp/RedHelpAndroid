package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

import org.redhelp.adapter.EventListViewAdapter;
import org.redhelp.adapter.items.EventItem;
import org.redhelp.app.R;
import org.redhelp.data.EventDataWrapper;
import org.redhelp.data.EventListData;
import org.redhelp.util.DateHelper;

import java.util.Set;

/**
 * Created by harshis on 7/12/14.
 */
public class EventsListFragment extends android.support.v4.app.ListFragment {
    public  static final String BUNDLE_EVENTS = "bundle_events";

    public static EventsListFragment createEventsListFragmentInstance(EventDataWrapper eventDataWrapper) {
        EventsListFragment eventsListFragment = new EventsListFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_EVENTS, eventDataWrapper);
        eventsListFragment.setArguments(content);
        return eventsListFragment;
    }

    private EventListViewAdapter listViewAdapter;


    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        Set<EventListData> eventListDataSet = null;
        if(data_passed != null){
            try {
                EventDataWrapper eventDataWrapper = (EventDataWrapper) data_passed.getSerializable(BUNDLE_EVENTS);
                eventListDataSet = eventDataWrapper.eventListDataSet;
            }catch (Exception e){
                return;
            }
        }

        if(eventListDataSet != null) {
            listViewAdapter = createAdapter(eventListDataSet);
            setListAdapter(listViewAdapter);
            ListView listView = getListView();
            listView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        }
    }

    private EventListViewAdapter createAdapter(Set<EventListData> eventListDataSet){
        EventListViewAdapter adapter = new EventListViewAdapter(getActivity());
        for(EventListData event : eventListDataSet) {
            Long e_id;
            if(event.e_id == null)
                continue;
            else
                e_id = event.e_id;
            String title;
            if(event.title == null)
                continue;
            else
                title = event.title;

            String scheduled_date = "-";
            if(event.scheduled_date != null)
                scheduled_date = DateHelper.getDateInViewableFormat(event.scheduled_date);

            String event_venue = "-";
            if(event.venue != null)
                event_venue = event.venue;

            String distance = "-";
            if(event.distance != null)
                distance = event.distance;

            EventItem eventItem = new EventItem(e_id, distance, title, event_venue, scheduled_date);
            adapter.add(eventItem);
        }

        return adapter;

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        EventItem eventItem = null;
        try {
            eventItem = (EventItem) getListAdapter().getItem(position);
        }catch (Exception e){

        }
        if(eventItem != null){
            ViewEventFragment viewEventFragment =  ViewEventFragment.createViewEventFragmentInstance(eventItem.e_id);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame_main_screen, viewEventFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
