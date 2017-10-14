package com.nghiepnguyen.survey.Interface;

import com.nghiepnguyen.survey.activity.MainActivity;
import com.nghiepnguyen.survey.networking2.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Nghiep Nguyen on 17-Sep-17.
 */
@Singleton
@Component(modules = AppModule.class)
public interface NetComponent {
    void inject(MainActivity activity);
}

/*
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {

    Retrofit retrofit();

    OkHttpClient okHttpClient();

    SharedPreferences sharedPreferences();
}
*/
