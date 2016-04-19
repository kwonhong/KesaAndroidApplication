package com.kesa.account;


import android.app.Activity;
import android.app.ProgressDialog;

import com.kesa.util.OnCompleteListener;

/**
 * A manager handling authentication into your application.
 *
 * @author hongil@
 */
public abstract class AccountManager {

    /** Mainly used to make the transactions synchronous by prompting a {@link ProgressDialog}. */
    protected Activity activity;

    public AccountManager registerActivity(final Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * Synchronously authenticates into the application using the given {@code email} and {@code
     * password} by displaying a {@link ProgressDialog}. On authentication error, the method prompt
     * an alert dialog with the registered {@code activity}.
     *
     * @param onCompleteListener a callback method being called on authentication success
     * @throws IllegalStateException if {@code activity} is not registered.
     */
    public abstract void authenticateWithPassword(
        final String email,
        final String password,
        final OnCompleteListener onCompleteListener);

    /** Returns the unique identifier of the authenticated user. */
    public abstract String getCurrentUserUid();

    /**
     * Synchronously creates a new account with given {@code email} and {@code password} by
     * displaying a {@link ProgressDialog}.
     *
     * @param onCompleteListener a callback method being called on authentication success
     * @throws IllegalStateException if {@code activity} is not registered.
     */
    public abstract void createAccount(
        final String email,
        final String password,
        final OnCompleteListener onCompleteListener);

    // TODO(hongil): Add javadoc
    public abstract void changePassword(String email, String oldPassword, String newPassword);

    // TODO(hongil): Add javadoc
    public abstract void changeEmail(String oldEmail, String newEmail, String password);


}
