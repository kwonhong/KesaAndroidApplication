package com.kesa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesa.account.AccountManager;
import com.kesa.account.LoginActivity;
import com.kesa.app.KesaApplication;
import com.kesa.members.MemberFragment;
import com.kesa.user.ProfileActivity;
import com.kesa.user.User;
import com.kesa.user.UserManager;
import com.kesa.util.ImageManager;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

public class MainActivity extends AppCompatActivity {

    @Inject UserManager userManager;
    @Inject AccountManager accountManager;
    @Inject ImageManager imageManager;
    @Bind(R.id.drawer) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;

    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView programTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dependency Injection
        ((KesaApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        // Setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize the view components of navigation header layout.
        View navigationViewHeader = navigationView.getHeaderView(0);
        profileImageView = ButterKnife.findById(navigationViewHeader, R.id.profileImageView);
        nameTextView = ButterKnife.findById(navigationViewHeader, R.id.nameTextView);
        programTextView = ButterKnife.findById(navigationViewHeader, R.id.programTextView);

        // Initializing Drawer Layout and ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to
                // happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we don't want anything to
                // happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Initializing NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.profile:
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;

                    case R.id.members:
                        // Insert the fragment by replacing any existing fragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, new MemberFragment())
                                .commit();
                        return true;

                    case R.id.logout:
                        accountManager.clearPreviousAuthentication();
                        Intent loginIntent =
                            new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        return true;

                    default:
                        return false;
                }
            }
        });

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        // Retrieving the profile information of the user.
        userManager
                .registerActivity(this)
                .findWithUID(accountManager.getCurrentUserUid(), new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        // Complete method is not necessary in this case.
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO(hongil): Handle error cases.
                        // ex) Network Error, Failing to receive information from Firebase API error
                    }

                    @Override
                    public void onNext(User user) {
                        // Updating the profile information after receiving the profile information.
                        if (user != null) {
                            nameTextView.setText(User.getFullName(user));
                            programTextView.setText(user.getProgram());
                            imageManager.loadImage(
                                getApplicationContext(), user.getProfileImage(), profileImageView);
                        }
                    }
                });

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame, new MemberFragment())
            .commit();
    }
}
