package com.kesa.event;

import android.app.Activity;
import android.app.ProgressDialog;

import com.kesa.util.ResultHandler;

import rx.Observer;

public abstract class EventManager {
    /** Mainly used to make the transactions synchronous by prompting a {@link ProgressDialog}. */
    protected Activity activity;

    public EventManager registerActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    // TODO(hongil): Add Javadoc
    public abstract void saveOrUpdate(final Event event, final ResultHandler resultHandler);

    // TODO(hongil): Add Javadoc
    public abstract void findAll(final Observer<Event> eventObserver);

    // TODO(hongil): Add Javadoc
    public abstract void findById(final String uid, final Observer<Event> eventObserver);
}
