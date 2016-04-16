package com.kesa.profile;

import android.app.Activity;
import android.app.ProgressDialog;

import com.kesa.app.KesaApplication;

import rx.Observable;
import rx.Observer;

/**
 * A manager handling profile data transactions for {@link KesaApplication}.
 *
 * @author hongil
 */
public abstract class ProfileManager {

    /** Mainly used to make the transactions synchronous by prompting a {@link ProgressDialog}. */
    protected Activity activity;

    public ProfileManager registerActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * Saves the given {@code user}.
     *
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void saveOrUpdate(final User user);

    /**
     * Retrieves the profile data of the given {@code uid}.
     *
     * @param userObserver an {@link Observer} listening to the {@link Observable} retrieving the
     *                     profile data
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void get(final String uid, final Observer<User> userObserver);
}
