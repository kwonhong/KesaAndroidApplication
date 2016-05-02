package com.kesa.user;

import android.app.ProgressDialog;
import android.content.res.Resources;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.kesa.R;
import com.kesa.util.ResultHandler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link UserManager} implementation with {@link Firebase} API.
 *
 * @author hongil@
 */
public class UserManagerFirebaseImpl extends UserManager {

    private final Firebase firebase;
    private final Resources resources;

    @Inject
    public UserManagerFirebaseImpl(@Named("users") Firebase firebase, Resources resources) {
        this.firebase = firebase;
        this.resources = resources;
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
        }
    }

    @Override
    public void findWithName(final String fullName, final Observer<User> userObserver) {
        checkNotNull(fullName);
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        // No data is available. Fetch the data with Firebase API.
        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_user_dialog_message),
                false,
                false);
        progressDialog.show();

        // Attempting to retrieve from the local database
        // Concatenating firstName & lastName & Searching case-insensitively.
        List<User> users = User.findWithQuery(
            User.class,
            "SELECT * FROM user " +
                "WHERE firstName || ' ' || lastName LIKE \'%" + fullName + "%\' " +
                "COLLATE NOCASE ORDER BY admissionYear DESC");
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userObserver.onNext(user);
            }

            // Signaling that the searching has been completed.
            userObserver.onCompleted();
        }

        // Stopping the progress dialog.
        progressDialog.dismiss();
    }

    @Override
    public void findAll(final Observer<User> userObserver) {
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

        // Attempting to retrieve from the local database
        List<User> users = User.findWithQuery(
            User.class, "SELECT * FROM user ORDER BY admissionYear DESC");
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userObserver.onNext(user);
            }

            // Signaling that the searching has been completed.
            userObserver.onCompleted();
            progressDialog.dismiss();
        } else {
            userObserver.onError(new IllegalStateException());
            progressDialog.dismiss();
        }

    }

    @Override
    public boolean isExecutiveMember(String uid) {
        checkNotNull(uid);

        // Attempting to retrieve from the local database
        List<User> users = User.find(User.class, "uid = ?", uid);
        if (users != null && !users.isEmpty()) {
            return users.iterator().next().isExecutive();
        }

        throw new IllegalStateException(String.format("No user found with uid: %s", uid));
    }
}
