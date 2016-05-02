package com.kesa.modules;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.firebase.client.Firebase;
import com.kesa.account.AccountManager;
import com.kesa.account.AccountManagerFireBaseImpl;
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
    UserManager provideUserManager(@Named("users") Firebase firebase, Resources resources) {
        return new UserManagerFirebaseImpl(firebase, resources);
    }

    @Provides
    @Singleton
    AccountManager provideAccountManager(
        Resources resources,
        @Named("base") Firebase firebase,
        SharedPreferences sharedPreferences) {
        return new AccountManagerFireBaseImpl(resources, firebase, sharedPreferences);
    }

    @Provides
    @Singleton
    ImageManager provideImageManager() {
        return new ImageManager();
    }
}
