package com.kesa.profile;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import javax.inject.Inject;

/**
 * TODO(hongil): Add Javadoc
 */
public class ProfileManagerFirebaseImpl implements ProfileManager {

    /** TODO(hongil): Add Javadoc */
    private static final String FIREBASE_USER = "users";

    private final Firebase firebase;

    @Inject
    public ProfileManagerFirebaseImpl(Firebase firebase) {
        this.firebase = firebase.child(FIREBASE_USER);
    }

    @Override
    public void saveOrUpdate(User user) {
        this.firebase.child(user.getUid()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                // TODO(hongil): Handle error cases
            }
        });
    }

    @Override
    public void remove(User user) {
        this.firebase.child(user.getUid()).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                // TODO(hongil): Handle error cases
            }
        });
    }
}
