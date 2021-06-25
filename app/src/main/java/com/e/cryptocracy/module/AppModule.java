package com.e.cryptocracy.module;

import com.e.cryptocracy.Interface.RetrofitService;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    String name;

    public AppModule(String name) {
        this.name = name;
    }

    @Singleton
    @Provides
    OkHttpClient.Builder provideHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        httpClient.addInterceptor(logging);
        httpClient.dispatcher(dispatcher);
        return httpClient;
    }


    @Singleton
    @Provides
    Retrofit provideRetrofit(OkHttpClient.Builder builder) {
        return new Retrofit.Builder()
                .baseUrl(AppUrl.BASE_URL_coinpaprika)
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
    }

    @Singleton
    @Provides
    RetrofitService provideApi(Retrofit retrofit) {
        return retrofit.create(RetrofitService.class);
    }


    @Singleton
    @Provides
    HIPlotOptions provideHIPlotOptions() {
        return new HIPlotOptions();
    }

    @Singleton
    @Provides
    HIExporting provideHIExporting() {
        return new HIExporting();
    }

    @Singleton
    @Provides
    HIOptions provideHIOptions(HIPlotOptions plotOptions, HIExporting hiExporting) {
        HIOptions options = new HIOptions();
        final HIYAxis yAxis = new HIYAxis();
        yAxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITitle hiTitle = new HITitle();
        hiTitle.setText("");
        options.setTitle(hiTitle);
        plotOptions.setLine(new HILine());
        plotOptions.getLine().setEnableMouseTracking(true);
        options.setPlotOptions(plotOptions);
        hiExporting.setEnabled(false);
        options.setExporting(hiExporting);
        return options;
    }

}
