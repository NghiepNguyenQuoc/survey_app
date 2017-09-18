package com.nghiepnguyen.survey.Interface;

import com.google.gson.Gson;
import com.nghiepnguyen.survey.networking2.NetModule;

import javax.inject.Singleton;

import dagger.Component;
import retrofit.Retrofit;

/**
 * Created by Nghiep Nguyen on 17-Sep-17.
 */
@Singleton
@Component(modules = {NetModule.class})
public interface NetComponent {
    Retrofit retrofit();

    Gson gson();
}
