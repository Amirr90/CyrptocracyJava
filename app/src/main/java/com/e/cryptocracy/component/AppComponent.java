package com.e.cryptocracy.component;


import com.e.cryptocracy.CoinDetailActivity;
import com.e.cryptocracy.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(CoinDetailActivity activity);
}
