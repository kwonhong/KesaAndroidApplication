package com.kesa;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.kesa.app.KesaApplication;
import com.kesa.profile.ProfileManager;
import com.kesa.profile.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;

/**
 * A fragment displaying/searching members of the {@link KesaApplication}.
 *
 * @author hongil
 */
public class MembersFragment extends Fragment {

    private MaterialSearchView searchView;

    @Inject MembersRecyclerViewAdapter membersRecyclerViewAdapter;
    @Inject ProfileManager profileManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((KesaApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);
        setHasOptionsMenu(true);
        setUpSearchView();
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.membersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(membersRecyclerViewAdapter);
        profileManager.getMembers(new Observer<User>() {
            List<User> users = new ArrayList<>();

            @Override
            public void onCompleted() {
                membersRecyclerViewAdapter.setMembers(users);
                searchView
                    .setSuggestions(FluentIterable
                        .from(users)
                        .transform(new Function<User, String>() {
                            @Override
                            public String apply(User input) {
                                return input.getName();
                            }
                        }).toArray(String.class));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                users.add(user);
            }
        }, Optional.<String>absent());
    }

    private void setUpSearchView() {
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.searchView);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setSuggestionIcon(getResources().getDrawable(R.drawable.ic_profile));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                membersRecyclerViewAdapter.clear();
                profileManager.getMembers(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        membersRecyclerViewAdapter.insertItem(user);
                    }
                }, Optional.of(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_members, menu);

        searchView.setMenuItem(menu.findItem(R.id.action_search));
    }
}
