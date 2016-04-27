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

    /** A key to retrieve the user's uid from an {@link Intent}. */
    public static final String USER_UID ="UserUID";

    @Inject UserManager profileManager;
    @Inject AccountManager accountManager;
    @Inject ImageEncoder imageEncoder;
    @Bind(R.id.nameTextView) TextView nameTextView;
    @Bind(R.id.programTextView) TextView programTextView;
    @Bind(R.id.mobileTextView) TextView mobileTextView;
    @Bind(R.id.admissionYearTextView) TextView admissionYearTextView;
    @Bind(R.id.profileImageView) ImageView profileImageView;

    private boolean isDisplayingCurrentUserProfile; // Default to false
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar();

        // Dependency Injections
        ButterKnife.bind(this);
        ((KesaApplication) getApplication()).getComponent().inject(this);

        // Getting the uid of the displaying profile
        this.userUid = getIntent().getStringExtra(USER_UID);
        if (this.userUid == null) {
            isDisplayingCurrentUserProfile = true;
            this.userUid = accountManager.getCurrentUserUid();
        }

        getProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileData();
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
    private void getProfileData() {
        profileManager
            .registerActivity(this)
            .get(userUid, new Observer<User>() {
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
