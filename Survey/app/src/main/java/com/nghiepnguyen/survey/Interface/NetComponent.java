package com.nghiepnguyen.survey.Interface;

import com.nghiepnguyen.survey.fragment.ProjectListFragment;
import com.nghiepnguyen.survey.networking2.AppModule;
import com.nghiepnguyen.survey.networking2.NetModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Nghiep Nguyen on 17-Sep-17.
 */
@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(ProjectListFragment projectListFragment);
}