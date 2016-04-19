package com.kesa.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesa.MainActivity;
import com.kesa.R;
import com.kesa.account.AccountManager;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageEncoder;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * An activity displaying the profile information of the user.
 *
 * @author hongil
 */
public class ProfileActivity extends AppCompatActivity {

    public static final String UID_EXTRA ="UidExtra";

    @Inject ProfileManager profileManager;
    @Inject AccountManager accountManager;
    @Inject ImageEncoder imageEncoder;

    @Bind(R.id.nameTextView) TextView nameTextView;
    @Bind(R.id.programTextView) TextView programTextView;
    @Bind(R.id.mobileTextView) TextView mobileTextView;
    @Bind(R.id.profileImageView) ImageView profileImageView;

    private boolean isDisplayingCurrentUserProfile; // Default to false
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar();

        // Dependency Injections
        ButterKnife.bind(this);
        ((KesaApplication) getApplication()).getComponent().inject(this);

        // Getting the uid of the displaying profile
        this.currentUid = getIntent().getStringExtra(UID_EXTRA);
        if (this.currentUid == null) {
            isDisplayingCurrentUserProfile = true;
            this.currentUid = accountManager.getCurrentUserUid();
        }

        updateProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isDisplayingCurrentUserProfile) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_profile, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
                return true;

            case R.id.action_edit:
                Intent editProfileIntent =
                    new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                return true;

            default:
                return false;
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** Updating the profile after retrieving the profile information of the user. */
    private void updateProfile() {
        profileManager
            .registerActivity(this)
            .get(currentUid, new Observer<User>() {
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
                    nameTextView.setText(user.getName());
                    programTextView.setText(user.getProgram());
                    mobileTextView.setText(user.getMobile());
                    profileImageView.setImageBitmap(
                        imageEncoder.decodeBase64(user.getProfileImage()));
                }
            });
    }
}
