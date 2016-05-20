package com.kesa.members;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.user.User;
import com.kesa.user.UserManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observer;

public class MemberFragment extends Fragment {
    @Inject MembersRecyclerViewAdapter membersRecyclerViewAdapter;
    @Inject UserManager userManager;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dependency Injection
        ((KesaApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Allowing the fragment to have its own actionBar menus.

        View view = inflater.inflate(R.layout.fragment_member, container, false);
        swipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView();
            }
        });

        recyclerView = ButterKnife.findById(view, R.id.memberRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(membersRecyclerViewAdapter);
        updateRecyclerView();

        FloatingActionButton floatingActionButton = ButterKnife.findById(view, R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        return view;
    }

    public void updateRecyclerView() {
        membersRecyclerViewAdapter.clear();
        userManager
            .registerActivity(getActivity())
            .findAll(new Observer<User>() {
                @Override
                public void onCompleted() {
                    membersRecyclerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(User user) {
                    membersRecyclerViewAdapter.insertItem(user);
                }
            });
    }
}
