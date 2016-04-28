package com.kesa.app;

import android.app.Application;

import com.firebase.client.Firebase;
import com.kesa.modules.AppModule;
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
    }

    public KesaComponent getComponent() {
        return kesaComponent;
    }
}
