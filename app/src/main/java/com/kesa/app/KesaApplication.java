package com.kesa.app;

import android.app.Application;

import com.kesa.modules.AppModule;

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
        this.kesaComponent = DaggerKesaComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public KesaComponent getComponent() {
        return kesaComponent;
    }
}
