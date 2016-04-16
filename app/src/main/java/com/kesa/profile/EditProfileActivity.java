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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageEncoder;

import javax.inject.Inject;

import rx.Observer;

/**
 * An activity responsible for editing the profile information of the user.
 *
 * @author hongil
 */
public class EditProfileActivity extends AppCompatActivity {

    /** Used to inform that the activity has been started from picture selection. */
    private static final int PICTURE_SELECTION_REQUEST_CODE = 1000;

    @Inject ProfileManager profileManager;
    @Inject ImageEncoder imageEncoder;

    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText programEditText;
    private EditText mobileEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((KesaApplication) getApplication()).getComponent().inject(this); // Dependency Injection
        setContentView(R.layout.activity_edit_profile);
        setUpToolbar();

        Button changePictureBtn = (Button) findViewById(R.id.changePictureBtn);
        changePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prompting to select a profile image from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                    Intent.createChooser(intent, null),
                    PICTURE_SELECTION_REQUEST_CODE);
            }
        });

        // Finding view components from the layout.
        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        programEditText = (EditText) findViewById(R.id.programEditText);
        mobileEditText = (EditText) findViewById(R.id.mobileEditText);

        // Retrieving the profile information of the user.
        profileManager
            .registerActivity(this)
            .get("1", new Observer<User>() {
                // TODO(hongil): Refactor an observer class for retrieving profile info.
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
                    // Pre-filling the profile information.
                    nameEditText.setText(user.getName());
                    programEditText.setText(user.getProgram());
                    mobileEditText.setText(user.getMobile());
                    profileImageView.setImageBitmap(imageEncoder.decodeBase64(user
                        .getProfileImage()));
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICTURE_SELECTION_REQUEST_CODE) {
            profileImageView.setImageBitmap(getSelectedBitmap(data));
        }
    }

    /** Retrieves selected profile {@link Bitmap} from the given {@code data}. */
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

    private User getUser() {
        // TODO(hongil): Validate input!
        String name = nameEditText.getText().toString();
        String program = programEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String profileImage = imageEncoder.encodeToBase64(
            ((BitmapDrawable) profileImageView.getDrawable()).getBitmap());
        return new User("1", name, program, mobile, profileImage);
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
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                profileManager
                    .registerActivity(this)
                    .saveOrUpdate(getUser());
                return true;

            default:
                return false;
        }
    }
}
