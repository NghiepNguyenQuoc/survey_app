package com.nghiepnguyen.survey.networking;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.CompletedProject;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.SaveAnswerModel;
import com.nghiepnguyen.survey.utils.Constant;
import com.nghiepnguyen.survey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nghiep on 10/29/15.
 */
public class SurveyApiWrapper {
    private static final String TAG = "SurveyApiWrapper";

    // Get country code
    public static synchronized void loginToServer(final Context context, String username, String password, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("userName", username);
        para.put("password", password);
        client.post(Endpoint.LOGIN, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                callBack.onSuccess(content);
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
            }
        });
    }

    // member login
    public static synchronized void memberLogin(final Context context, String username, String password, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("userName", username);
        para.put("password", password);
        client.post(Endpoint.MEMBER_LOGIN, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                callBack.onSuccess(content);
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
            }
        });
    }


    public static synchronized void getProjectList(final Context context, int userId, String secrectToken, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("UserId", userId);
        para.put("SecrectToken", secrectToken);
        client.get(Endpoint.GET_PROJECT_LIST, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    List<ProjectModel> projectList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonService = jsonArray.getJSONObject(i);
                        ProjectModel project = new Gson().fromJson(jsonService.toString(), ProjectModel.class);
                        projectList.add(project);
                    }
                    callBack.onSuccess(projectList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
                }
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
                checkUnauthorizedAndHandleError(context, 0, throwable.getMessage(), callBack);
            }
        });
    }

    public static synchronized void checkCompeleteProject(final Context context, String secrectToken, int userId, int projectID, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("SecrectToken", secrectToken);
        para.put("userId", userId);
        para.put("projectID", projectID);
        client.get(Endpoint.CHECK_COMPLETED_PROJECT, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {

                try {
                    JSONObject jsonService = new JSONObject(content);
                    CompletedProject completedProject = new Gson().fromJson(jsonService.toString(), CompletedProject.class);
                    callBack.onSuccess(completedProject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
                }
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
            }
        });
    }

    public static synchronized void downloadProjectData(final Context context, int projectID, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("projectID", projectID);
        client.get(Endpoint.DOWNLOAD_PROJECT_DATA, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    /*Resources res = context.getResources();
                    InputStream in_s = res.openRawResource(R.raw.data);

                    byte[] b = new byte[in_s.available()];
                    in_s.read(b);*/
                    JSONArray jsonArray = new JSONArray(content);
                    if (jsonArray.length() > 0) {

                        Type listType = new TypeToken<List<QuestionnaireModel>>() {
                        }.getType();
                        List<QuestionnaireModel> questionnaireModels = new Gson().fromJson(jsonArray.toString(), listType);
                        callBack.onSuccess(questionnaireModels);
                    } else {
                        callBack.onSuccess(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
                }
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
                checkUnauthorizedAndHandleError(context, 0, throwable.getMessage(), callBack);

            }
        });
    }


    public static synchronized void downloadProjectRoute(final Context context, int projectID, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("ProjectIDCondition", projectID);
        client.get(Endpoint.DOWNLOAD_PROJECT_ROUTE, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    JSONArray jsonArray = new JSONArray(content);
                    if (jsonArray.length() > 0) {

                        Type listType = new TypeToken<List<RouteModel>>() {
                        }.getType();
                        List<RouteModel> routeModels = new Gson().fromJson(jsonArray.toString(), listType);
                        callBack.onSuccess(routeModels);
                    } else {
                        callBack.onSuccess(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
                }
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
                checkUnauthorizedAndHandleError(context, 0, throwable.getMessage(), callBack);

            }
        });
    }

    /*save survey*/
    public static synchronized void saveResultSurvey(final Context context, SaveAnswerModel saveAnswerModel, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();
        RequestParams para = new RequestParams();
        para.put("FullName", saveAnswerModel.getFullName());
        para.put("NumberID", saveAnswerModel.getNumberID());
        para.put("PhoneNumber", saveAnswerModel.getPhoneNumber());
        para.put("Address", saveAnswerModel.getAddress());
        para.put("Email", saveAnswerModel.getEmail());
        para.put("ProjectID", saveAnswerModel.getProjectID());
        para.put("IsCompeleted", saveAnswerModel.getIsCompeleted());
        para.put("GPSLatitude", saveAnswerModel.getGeoLatitude());
        para.put("GPSLongitude", saveAnswerModel.getGeoLongitude());
        para.put("GPSAddress", !TextUtils.isEmpty(saveAnswerModel.getGeoAddress()) ? saveAnswerModel.getGeoAddress() : "");
        para.put("GPSTime", !TextUtils.isEmpty(saveAnswerModel.getGeoTime()) ? saveAnswerModel.getGeoTime() : "");
        para.put("Action", "INSERT");
        para.put("Data", saveAnswerModel.getData());
        client.post(Endpoint.SAVE_RESULT_SURVEY, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                callBack.onSuccess(content);
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                Log.d(TAG, "Server responded with a status code " + statusCode);
                checkUnauthorizedAndHandleError(context, statusCode, content, callBack);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "An exception occurred during the request. Usually unable to connect or there was an error reading the response");
            }
        });
    }


    public static void checkUnauthorizedAndHandleError(Context context, int statusCode, String errorResponse, final ICallBack callback) {
        callback.onCompleted();
        CommonErrorModel error = new CommonErrorModel();
        if (!Utils.isNetworkAvailable(context) || statusCode == 0) {
            error.setCode(Constant.CODE_CANNOT_CONNECT_INTERNET);
            error.setError(context.getString(R.string.lost_internet_connection_message));
            callback.onFailure(error);
        } else if (statusCode == Constant.HTTP_UNAUTHORIZED) {
            /*Intent intent = new Intent(context, SignInActivity.class);
            intent.putExtra("statusCode", Constant.HTTP_UNAUTHORIZED);
            ((Activity) context).startActivityForResult(intent, Constant.KEY_ACTIVITY_RESULT_SIGN_IN);*/
        } else if (statusCode == 400 || statusCode == 422 || statusCode == 405) { // input invalid
            error.setCode(statusCode);
            try {
                error.setError(errorResponse);
            } catch (Exception e) {
                e.printStackTrace();
                error.setError(context.getString(R.string.survey_error_unfortunately));
            }
            callback.onFailure(error);
        } else {
            error.setCode(statusCode);
            error.setError(context.getString(R.string.survey_error_unfortunately));
            callback.onFailure(error);
        }

    }

}
