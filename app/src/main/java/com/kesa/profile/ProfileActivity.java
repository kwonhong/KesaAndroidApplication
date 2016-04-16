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
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageEncoder;

import javax.inject.Inject;

import rx.Observer;

/**
 * An activity displaying the profile information of the user.
 *
 * @author hongil
 */
public class ProfileActivity extends AppCompatActivity {

    @Inject ProfileManager profileManager;
    @Inject ImageEncoder imageEncoder;

    private TextView nameTextView;
    private TextView programTextView;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((KesaApplication) getApplication()).getComponent().inject(this); // Dependency Injection
        setContentView(R.layout.activity_profile);
        setUpToolbar();

        // Finding view components from the layout.
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        programTextView = (TextView) findViewById(R.id.programTextView);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        updateProfile();
    }

    /** Updating the profile after retrieving the profile information of the user. */
    private void updateProfile() {
        profileManager
                .registerActivity(this)
                .get("1", new Observer<User>() {
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
                        profileImageView.setImageBitmap(imageEncoder.decodeBase64(user
                                .getProfileImage()));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfile();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
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
}
