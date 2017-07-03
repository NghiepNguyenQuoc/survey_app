package com.nghiepnguyen.survey.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.lassana.recorder.AudioRecorder;

/**
 * Created by nghiep on 10/29/15.
 */
public class MainApplication extends Application {
    private static MainApplication instance;
    private AudioRecorder mAudioRecorder;

    public MainApplication() {
        super();
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
    }

    public void setRecorder(@NonNull AudioRecorder recorder) {
        mAudioRecorder = recorder;
    }

    public AudioRecorder getRecorder() {
        return mAudioRecorder;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
