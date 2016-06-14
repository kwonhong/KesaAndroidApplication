package com.kesa.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.util.ImageManager;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class EventCreateActivity extends AppCompatActivity {
    @Inject ImageManager imageManager;
    @Inject EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        // Dependency Injections
        ButterKnife.bind(this);
        ((KesaApplication) getApplication()).getComponent().inject(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageManager.PICTURE_SELECTION_REQUEST_CODE) {
            // Processing/Saving the selected image.
            String selectedImagePath = imageManager.getSelectedImagePath(this, data.getData());
            // TODO(hongil): Handle the selected image
        }
    }
}
