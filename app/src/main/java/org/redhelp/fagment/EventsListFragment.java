package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

import org.redhelp.adapter.EventListViewAdapter;
import org.redhelp.adapter.items.EventItem;
import org.redhelp.app.R;
import org.redhelp.common.EventSearchResponse;
import org.redhelp.common.SearchResponse;
import org.redhelp.util.DateHelper;

import java.util.Set;

/**
 * Created by harshis on 7/12/14.
 */
public class EventsListFragment extends android.support.v4.app.ListFragment {
    public  static final String BUNDLE_EVENTS = "bundle_events";

    public static EventsListFragment createEventsListFragmentInstance(SearchResponse searchResponse) {
        EventsListFragment eventsListFragment = new EventsListFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_EVENTS, searchResponse);
        eventsListFragment.setArguments(content);
        return eventsListFragment;
    }

    private EventListViewAdapter listViewAdapter;


    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        Set<EventSearchResponse> eventSearchResponse = null;
        if(data_passed != null){
            try {
                SearchResponse searchResponse = (SearchResponse) data_passed.getSerializable(BUNDLE_EVENTS);
                eventSearchResponse = searchResponse.getSet_events();
            }catch (Exception e){
                return;
            }
        }

        if(eventSearchResponse != null) {
            listViewAdapter = createAdapter(eventSearchResponse);
            setListAdapter(listViewAdapter);
            ListView listView = getListView();
            listView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        }
    }

    private EventListViewAdapter createAdapter(Set<EventSearchResponse> eventSearchResponse){
        EventListViewAdapter adapter = new EventListViewAdapter(getActivity());
        for(EventSearchResponse event : eventSearchResponse) {

            String scheduled_date = "-";
            if(event.getScheduled_date_time() != null)
                scheduled_date = DateHelper.getDateInViewableFormat(event.getScheduled_date_time());

            String event_venue = "-";
            if(event.getVenue() != null)
                event_venue = event.getVenue();

            EventItem eventItem = new EventItem(event.getE_id(), "2.9", event.getTitle(), event_venue, scheduled_date);
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
