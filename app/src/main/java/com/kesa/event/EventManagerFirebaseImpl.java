package com.kesa.event;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kesa.R;
import com.kesa.util.NetworkManager;
import com.kesa.util.ResultHandler;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class EventManagerFirebaseImpl extends EventManager {

    private final DatabaseReference databaseReference;
    private final StorageReference imageStorageReference;
    private final Resources resources;

    @Inject
    public EventManagerFirebaseImpl(@Named("events") DatabaseReference databaseReference,
                                    StorageReference imageStorageReference,
                                    Resources resources) {
        this.databaseReference = databaseReference;
        this.imageStorageReference = imageStorageReference;
        this.resources = resources;
    }

    @Override
    public void saveOrUpdate(final Event event, final ResultHandler resultHandler) {
        checkNotNull(event);
        checkState(activity != null, "Activity must be registered.");

        if (!NetworkManager.isNetworkAvailable(activity)) {
            // TODO(hongil): Handle no network case.
            return;
        }

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.saving_or_updating_event_dialog_message),
                false,
                false);
        progressDialog.show();

        // Generate unique identifier if event object doesn't have one yet.
        if (event.getUid() == null) {
            event.setUid(this.databaseReference.push().getKey());
        }

        // Saves the image in Firebase Storage
        imageStorageReference
            .child(event.getUid())
            .putFile(Uri.fromFile(new File(event.getImage())))
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri imageDownloadUri = taskSnapshot.getDownloadUrl();
                    if (imageDownloadUri != null) {
                        event.setImage(imageDownloadUri.toString());
                        saveOrUpdateHelper(event, resultHandler, progressDialog);
                    } else {
                        //TODO(hongilk): Handle error case.
                        progressDialog.dismiss();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.show();
                    resultHandler.onError(e);
                }
            });
    }

    private void saveOrUpdateHelper(final Event event,
                                    final ResultHandler resultHandler,
                                    final ProgressDialog progressDialog) {
        databaseReference
            .child(event.getUid())
            .setValue(event)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        resultHandler.onComplete();
                    } else {
                        resultHandler.onError(task.getException());
                    }

                    // Stopping the progress dialog.
                    progressDialog.dismiss();
                }
            });
    }

    @Override
    public void findAll(final Observer<Event> eventObserver) {
        checkNotNull(eventObserver);
        checkState(activity != null, "Activity must be registered.");

        if (!NetworkManager.isNetworkAvailable(activity)) {
            // TODO(hongil): Handle no network case.
            return;
        }

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_event_dialog_message),
                false,
                false);
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Getting the list of events
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    eventObserver.onNext(event);
                }

                eventObserver.onCompleted();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventObserver.onError(databaseError.toException());
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void findById(final String uid, final Observer<Event> eventObserver) {
        checkNotNull(eventObserver);
        checkNotNull(uid);
        checkState(activity != null, "Activity must be registered.");

        if (!NetworkManager.isNetworkAvailable(activity)) {
            // TODO(hongil): Handle no network case.
            return;
        }

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_user_dialog_message),
                false,
                false);
        progressDialog.show();

        databaseReference
            .child(uid)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Getting the list of events
                    Event event = dataSnapshot.getValue(Event.class);
                    eventObserver.onNext(event);
                    eventObserver.onCompleted();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    eventObserver.onError(databaseError.toException());
                    progressDialog.dismiss();
                }
            });
    }
}
