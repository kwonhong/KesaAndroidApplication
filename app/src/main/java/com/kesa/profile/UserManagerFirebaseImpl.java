package com.kesa.profile;

import android.app.ProgressDialog;
import android.content.res.Resources;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.kesa.R;
import com.kesa.util.ResultHandler;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link UserManager} implementation with {@link Firebase} API.
 *
 * @author hongil@
 */
public class UserManagerFirebaseImpl extends UserManager {

    /** A Firebase-key containing all the users' data. */
    private static final String FIREBASE_USER = "users";

    private final Firebase firebase;
    private final Resources resources;

    @Inject
    public UserManagerFirebaseImpl(Firebase firebase, Resources resources) {
        this.firebase = firebase.child(FIREBASE_USER);
        this.resources = resources;
        initializeMembers();
    }

    @Override
    public void saveOrUpdate(final User user, final ResultHandler resultHandler) {
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
                if (firebaseError == null) {
                    // Success case.
                    resultHandler.onComplete();
                    user.save(); // Saving in local database.
                } else {
                    // Error case.
                    resultHandler.onError(firebaseError.toException());
                }

                // Stopping the progress dialog.
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void findWithUID(final String uid, final Observer<User> userObserver) {
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        // Attempting to retrieve from the local database
        List<User> users = User.find(User.class, "uid = ?", uid);
        if (users != null && !users.isEmpty()) {
            userObserver.onNext(users.iterator().next());
            userObserver.onCompleted();
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
    public void findWithName(final String name, final Observer<User> userObserver) {
        checkNotNull(name);
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        // Attempting to retrieve from the local database
        List<User> users = User.findWithQuery(
            User.class, "SELECT * FROM user WHERE name LIKE %?% COLLATE NOCASE", name);
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userObserver.onNext(user);
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
                        // Comparing after changing to all lowercase - Case-Insensitive
                        if (user.getName().toLowerCase().contains(name.toLowerCase())) {
                            userObserver.onNext(user);
                        }
                    }

                    progressDialog.dismiss();
                    userObserver.onCompleted();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    progressDialog.dismiss();
                    userObserver.onError(firebaseError.toException());
                }
            });
    }

    @Override
    public void findWithRole(int roleId, final Observer<User> userObserver) {
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        // Attempting to retrieve from the local database
        List<User> users = User.findWithQuery(
            User.class, "SELECT * FROM user WHERE roleId = " + roleId);
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userObserver.onNext(user);
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
            .equalTo(roleId, "roleId")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        userObserver.onNext(user);
                    }

                    progressDialog.dismiss();
                    userObserver.onCompleted();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    progressDialog.dismiss();
                    userObserver.onError(firebaseError.toException());
                }
            });
    }

    private void initializeMembers() {
        firebase
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    List<User> users = User.find(User.class, "uid = ?", user.getUid());
                    if (users.isEmpty()) {
                        user.save();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    List<User> users = User.find(User.class, "uid = ?", user.getUid());
                    if (users == null || users.size() != 1) {
                        // TODO(hongil): Handle error later... & exit
                    }

                    // Updating the change
                    long currentUserId = users.iterator().next().getId();
                    user.setId(currentUserId);
                    user.save();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // TODO(hongil):
                }
            });
    }
}
