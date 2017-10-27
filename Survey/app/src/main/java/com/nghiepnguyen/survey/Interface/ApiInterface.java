package com.nghiepnguyen.survey.Interface;

import com.nghiepnguyen.survey.model.LoginModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by W10-PRO on 21-Sep-17.
 */

public interface ApiInterface {
    @POST("/api/admin/MemberLogin")
    @FormUrlEncoded
    Call<LoginModel> postLogin(@Field("userName") String userName, @Field("password") String password);

    @POST("/api/admin2/SaveSurvey")
    @FormUrlEncoded
    Call<List<ProjectModel>> postResultSurvey(@Field("FullName") String FullName, @Field("NumberID") String NumberID
            , @Field("PhoneNumber") String PhoneNumber, @Field("Address") String Address, @Field("Email") String Email
            , @Field("ProjectID") String ProjectID, @Field("IsCompeleted") String IsCompeleted, @Field("GPSLatitude") String GPSLatitude
            , @Field("GPSLongitude") String GPSLongitude, @Field("GPSAddress") String GPSAddress, @Field("GPSTime") String GPSTime
            , @Field("Action") String Action, @Field("Data") String Data);

    @GET("/api/admin/getProjecList")
    Call<List<ProjectModel>> getProjectList(@Query("UserId") int UserId, @Query("SecrectToken") String SecrectToken);

    @GET("/api/admin/getquestionarelist")
    Call<List<QuestionnaireModel>> getProjectData(@Query("projectID") int projectID);

    @GET("/api/admin/GetQuestionareConditionList")
    Call<List<RouteModel>> getProjectRoute(@Query("ProjectIDCondition") int ProjectIDCondition);
}
