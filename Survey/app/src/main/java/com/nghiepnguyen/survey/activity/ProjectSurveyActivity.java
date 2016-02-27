package com.nghiepnguyen.survey.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.adapter.OptionQuestionnaireAdapter;
import com.nghiepnguyen.survey.model.AppMessageModel;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
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
 * question type :
 * -0 SA
 * - 1 CheckBok
 * -2 T/F
 * -3 Textbox
 * -4 Scale
 * -5 SA theo hang ngang
 * -6 CheckBok,Arrangements
 * -7 CheckBok
 * - 8 CheckSum
 * -9 Matrix MA
 * - 10 Matrix Column
 */
public class ProjectSurveyActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ProjectSurveyActivity";

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mQuestionContentTextView;
    private Button mNextQuestionButton;
    private Button mSaveSurveyButton;
    private ListView mOptionListView;


    private UserInfoModel currentUser;
    private QuestionModel questionModel;
    private ProjectModel projectModel;

    private List<QuestionnaireModel> questionnaireList;
    private OptionQuestionnaireAdapter optionQuestionnaireAdapter;
    private String strInputValue = "";
    private String strResponseOption = "";
    private List<Integer> pathList;

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
        callApiToGetNextQuestion(currentUser.getSecrectToken(), projectModel.getID(), "");
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
        mOptionListView = (ListView) findViewById(R.id.activity_project_survey_option_listview);

        mToolbar.setTitle(getResources().getString(R.string.nav_item_project_list));
        setSupportActionBar(mToolbar);

        mNextQuestionButton.setOnClickListener(this);

        pathList = new ArrayList<>();
    }

    // get data from Intent or Storage
    private void initDataToComponets() {
        projectModel = getIntent().getParcelableExtra(Constant.BUNDLE_QUESTION);

        //
        currentUser = UserInfoManager.getUserInfo(this);
    }

    public void callApiToGetNextQuestion(String secrectToken, int projectId, String preOption) {
        mProgressBar.setVisibility(View.VISIBLE);
        SurveyApiWrapper.getNextQuestion(this, secrectToken, projectId, preOption, new ICallBack() {
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
                if (questionModel != null) {
                    String patternString = String.format("<InputValue UserID=\"0\" QuestionnaireID=\"%d\" />", questionModel.getID()) + strInputValue;
                    callApiToGetResponseOption(patternString);
                } else {
                    // finished survey
                    if (pathList.size() == projectModel.getQuestionCount()) {
                        saveResultSurvey(1);
                    } else {// kick out of survey
                        saveResultSurvey(0);
                    }
                }
            }

            @Override
            public void onFailure(CommonErrorModel error) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    public void callApiToGetResponseOption(String inputValue) {
        SurveyApiWrapper.getResponseOptionByQuestionID(this, inputValue, new ICallBack() {
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
                 *
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        questionnaireList = (List<QuestionnaireModel>) data;
                        Spannable wordtoSpan = new SpannableString(questionModel.getCode() + ". " + questionModel.getQuestionText());
                        wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionModel.getCode().length() + 1, 0); // set size
                        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cl_pink)), 0, questionModel.getCode().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mQuestionContentTextView.setText(wordtoSpan);

                        optionQuestionnaireAdapter = new OptionQuestionnaireAdapter(ProjectSurveyActivity.this, questionModel, questionnaireList);
                        mOptionListView.setAdapter(optionQuestionnaireAdapter);

                        /*mFragment = new SAQuestionFragment();
                        Bundle args = new Bundle();
                        args.putParcelableArrayList(Constant.BUNDLE_QUESTIONNAIRE, (ArrayList<? extends Parcelable>) questionnaireList);
                        args.putParcelable(Constant.BUNDLE_QUESTION, questionModel);
                        mFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_project_survey_content_main_frame_layout, mFragment, Constant.PROJECT_LIST).commit();*/
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

    // save survey api
    public void saveResultSurvey(final int isCompleted) {
        String inputValue = String.format("<InputValue SecrectToken=\"%s\" UserID=\"%d\" ProjectID=\"%d\" isCompleted=\"%d\" Action=\"INSERT\"/>",
                currentUser.getSecrectToken(), currentUser.getID(), projectModel.getID(), isCompleted) + strResponseOption;

        SurveyApiWrapper.saveResultSurvey(this, inputValue, new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AppMessageModel messageModel = (AppMessageModel) data;
                        mProgressBar.setVisibility(View.GONE);

                        AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                        customBuilder.setCancelable(false);
                        if (isCompleted == 1) {
                            customBuilder.setTitle(getResources().getString(R.string.title_confirm));
                            customBuilder.setMessage(showMessageCompletedProject(messageModel));
                            customBuilder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProjectSurveyActivity.this.finish();
                                }
                            });

                            AlertDialog dialog = customBuilder.create();
                            dialog.show();
                            Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            if (b != null) {
                                b.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        } else {
                            // check more condition in here, IsSucessfull=false;
                            customBuilder.setTitle(getResources().getString(R.string.title_notice));
                            customBuilder.setMessage(getResources().getString(R.string.txt_not_suitable_option));
                            customBuilder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProjectSurveyActivity.this.finish();
                                }
                            });
                            customBuilder.show();
                        }


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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_project_survey_next_question_button:

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                dialog.setTitle(getResources().getString(R.string.title_attention));
                dialog.setPositiveButton(getResources().getString(R.string.button_ok), null);

                //////////////////////////////////////////////////////////////////
                //check maxAnswer
                boolean isMaxAnswer = checkMaxMaxResponse(questionModel.getType(), questionModel.getMaxResponseCount(), optionQuestionnaireAdapter.getOptionList());
                if (!isMaxAnswer) {
                    dialog.setMessage(String.format(getResources().getString(R.string.txt_over_max_answer), questionModel.getMaxResponseCount()));
                    dialog.show();
                    return;
                }

                //check emty
                boolean isEmtyAnswer = checkEmtyAnswer(questionModel.getType(), optionQuestionnaireAdapter.getOptionList());
                if (!isEmtyAnswer) {
                    dialog.setMessage(getResources().getString(R.string.txt_emty_answer));
                    dialog.show();
                    return;
                }

                // check logic
                boolean isLogicAnswer = checkLogicAnswer(questionModel.getType(), optionQuestionnaireAdapter.getOptionList());
                if (!isLogicAnswer) {
                    dialog.setMessage(getResources().getString(R.string.txt_emty_other_option));
                    dialog.show();
                    return;
                }

                /////////////////////////////////////////////////////////////////
                // collect data to send to server
                String patternString = "<R QID=\"%s\" V=\"%s\" T=\"%s\"/>";
                switch (questionModel.getType()) {
                    case 0:
                    case 1:
                        for (QuestionnaireModel item : optionQuestionnaireAdapter.getOptionList()) {
                            String valueOption;
                            if (item.isSelected()) {
                                if (item.getAllowInputText() == 1 && !TextUtils.isEmpty(item.getOtherOption()))
                                    valueOption = String.format(patternString, questionModel.getID(), item.getValue(), item.getOtherOption());
                                else
                                    valueOption = String.format(patternString, questionModel.getID(), item.getValue(), item.getDescription());
                                strInputValue += valueOption;
                                strResponseOption += valueOption;
                            }
                        }
                        pathList.add(questionModel.getID());
                        callApiToGetNextQuestion(currentUser.getSecrectToken(), projectModel.getID(), strInputValue);

                        break;
                }
                break;
        }
    }

    private boolean checkEmtyAnswer(int typeQuesion, List<QuestionnaireModel> questionnaireList) {
        switch (typeQuesion) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 6:
            case 7:
                for (QuestionnaireModel item : questionnaireList) {
                    if (item.isSelected())
                        return true;
                }
                break;
            case 3:
                break;
        }
        return false;
    }

    private boolean checkLogicAnswer(int typeQuesion, List<QuestionnaireModel> questionnaireList) {
        boolean flag = true;
        switch (typeQuesion) {
            case 0:
            case 1:
                for (QuestionnaireModel item : questionnaireList) {
                    if (item.isSelected() && item.getAllowInputText() == 1 && TextUtils.isEmpty(item.getOtherOption()))
                        flag = false;
                }
                break;
        }
        return flag;
    }

    private boolean checkMaxMaxResponse(int typeQuesion, int maxAnswer, List<QuestionnaireModel> questionnaireList) {
        if ((!(typeQuesion == 6 || typeQuesion == 1)) || maxAnswer == 0)
            return true;

        int count = 0;
        for (QuestionnaireModel item : questionnaireList) {
            if (item.isSelected())
                count++;
        }
        if (count > maxAnswer)
            return false;
        return true;
    }
    private String showMessageCompletedProject(AppMessageModel message){
        switch(message.getCode()){
            case "USER_RESPONSE_LOTTERYCODE":
            case "USER_RESPONSE_GIFT":
            case "OK":
                return message.getDescription();
            case "USER_RESPONSE_MARK":
                return String.format(getResources().getString(R.string.txt_completed_survey_mark),message.getResult());
        }
        return getResources().getString(R.string.txt_completed_survey);
    }
}