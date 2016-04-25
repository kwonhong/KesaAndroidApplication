package com.kesa.account;


import android.app.Activity;
import android.app.ProgressDialog;

import com.kesa.app.KesaApplication;
import com.kesa.util.ResultHandler;

/**
 * A manager handling authentication into the {@link KesaApplication}.
 *
 * @author hongil
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
     * password} by displaying a {@link ProgressDialog}. On authentication error, the method invokes
     * {@link ResultHandler#onError(Exception)} method.
     *
     * @param resultHandler a callback method handling a success/error case of authentication
     * @throws IllegalStateException if {@code activity} is not registered.
     */
    public abstract void authenticateWithPassword(
        final String email,
        final String password,
        final ResultHandler resultHandler);

    /**
     * Synchronously creates a new account with given {@code email} and {@code password} by
     * displaying a {@link ProgressDialog}. On error, the method invokes
     * {@link ResultHandler#onError(Exception)} method.
     *
     * @param resultHandler a callback method being called on authentication success
     * @throws IllegalStateException if {@code activity} is not registered.
     */
    public abstract void createAccount(
        final String email,
        final String password,
        final ResultHandler resultHandler);

    /** Returns the unique identifier of the authenticated user. */
    public abstract String getCurrentUserUid();

    // TODO(hongil): Add javadoc
    public abstract void changePassword(
        final String email,
        final String oldPassword,
        final String newPassword);

    // TODO(hongil): Add javadoc
    public abstract void changeEmail(
        final String oldEmail,
        final String newEmail,
        final String password);


}
