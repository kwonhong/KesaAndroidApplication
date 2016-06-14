package com.kesa.app;

import com.kesa.MainActivity;
import com.kesa.event.EventCreateActivity;
import com.kesa.event.EventFragment;
import com.kesa.members.MemberFragment;
import com.kesa.members.SearchActivity;
import com.kesa.account.LoginActivity;
import com.kesa.account.SignUpActivity;
import com.kesa.modules.AppModule;
import com.kesa.modules.NetModule;
import com.kesa.user.EditProfileActivity;
import com.kesa.user.ProfileActivity;
import com.kesa.user.UserService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component that assigns references in activities, services, or fragments to have access
 * to provider methods provided in module classes.
 *
 * @author hongil@
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface KesaComponent {
    void inject(MainActivity mainActivity);
    void inject(EditProfileActivity editProfileActivity);
    void inject(ProfileActivity profileActivity);
    void inject(LoginActivity loginActivity);
    void inject(SearchActivity searchActivity);
    void inject(SignUpActivity signUpActivity);
    void inject(MemberFragment memberFragment);
    void inject(UserService userService);
    void inject(EventFragment eventFragment);

    void inject(EventCreateActivity eventCreateActivity);
}
