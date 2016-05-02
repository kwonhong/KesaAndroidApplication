package com.kesa.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kesa.MainActivity;
import com.kesa.R;
import com.kesa.account.AccountManager;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageManager;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

import static com.kesa.user.User.getFullName;

/**
 * An activity displaying the profile information of the user.
 *
 * @author hongil
 */
public class ProfileActivity extends AppCompatActivity {

    /** A key to retrieve the user's uid from an {@link Intent}. */
    public static final String USER_UID ="UserUID";

    /** A key to retrieve whether the activity has been started from {@link EditProfileActivity}. */
    public static final String FROM_EDIT_PROFILE = "FromEditProfile";

    @Inject UserManager userManager;
    @Inject AccountManager accountManager;
    @Inject ImageManager imageManager;
    @Bind(R.id.nameTextView) TextView nameTextView;
    @Bind(R.id.programTextView) TextView programTextView;
    @Bind(R.id.mobileTextView) TextView mobileTextView;
    @Bind(R.id.emailTextView) TextView emailTextView;
    @Bind(R.id.admissionYearTextView) TextView admissionYearTextView;
    @Bind(R.id.contactNotAvailableTextView) TextView contactNotAvailableTextView;
    @Bind(R.id.profileImageView) ImageView profileImageView;
    @Bind(R.id.emailLayout) RelativeLayout emailRelativeLayout;
    @Bind(R.id.mobileLayout) RelativeLayout mobileRelativeLayout;

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
        if (this.userUid == null || this.userUid.equals(accountManager.getCurrentUserUid())) {
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
        // Displaying the editButton when displaying the profile of the current user.
        if (isDisplayingCurrentUserProfile) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_profile, menu);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        redirectToMainActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                redirectToMainActivity();
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

    private void redirectToMainActivity() {
        boolean isFirstTimeProfile = getIntent().getBooleanExtra(FROM_EDIT_PROFILE, false);
        if (isFirstTimeProfile) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
        }

        finish();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** Updating the profile after retrieving the profile information of the user. */
    private void getProfileData() {
        userManager
            .registerActivity(this)
            .findWithUID(userUid, new Observer<User>() {
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
                    nameTextView.setText(getFullName(user));
                    programTextView.setText(user.getProgram());
                    admissionYearTextView.setText(
                        User.getAdmissionYearInString(user.getAdmissionYear()));
                    imageManager.loadImage(
                        getApplicationContext(), user.getProfileImage(), profileImageView);

                    // Displays the contact information for the following cases
                    // 1. Current user is an executive member.
                    // 2. User publicized the contact information.
                    // 3. Displaying the profile page of the current user.
                    // TODO(hongil): Check if the currentUser is executive
                    if (user.isContactPublic() || isDisplayingCurrentUserProfile ||
                        userManager.isExecutiveMember(accountManager.getCurrentUserUid())) {
                        mobileTextView.setText(user.getMobile());
                        emailTextView.setText(user.getEmail());
                    } else {
                        emailRelativeLayout.setVisibility(View.INVISIBLE);
                        mobileRelativeLayout.setVisibility(View.INVISIBLE);
                        contactNotAvailableTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
    }
}
