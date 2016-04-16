package com.kesa.modules;

import android.content.res.Resources;

import com.firebase.client.Firebase;
import com.kesa.util.ImageEncoder;
import com.kesa.profile.ProfileManager;
import com.kesa.profile.ProfileManagerFirebaseImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module containing the provide methods for dependency graph.
 *
 * @author hongil@
 */
@Module
public class NetModule {
    @Provides
    @Singleton
    ProfileManager provideProfileManager(Firebase firebase, Resources resources) {
        return new ProfileManagerFirebaseImpl(firebase, resources);
    }

    @Provides
    @Singleton
    ImageEncoder provideImageEncoder() {
        return new ImageEncoder();
    }
}
