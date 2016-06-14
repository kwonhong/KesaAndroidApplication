package com.kesa.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module containing the application provider methods.
 *
 * @author hongil@
 */
@Module
public class AppModule {

    /** A Firebase-key containing all the users' data. */
    private static final String FIREBASE_USER = "users";

    /** A Firebase-key containing all the events' data. */
    private static final String FIREBASE_EVENT = "events";

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    Resources provideResources(Application application) {
        return application.getResources();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    FirebaseDatabase provideFirebaseDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        return firebaseDatabase;
    }

    @Provides
    @Singleton
    FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Provides
    @Singleton
    StorageReference provideImageStorageReference(FirebaseStorage firebaseStorage) {
        return firebaseStorage.getReferenceFromUrl("gs://torrid-inferno-4735.appspot.com/images");
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    @Named("users")
    DatabaseReference provideUserDatabaseReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference().child(FIREBASE_USER);
    }

    @Provides
    @Singleton
    @Named("events")
    DatabaseReference provideEventDatabaseReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference().child(FIREBASE_EVENT);
    }
}
