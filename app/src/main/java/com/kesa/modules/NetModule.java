package com.kesa.modules;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.kesa.account.AccountManager;
import com.kesa.account.AccountManagerFireBaseImpl;
import com.kesa.event.EventManager;
import com.kesa.event.EventManagerFirebaseImpl;
import com.kesa.user.UserManager;
import com.kesa.user.UserManagerFirebaseImpl;
import com.kesa.util.ImageManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module containing the non-application provider methods.
 *
 * @author hongil@
 */
@Module
public class NetModule {

    @Provides
    @Singleton
    UserManager provideUserManager(@Named("users") DatabaseReference databaseReference,
                                   Resources resources) {
        return new UserManagerFirebaseImpl(databaseReference, resources);
    }

    @Provides
    @Singleton
    AccountManager provideAccountManager(
        Resources resources,
        FirebaseAuth firebaseAuth,
        SharedPreferences sharedPreferences) {
        return new AccountManagerFireBaseImpl(resources, firebaseAuth, sharedPreferences);
    }

    @Provides
    @Singleton
    ImageManager provideImageManager() {
        return new ImageManager();
    }

    @Provides
    @Singleton
    EventManager provideEventManager(@Named("events") DatabaseReference databaseReference,
                                     StorageReference storageReference,
                                     Resources resources) {
        return new EventManagerFirebaseImpl(databaseReference, storageReference, resources);
    }
}
