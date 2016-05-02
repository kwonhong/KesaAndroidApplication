package com.kesa.app;

import android.app.Application;
import android.content.Intent;

import com.firebase.client.Firebase;
import com.kesa.modules.AppModule;
import com.kesa.user.UserService;
import com.orm.SugarContext;

/**
 * An application of Korean Engineering Student Association.
 *
 * @author hongil@
 */
public class KesaApplication extends Application{

    private KesaComponent kesaComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        SugarContext.init(this);
        this.kesaComponent = DaggerKesaComponent.builder()
                .appModule(new AppModule(this))
                .build();

        // Running the UserService if it not currently running.
        if (!UserService.isServiceRunning(this)) {
            Intent userService = new Intent(this, UserService.class);
            startService(userService);
        }
    }

    public KesaComponent getComponent() {
        return kesaComponent;
    }
}
