package com.nghiepnguyen.survey.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.fragment.SAQuestionFragment;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nghiep Nguyen on 20/02/2016.
 * before start next question
 */
public class ProjectSurveyActivity extends BaseActivity {
    private static String TAG = "ProjectSurveyActivity";
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mQuestionContentTextView;
    private Button mNextQuestionButton;
    private Button mSaveSurveyButton;

    private UserInfoModel currentUser;
    private Fragment mFragment;
    private int questionId;
    private QuestionModel questionModel;
    private List<QuestionnaireModel> questionnaireList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_survey);
        initView();
        initDataToComponets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        callApiToGetNextQuestion();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_project_survey_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_project_survey_loading_progress_bar);
        mQuestionContentTextView = (TextView) findViewById(R.id.activity_project_survey_question_content_textview);
        mNextQuestionButton = (Button) findViewById(R.id.activity_project_survey_next_question_button);
        mSaveSurveyButton = (Button) findViewById(R.id.activity_project_survey_save_survey_button);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_project_survey_loading_progress_bar);

        mToolbar.setTitle(getResources().getString(R.string.nav_item_project_list));
        setSupportActionBar(mToolbar);
    }

    // get data from Intent or Storage
    private void initDataToComponets() {
        questionId = getIntent().getIntExtra(Constant.QUESTION_ID, 0);

        //
        currentUser = UserInfoManager.getUserInfo(this);
    }

    public void callApiToGetNextQuestion() {
        mProgressBar.setVisibility(View.VISIBLE);
        SurveyApiWrapper.getNextQuestion(this, currentUser.getSecrectToken(), questionId, "", new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                 /*
                 * [{
                 * "ID": 4013,
                 * "Code": "Q01",
                 * "ProjectID": 226,
                 * "ProjectName": "Nước giải khát đóng chai",
                 * "QuestionText": "Trong 3 tháng qua, bạn thường sử dụng những loại nước uống giải khát đóng chai/lon nào? (MA)",
                 * "ZOrder": 10,
                 * "Status": 0,
                 * "ParentID": 0,
                 * "CreatedDateTime": "2016-01-22T10:49:37.213",
                 * "CreatedBy": 50,
                 * "CreatedByName": "Lê Việt Thái",
                 * "LastUpdatedDateTime": "2016-01-29T11:52:13.44",
                 * "LastUpdatedBy": 50,
                 * "LastUpdatedByName": "Lê Việt Thái",
                 * "Type": 1,
                 * "TypeCode": "MA",
                 * "TypeName": "MA",
                 * "ScaleFrom": 0,
                 * "ScaleFromText": "",
                 * "ScaleTo": 0,
                 * "ScaleToText": "",
                 * "DependentID": 0,
                 * "MaxResponseCount": 0,
                 * "MinResponseCount": 0,
                 * "DisplayRandomResponse": false,
                 * "MediaUrl": null,
                 * "CheckSumValue": 0,
                 * "DependentType": 0,
                 * "ContentType": 0
                 * }]
                  */
                questionModel = (QuestionModel) data;
                callApiToGetResponseOption(questionModel.getID());
            }

            @Override
            public void onFailure(CommonErrorModel error) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    public void callApiToGetResponseOption(int questionId) {
        SurveyApiWrapper.getResponseOptionByQuestionID(this, questionId, new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                /**
                 * Created by 08670_000 on 22/02/2016.
                 * Option of a question
                 * [{
                 * "ID": 8429,
                 * "QuestionnaireID": 4015,
                 * "Type": 0,
                 * "Value": "1",
                 * "Symbol": "",
                 * "Caption": "Lavie (Nestle)",
                 * "Description": "Lavie (Nestle)",
                 * "ScaleFrom": 0,
                 * "ScaleFromText": "",
                 * "ScaleTo": 0,
                 * "ScaleToText": "",
                 * "ZOrder": "0000001",
                 * "AllowInputText": 0,
                 * "Status": 0,
                 * "CreatedDateTime": "/Date(-62135596800000)/",
                 * "CreatedBy": null,
                 * "LastUpdatedDateTime": null,
                 * "LastUpdatedBy": null,
                 * "MValue": 0,
                 * "MediaUrl": "",
                 * "ProjectID": null
                 * }, {
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        questionnaireList = (List<QuestionnaireModel>) data;
                        Spannable wordtoSpan = new SpannableString(questionModel.getCode() + ". " + questionModel.getQuestionText());
                        wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionModel.getCode().length() + 1, 0); // set size
                        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cl_pink)), 0, questionModel.getCode().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mQuestionContentTextView.setText(wordtoSpan);

                        mFragment = new SAQuestionFragment();
                        Bundle args = new Bundle();
                        args.putParcelableArrayList(Constant.BUNDLE_QUESTIONNAIRE, (ArrayList<? extends Parcelable>) questionnaireList);
                        mFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_project_survey_content_main_frame_layout, mFragment, Constant.PROJECT_LIST).commit();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });


            }

            @Override
            public void onFailure(CommonErrorModel error) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }
}