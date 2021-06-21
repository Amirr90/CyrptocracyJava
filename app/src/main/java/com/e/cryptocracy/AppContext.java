package com.e.cryptocracy;

import android.app.Application;

import com.e.cryptocracy.component.AppComponent;
import com.e.cryptocracy.component.DaggerAppComponent;
import com.e.cryptocracy.module.AppModule;

import dagger.Module;


@Module
public class AppContext extends Application {

    AppComponent appComponent;
    public static AppContext context;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule("")).build();

        context = this;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
