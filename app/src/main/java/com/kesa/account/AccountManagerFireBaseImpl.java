package com.kesa.account;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kesa.R;
import com.kesa.util.ResultHandler;

import javax.inject.Inject;

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

    // TODO(hongilk): Changes to OAuth later
    private static final String AUTHENTICATED_EMAIL = "AuthenticatedEmail";
    private static final String AUTHENTICATED_PASSWORD = "AuthenticatedPassword";

    private final SharedPreferences sharedPreferences;
    private final FirebaseAuth firebaseAuth;
    private final Resources resources;

    @Inject
    public AccountManagerFireBaseImpl(
        Resources resources,
        FirebaseAuth firebaseAuth,
        SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.firebaseAuth = firebaseAuth;
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

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Saving the UID of the authenticated user to prevent redundant authentication.
                        sharedPreferences
                            .edit()
                            .putString(AUTHENTICATED_UID, task.getResult().getUser().getUid())
                            .putString(AUTHENTICATED_EMAIL, email)
                            .putString(AUTHENTICATED_PASSWORD, password)
                            .apply();
                        progressDialog.dismiss();
                        resultHandler.onComplete();
                        keepUserSignedIn(email, password);
                    } else {
                        // TODO(hongil): More error handling.
                        progressDialog.dismiss();
                        resultHandler.onError(task.getException());
                    }
                }
            });
    }

    @Override
    public void reauthenticate() {
        final String email = sharedPreferences.getString(AUTHENTICATED_EMAIL, null);
        final String password = sharedPreferences.getString(AUTHENTICATED_EMAIL, null);
        checkNotNull(email);
        checkNotNull(password);

        firebaseAuth.signInWithEmailAndPassword(email, password);
        keepUserSignedIn(email, password);
    }

    private void keepUserSignedIn(final String email, final String password) {
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    firebaseAuth.signInWithEmailAndPassword(email, password);
                }
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

        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If the new account was created, the user is also signed in
                    if (task.isSuccessful()) {
                        // Saving the UID of the authenticated user to prevent
                        // redundant authentication.
                        sharedPreferences
                            .edit()
                            .putString(AUTHENTICATED_UID, task.getResult().getUser().getUid())
                            .apply();
                        progressDialog.dismiss();
                        resultHandler.onComplete();
                        keepUserSignedIn(email, password);
                    } else {
                        // TODO(hongil): More error handling.
                        progressDialog.dismiss();
                        resultHandler.onError(task.getException());
                    }
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

        // TODO(hongil): Implement the functionality later
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

        // TODO(hongil): Implement the functionality later
    }
}
