package org.sdfw.biometric.di.component;

import android.app.Application;

import org.sdfw.biometric.App;
import org.sdfw.biometric.di.module.ActivityBindingModule;
import org.sdfw.biometric.di.module.AppModule;
import org.sdfw.biometric.di.module.FragmentModule;
import org.sdfw.biometric.di.module.ServiceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules={AndroidSupportInjectionModule.class, ActivityBindingModule.class,
        ServiceModule.class, FragmentModule.class, AppModule.class})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(App app);
}
