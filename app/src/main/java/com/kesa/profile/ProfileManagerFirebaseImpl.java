package com.kesa.profile;

import android.app.ProgressDialog;
import android.content.res.Resources;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.kesa.R;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link ProfileManager} implementation with {@link Firebase} API.
 *
 * @author hongil@
 */
public class ProfileManagerFirebaseImpl extends ProfileManager {

    /** A Firebase-key containing all the users' data. */
    private static final String FIREBASE_USER = "users";

    private final Firebase firebase;
    private final Resources resources;

    @Inject
    public ProfileManagerFirebaseImpl(Firebase firebase, Resources resources) {
        this.firebase = firebase.child(FIREBASE_USER);
        this.resources = resources;
    }

    @Override
    public void saveOrUpdate(User user) {
        checkNotNull(user);
        checkState(activity != null, "Activity must be registered.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.saving_or_updating_user_dialog_message),
                false,
                false);
        progressDialog.show();

        this.firebase.child(user.getUid()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                // TODO(hongil): Handle error cases

                // Success case.
                progressDialog.dismiss();
                activity.finish();
            }
        });
    }

    @Override
    public void get(final String uid, final Observer<User> userObserver) {
        checkNotNull(userObserver);
        checkState(activity != null, "Activity must be registered.");

        final ProgressDialog progressDialog =
            ProgressDialog.show(
                activity,
                null,
                resources.getString(R.string.retrieving_user_dialog_message),
                false,
                false);
        progressDialog.show();

        // Creating an observable to retrieve latest snapshot of profile data.
        Observable<User> observable =
            Observable.create(new Observable.OnSubscribe<User>() {
                @Override
                public void call(final Subscriber<? super User> subscriber) {
                    firebase
                        .child(uid)
                        .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    subscriber.onNext(user);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    subscriber.onError(firebaseError.toException());
                                    progressDialog.dismiss();
                                }
                            });
                }
            });

        observable
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(userObserver);
    }
}
