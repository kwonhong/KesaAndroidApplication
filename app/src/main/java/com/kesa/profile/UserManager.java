package com.kesa.profile;

import android.app.Activity;
import android.app.ProgressDialog;

import com.kesa.app.KesaApplication;
import com.kesa.util.ResultHandler;

import java.net.UnknownHostException;

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
     * Saves the given {@code user}. On network error, the method invokes
     * {@link ResultHandler#onError(Exception)} with {@link UnknownHostException}.
     *
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void saveOrUpdate(final User user, final ResultHandler resultHandler);

    /**
     * Retrieves the profile data of the given {@code uid}. It blocks the UI with
     * a {@link ProgressDialog} if the data needs to be retrieved through the network.
     *
     * @param userObserver an Observer handling the profile data
     * @throws IllegalStateException if {@code activity} is not registered
     */
    public abstract void findWithUID(final String uid, final Observer<User> userObserver);

    public abstract void findWithName(final String name, final Observer<User> userObserver);

    public abstract void findWithRole(final int roleId, final Observer<User> userObserver);
}
