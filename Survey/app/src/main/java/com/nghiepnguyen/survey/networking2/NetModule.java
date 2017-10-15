package com.nghiepnguyen.survey.networking2;

/**
 * Created by Nghiep Nguyen on 17-Sep-17.
 */

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {
    String mBaseUrl;

    public NetModule(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }


    @Provides
    @Singleton
    okhttp3.Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        okhttp3.Cache cache = new okhttp3.Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    okhttp3.OkHttpClient provideOkhttpClient(okhttp3.Cache cache) {
        okhttp3.OkHttpClient.Builder client = new okhttp3.OkHttpClient.Builder();
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    retrofit2.Retrofit provideRetrofit(Gson gson, okhttp3.OkHttpClient okHttpClient) {
        return new retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();
    }
}
