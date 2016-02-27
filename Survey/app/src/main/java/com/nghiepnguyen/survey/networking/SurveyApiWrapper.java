package com.nghiepnguyen.survey.networking;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.AppMessageModel;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.utils.Constant;
import com.nghiepnguyen.survey.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            }
        });
    }

    public static synchronized void getNextQuestion(final Context context, String session, int projectID, String preOption, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("session", session);
        para.put("projectID", projectID);
        para.put("preOption", preOption);
        client.get(Endpoint.GET_NEXT_QUESTION, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    JSONArray jsonArray = new JSONArray(content);
                    if (jsonArray.length() > 0) {
                        JSONObject jsonService = jsonArray.getJSONObject(0);
                        QuestionModel question = new Gson().fromJson(jsonService.toString(), QuestionModel.class);
                        callBack.onSuccess(question);
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
            }
        });
    }

    public static synchronized void getResponseOptionByQuestionID(final Context context, String inputValue, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("inputValue", inputValue);
        client.get(Endpoint.GET_RESPONSEOPTION_BY_QUESTION_ID_2, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    List<QuestionnaireModel> questionnaireList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonService = jsonArray.getJSONObject(i);
                        QuestionnaireModel questionnaire = new Gson().fromJson(jsonService.toString(), QuestionnaireModel.class);
                        questionnaireList.add(questionnaire);
                    }
                    callBack.onSuccess(questionnaireList);
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

    /*save survey*/
    public static synchronized void saveResultSurvey(final Context context, String inputValue, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        RequestParams para = new RequestParams();
        para.put("inputValue", inputValue);
        client.get(Endpoint.SAVE_RESULT_SURVEY, para, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                AppMessageModel message = new Gson().fromJson(content.toString(), AppMessageModel.class);
                callBack.onSuccess(message);
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

    // Get country code
    public static synchronized void getCountryCode(final Context context, final ICallBack callBack) {
        HttpClient client = new AsyncHttpClient();

        client.get(Endpoint.COUNTRY_CODE, null, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                //IpCountryInfo result = new Gson().fromJson(content, IpCountryInfo.class);
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
