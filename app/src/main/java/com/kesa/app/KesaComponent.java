package com.kesa.app;

import com.kesa.MainActivity;
import com.kesa.modules.AppModule;
import com.kesa.modules.NetModule;

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
}