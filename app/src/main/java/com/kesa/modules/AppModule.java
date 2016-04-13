package com.kesa.modules;

import android.app.Application;
import android.content.res.Resources;

import com.firebase.client.Firebase;
import com.kesa.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module containing application provider methods.
 *
 * @author hongil@
 */
@Module
public class AppModule {
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
    Firebase provideFirebase(Resources resources) {
        return new Firebase(resources.getString(R.string.firebase_url));
    }
}
