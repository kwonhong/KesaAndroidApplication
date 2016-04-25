package com.kesa.members;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.kesa.MembersRecyclerViewAdapter;
import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.profile.User;
import com.kesa.profile.UserManager;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observer;

/**
 * An activity that allows to search the registered members of the {@link KesaApplication}.
 *
 * @author hongil
 */
public class SearchActivity extends AppCompatActivity {

    @Inject MembersRecyclerViewAdapter membersRecyclerViewAdapter;
    @Inject UserManager profileManager;

    private RecyclerView recyclerView;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Dependency Injection
        ((KesaApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        setUpToolbar();
        setUpSearchView();
        setUpRecyclerView();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpSearchView() {
        // TODO(hongil): Changes the suggestionIcon.
        searchView = (MaterialSearchView) findViewById(R.id.searchView);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.showSearch();
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

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.membersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(membersRecyclerViewAdapter);
        profileManager.getMembers(new Observer<User>() {
            List<User> users = new ArrayList<>();

            @Override
            public void onCompleted() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_members, menu);
        searchView.setMenuItem(menu.findItem(R.id.action_search));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return false;
        }
    }
}
