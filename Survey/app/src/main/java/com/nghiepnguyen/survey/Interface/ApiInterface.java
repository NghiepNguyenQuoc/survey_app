package com.nghiepnguyen.survey.Interface;

import com.nghiepnguyen.survey.model.ProjectModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by W10-PRO on 21-Sep-17.
 */

public interface ApiInterface {
    @GET("/getProjecList")
    Call<List<ProjectModel>> getProjectList();
}
