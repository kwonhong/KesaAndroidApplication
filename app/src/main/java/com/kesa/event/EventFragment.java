package com.kesa.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observer;


public class EventFragment extends Fragment {
    @Inject EventManager eventManager;
    @Inject EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    @Inject ImageManager imageManager;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dependency Injection
        ((KesaApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Allowing the fragment to have its own actionBar menus.

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = ButterKnife.findById(view, R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(eventsRecyclerViewAdapter);
        updateRecyclerView();
        return view;
    }

    public void updateRecyclerView() {
        eventsRecyclerViewAdapter.clear();
        eventManager
            .registerActivity(getActivity())
            .findAll(new Observer<Event>() {
                @Override
                public void onCompleted() {
                    eventsRecyclerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Event event) {
                    eventsRecyclerViewAdapter.insertItem(event);
                }
            });
    }
}
