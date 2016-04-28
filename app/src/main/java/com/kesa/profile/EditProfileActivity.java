package com.kesa.profile;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kesa.R;
import com.kesa.account.AccountManager;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageEncoder;
import com.kesa.util.ResultHandler;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

/**
 * An activity responsible for editing the profile information of the user.
 *
 * @author hongil
 */
public class EditProfileActivity extends AppCompatActivity {

    /**
     * A key to retrieve the user's name from an {@link Intent}.
     */
    public static final String USER_NAME = "UserName";

    /**
     * A key to retrieve the user's email from an {@link Intent}.
     */
    public static final String USER_EMAIL = "UserEmail";

    /**
     * Used to inform that the activity has been started from picture selection.
     */
    private static final int PICTURE_SELECTION_REQUEST_CODE = 1000;

    @Inject UserManager userManager;
    @Inject AccountManager accountManager;
    @Inject ImageEncoder imageEncoder;
    @Bind(R.id.profileImageView) ImageView profileImageView;
    @Bind(R.id.nameEditText) EditText nameEditText;
    @Bind(R.id.programEditText) EditText programEditText;
    @Bind(R.id.mobileEditText) EditText mobileEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.admissionYearEditText) EditText admissionYearEditText;
    @Bind(R.id.changePictureBtn) Button changePictureButton;
    @Bind(R.id.publicizeSwitch) SwitchCompat publicizeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUpToolbar();

        // Dependency Injections
        ButterKnife.bind(this);
        ((KesaApplication) getApplication()).getComponent().inject(this);

        // Intent contains userName extra data iff the activity has been started from
        // SignUpActivity.
        String userName = getIntent().getStringExtra(USER_NAME);
        String userEmail = getIntent().getStringExtra(USER_EMAIL);
        if (userName != null && userEmail != null) {
            nameEditText.setText(userName);
            emailEditText.setText(userEmail);
            return;
        }

        // Otherwise, retrieve the profile information of the current user.
        retrieveProfile();
    }

    @OnClick(R.id.changePictureBtn)
    void onChangePictureButtonClickEvent() {
        // Prompting to select a profile image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
            Intent.createChooser(intent, null),
            PICTURE_SELECTION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICTURE_SELECTION_REQUEST_CODE) {
            profileImageView.setImageBitmap(getSelectedBitmap(data));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Going back to the previous activity.
                finish();
                return true;

            case R.id.action_done:
                // Validating the inputs from the user
                User currentUser = getCurrentUserData();
                if (!validate(currentUser)) {
                    return false;
                }

                // Updating the profile information
                updateProfile(currentUser);
                return true;

            default:
                return false;
        }
    }

    private void updateProfile(final User user) {
        userManager
            .registerActivity(this)
            .saveOrUpdate(user, new ResultHandler() {
                @Override
                public void onComplete() {
                    // Redirecting to ProfileActivity on complete.
                    Intent profileIntent =
                        new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profileIntent);
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    // Giving a user another attempt to update.
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Unexpected error while saving the information...",
                        Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateProfile(user);
                            }
                        }).show();
                }
            });
    }

    private void retrieveProfile() {
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
                    if (user != null) {
                        fillProfileData(user); // Pre-filling the profile information.
                    }
                }
            });
    }

    private void fillProfileData(User user) {
        nameEditText.setText(user.getName());
        programEditText.setText(user.getProgram());
        mobileEditText.setText(user.getMobile());
        admissionYearEditText.setText(Integer.toString(user.getAdmissionYear()));
        emailEditText.setText(user.getEmail());
        profileImageView.setImageBitmap(
            imageEncoder.decodeBase64(user.getProfileImage()));
        publicizeSwitch.setChecked(user.isContactPublic());
    }

    private boolean validate(User currentUser) {
        // Checking the format of the name
        String name = currentUser.getName();
        if (name.isEmpty()) {
            nameEditText.setError("Enter a valid name.");
            return false;
        }

        // Checking the format of the program
        String program = currentUser.getProgram();
        if (program.isEmpty()) {
            programEditText.setError("Enter a valid program.");
            return false;
        }

        // Checking the format of the year
        int admissionYear = currentUser.getAdmissionYear();
        if (admissionYear == -1) {
            admissionYearEditText.setError("Enter a valid admission year.");
            return false;
        }

        // Checking the format of the mobile
        String mobile = currentUser.getMobile();
        if (mobile.isEmpty() || !TextUtils.isDigitsOnly(mobile)) {
            mobileEditText.setError("Enter a valid mobile.");
            return false;
        }

        // Checking the format of the email
        String email = currentUser.getEmail();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email.");
            return false;
        }

        return true;
    }

    /**
     * Retrieves selected profile {@link Bitmap} from the given {@code data}.
     */
    private Bitmap getSelectedBitmap(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        CursorLoader cursorLoader =
            new CursorLoader(this, selectedImageUri, projection, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        return BitmapFactory.decodeFile(selectedImagePath, null);
    }

    private User getCurrentUserData() {
        String name = nameEditText.getText().toString();
        String program = programEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String profileImage = imageEncoder.encodeToBase64(
            ((BitmapDrawable) profileImageView.getDrawable()).getBitmap());
        boolean isContactPublic = publicizeSwitch.isChecked();

        String admissionYearString = (admissionYearEditText.getText().toString());
        int admissionYear = (TextUtils.isDigitsOnly(admissionYearString)) ?
            Integer.parseInt(admissionYearString) : -1;

        return new User(
            accountManager.getCurrentUserUid(),
            name,
            program,
            mobile,
            profileImage,
            email,
            admissionYear,
            Role.MEMBER.getRoleId(),
            isContactPublic);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
