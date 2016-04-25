package com.kesa.profile;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.common.base.Optional;
import com.kesa.app.KesaApplication;
import com.kesa.util.ResultHandler;

import rx.Observer;

/**
 * A manager handling profile data transactions for {@link KesaApplication}.
 *
 * @author hongil
 */
public abstract class UserManager {

    /** Mainly used to make the transactions synchronous by prompting a {@link ProgressDialog}. */
    protected Activity activity;

    public UserManager registerActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * Saves the given {@code user}.
     *
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void saveOrUpdate(final User user, final ResultHandler onCompleteListener);

    /**
     * Retrieves the profile data of the given {@code uid}.
     *
     * @param userObserver an Observer handling the profile data
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void get(final String uid, final Observer<User> userObserver);

    /**
     * Retrieves the profile data of all members satisfying the given {@code query}.
     *
     * @param userObserver an Observer handling the profile data
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void getMembers(final Observer<User> userObserver, Optional<String> query);

    public abstract void getExecutives(final Observer<User> userObserver);
}
