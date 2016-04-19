package com.kesa.profile;

import android.app.ProgressDialog;
import android.content.res.Resources;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.base.Optional;
import com.kesa.R;
import com.kesa.util.OnCompleteListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link ProfileManager} implementation with {@link Firebase} API.
 *
 * @author hongil@
 */
public class ProfileManagerFirebaseImpl extends ProfileManager {

    /** A Firebase-key containing all the users' data. */
    private static final String FIREBASE_USER = "users";

    private final Firebase firebase;
    private final Resources resources;
    private Map<String, User> members;

    @Inject
    public ProfileManagerFirebaseImpl(Firebase firebase, Resources resources) {
        this.firebase = firebase.child(FIREBASE_USER);
        this.resources = resources;
        this.members = Collections.synchronizedMap(new HashMap<String, User>());
        initializeMembers();
    }

    @Override
    public void saveOrUpdate(final User user, final OnCompleteListener onCompleteListener) {
        checkNotNull(user);
        checkState(activity != null, "Activity must be registered.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.saving_or_updating_user_dialog_message),
                false,
                false);
        progressDialog.show();

        this.firebase.child(user.getUid()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                // TODO(hongil): Handle error cases

                // Success case.
                progressDialog.dismiss();
                onCompleteListener.onComplete();
            }
        });
    }

    @Override
    public void get(final String uid, final Observer<User> userObserver) {
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_user_dialog_message),
                false,
                false);
        progressDialog.show();

        // Retrieves latest snapshot of profile data.
        firebase
            .child(uid)
            .addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userObserver.onNext(user);
                        userObserver.onCompleted();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        userObserver.onError(firebaseError.toException());
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void getMembers(final Observer<User> userObserver, final Optional<String> query) {
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        // Checking if the data has been cached.
        if (!members.isEmpty()) {
            for (User user : members.values()) {
                if (!query.isPresent() || user.getName().contains(query.get())) {
                    userObserver.onNext(user);
                }
            }

            userObserver.onCompleted();
            return;
        }

        // No data is available. Fetch the data with Firebase API.
        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_user_dialog_message),
                false,
                false);
        progressDialog.show();

        firebase
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!query.isPresent() || user.getName().contains(query.get())) {
                            userObserver.onNext(user);
                        }
                    }

                    progressDialog.dismiss();
                    userObserver.onCompleted();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    progressDialog.dismiss();
                }
            });
    }

    private void initializeMembers() {
        firebase
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    members.put(user.getUid(), user);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // TODO(hongil):
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // TODO(hongil):
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    // TODO(hongil):
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // TODO(hongil):
                }
            });
    }
}
