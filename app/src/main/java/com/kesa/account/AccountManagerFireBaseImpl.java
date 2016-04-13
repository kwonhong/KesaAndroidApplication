package com.kesa.account;

import android.app.ProgressDialog;
import android.content.res.Resources;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.kesa.R;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class AccountManagerFireBaseImpl extends AccountManager{

    private final Firebase firebase;
    private final Resources resources;

    @Inject
    public AccountManagerFireBaseImpl(Resources resources, Firebase firebase) {
        this.firebase = firebase;
        this.resources = resources;
    }

    @Override
    public void authenticateWithPassword(String email, String password) {
        checkNotNull(email);
        checkNotNull(password);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog = ProgressDialog.show(activity, null,
                resources.getString(R.string.authenticate_dialog_message), false, false);
        progressDialog.show();
        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                progressDialog.dismiss();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    void changePassword(String email, String oldPassword, String newPassword) {
        checkNotNull(email);
        checkNotNull(oldPassword);
        checkNotNull(newPassword);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog = ProgressDialog.show(activity, null,
                resources.getString(R.string.change_password_dialog_message), false, false);
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
    void changeEmail(String oldEmail, String newEmail, String password) {
        checkNotNull(oldEmail);
        checkNotNull(newEmail);
        checkNotNull(password);
        checkState(activity != null, "Activity must be registered before authentication.");

        final ProgressDialog progressDialog = ProgressDialog.show(activity, null,
                resources.getString(R.string.change_email_dialog_message), false, false);
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
