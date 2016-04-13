package com.kesa.account;


import android.app.Activity;
import android.app.ProgressDialog;

/**
 * A manager handling authentication into your application.
 *
 * @author hongil@
 */
public abstract class AccountManager {

    // TODO(hongil): Add javadoc
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
     * @throws IllegalStateException if {@code activity} is not registered.
     */
    abstract void authenticateWithPassword(String email, String password);

    // TODO(hongil): Add javadoc
    abstract void changePassword(String email, String oldPassword, String newPassword);

    // TODO(hongil): Add javadoc
    abstract void changeEmail(String oldEmail, String newEmail, String password);
}
