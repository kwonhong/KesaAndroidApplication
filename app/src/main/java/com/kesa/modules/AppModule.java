package com.kesa.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.firebase.client.Firebase;
import com.kesa.R;

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
    @Named("base")
    Firebase provideFirebaseBase(Resources resources) {
        return new Firebase(resources.getString(R.string.firebase_url));
    }

    @Provides
    @Singleton
    @Named("users")
    Firebase provideFirebaseUsers(@Named("base") Firebase firebase) {
        return firebase.child(FIREBASE_USER);
    }
}
