package com.kesa.account;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.kesa.R;
import com.kesa.util.ResultHandler;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An implementation of the {@link AccountManager} with the Firebase Authentication API.
 *
 * @author hongil
 */
public class AccountManagerFireBaseImpl extends AccountManager {

    /**
     * A key to retrieve the uid of the authenticated user from the
     * {@link SharedPreferences} file.
     */
    private static final String AUTHENTICATED_UID = "AuthenticatedUid";

    private final SharedPreferences sharedPreferences;
    private final Firebase firebase;
    private final Resources resources;

    @Inject
    public AccountManagerFireBaseImpl(
        Resources resources,
        @Named("base") Firebase firebase,
        SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.firebase = firebase;
        this.resources = resources;
    }

    @Override
    public void authenticateWithPassword(
        final String email,
        final String password,
        final ResultHandler resultHandler) {

        checkNotNull(email);
        checkNotNull(password);
        checkNotNull(resultHandler);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.authenticate_dialog_message),
                false,
                false);
        progressDialog.show();
        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Saving the UID of the authenticated user to prevent redundant authentication.
                sharedPreferences
                    .edit()
                    .putString(AUTHENTICATED_UID, authData.getUid())
                    .apply();
                progressDialog.dismiss();
                resultHandler.onComplete();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // TODO(hongil): More error handling.
                progressDialog.dismiss();
                resultHandler.onError(firebaseError.toException());
            }
        });
    }

    @Override
    public void createAccount(
        final String email,
        final String password,
        final ResultHandler resultHandler) {

        checkNotNull(email);
        checkNotNull(password);
        checkNotNull(resultHandler);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.create_account_dialog_message),
                false,
                false);
        progressDialog.show();
        firebase.createUser(
            email,
            password,
            new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    // Saving the UID of the authenticated user to prevent redundant authentication.
                    sharedPreferences
                        .edit()
                        .putString(AUTHENTICATED_UID, (String) stringObjectMap.get("uid"))
                        .apply();
                    progressDialog.dismiss();
                    resultHandler.onComplete();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    // TODO(hongil): More error handling.
                    progressDialog.dismiss();
                    resultHandler.onError(firebaseError.toException());
                }
            });
    }

    @Override
    public String getCurrentUserUid() {
        return sharedPreferences.getString(AUTHENTICATED_UID, null);
    }

    @Override
    public void clearPreviousAuthentication() {
        // Clearing the UID of the authenticated user.
        sharedPreferences
            .edit()
            .remove(AUTHENTICATED_UID)
            .apply();
    }

    @Override
    public void changePassword(
        final String email,
        final String oldPassword,
        final String newPassword) {

        checkNotNull(email);
        checkNotNull(oldPassword);
        checkNotNull(newPassword);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.change_password_dialog_message),
                false,
                false);
        progressDialog.show();
        firebase.changePassword(email, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void changeEmail(
        final String oldEmail,
        final String newEmail,
        final String password) {

        checkNotNull(oldEmail);
        checkNotNull(newEmail);
        checkNotNull(password);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.change_email_dialog_message),
                false,
                false);
        progressDialog.show();
        firebase.changeEmail(oldEmail, password, newEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
