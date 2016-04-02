package com.nghiepnguyen.survey.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.AppMessageModel;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.CompletedProject;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
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
    //private EditText mOtherOptionEditText;
    private Button mNextQuestionButton;
    private Button mSaveSurveyButton;
    private LinearLayout mOptionLinearLayout;


    //private UserInfoModel currentUser;
    private MemberModel currentMember;
    private List<QuestionnaireModel> projectQuestionnaireModels;
    private List<QuestionnaireModel> questionnaireModelList;
    private QuestionModel questionModel;
    private ProjectModel projectModel;

    private String strInputValue = "";
    private String strResponseOption = "";
    private List<Integer> pathList;

    private AppCompatRadioButton mSelectedRB;// current RadioButton when user focus
    private int mSelectedPosition = -1;// current position in adapter
    private int mCurrentQuestion = 0;// current position in adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_survey);
        initView();
        initDataToComponets();

        callApiToCheckCompletedProject(currentMember.getSecrectToken(), currentMember.getID(), projectModel.getID());
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        //mOtherOptionEditText = (EditText) findViewById(R.id.activity_project_survey_other_option_edittext);
        mNextQuestionButton = (Button) findViewById(R.id.activity_project_survey_next_question_button);
        mSaveSurveyButton = (Button) findViewById(R.id.activity_project_survey_save_survey_button);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_project_survey_loading_progress_bar);
        mOptionLinearLayout = (LinearLayout) findViewById(R.id.activity_project_survey_option_linearlayout);

        setSupportActionBar(mToolbar);

        mNextQuestionButton.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        pathList = new ArrayList<>();
    }

    // get data from Intent or Storage
    private void initDataToComponets() {
        projectModel = getIntent().getParcelableExtra(Constant.BUNDLE_QUESTION);
        getSupportActionBar().setTitle(projectModel.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //
        //currentUser = UserInfoManager.getUserInfo(this);
        currentMember = UserInfoManager.getMemberInfo(this);
    }

    private void callApiToCheckCompletedProject(String secrectToken, int userId, int projectId) {
        mProgressBar.setVisibility(View.VISIBLE);
        SurveyApiWrapper.checkCompeleteProject(this, secrectToken, userId, projectId, new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                final CompletedProject completedProject = (CompletedProject) data;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);

                        if (!completedProject.isSuccessfull()) {
                            AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                            customBuilder.setCancelable(false);
                            // check more condition in here, IsSucessfull=false;
                            customBuilder.setTitle(getResources().getString(R.string.title_notice));
                            customBuilder.setMessage(completedProject.getDescription());
                            customBuilder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProjectSurveyActivity.this.finish();
                                }
                            });
                            customBuilder.show();
                        } else {
                            callApiToDownloadProjectData(projectModel.getID());
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

    public void callApiToDownloadProjectData(int projectId) {
        mOptionLinearLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        SurveyApiWrapper.downloadProjectData(this, projectId, new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                /*{
                    "ID": 4109,
                        "Code": "Q1",
                        "QuestionText": "Hiện nay, mức độ thường xuyên ăn chay của bạn như thế nào? (SA)",
                        "Type": 0,
                        "ZOrderQuestion": 10,
                        "QuestionnaireID": 4109,
                        "Value": 1,
                        "Caption": "Mỗi ngày (ăn chay trường)",
                        "Description": "Mỗi ngày (ăn chay trường)",
                        "ZOrderOption": "0"
                },*/

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        projectQuestionnaireModels = (List<QuestionnaireModel>) data;
                        if (projectQuestionnaireModels != null && projectQuestionnaireModels.size() > 0) {
                            mCurrentQuestion = 0;
                            getNextQuestion(projectQuestionnaireModels, mCurrentQuestion);
                            mProgressBar.setVisibility(View.GONE);
                        } else {
                            // finished survey
                            if (pathList.size() == projectModel.getQuestionCount()) {
                                saveResultSurvey(1);
                            } else {// kick out of survey
                                saveResultSurvey(0);
                            }
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

    private void getNextQuestion(List<QuestionnaireModel> questionnaireModels, int questionnaireID) {
        questionnaireModelList = new ArrayList<>();
        for (int i = 0; i < questionnaireModels.size(); i++) {
            if (questionnaireModels.get(i).getQuestionnaireID() == questionnaireID) {
                if (i == 0) {
                    Spannable wordtoSpan = new SpannableString(questionnaireModels.get(i).getCode() + ". " + questionnaireModels.get(i).getQuestionText());
                    wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionnaireModels.get(i).getCode().length() + 1, 0); // set size
                    wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cl_pink)), 0, questionnaireModels.get(i).getCode().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mQuestionContentTextView.setText(wordtoSpan);

                    questionModel = new QuestionModel(Parcel.obtain());
                    questionModel.setID(questionnaireModels.get(i).getID());
                    questionModel.setQuestionText(questionnaireModels.get(i).getQuestionText());
                    questionModel.setZOrder(questionnaireModels.get(i).getZOrderQuestion());
                    questionModel.setCode(questionnaireModels.get(i).getCode());
                    questionModel.setType(questionnaireModels.get(i).getType());
                    //questionModel.setMaxResponseCount(questionnaireModels.get(i).getMaxResponseCount());
                    questionModel.setMaxResponseCount(6);
                }
                questionnaireModelList.add(questionnaireModels.get(i));
            }
        }

        if (questionnaireModelList != null && questionnaireModelList.size() > 0)
            generateOption(mOptionLinearLayout, questionnaireModelList);
    }

    private void generateOption(LinearLayout mainView, final List<QuestionnaireModel> questionnaireList) {
        LinearLayout linearLayout1 = new LinearLayout(ProjectSurveyActivity.this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);

        // add para to radio group
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        linearLayout1.setLayoutParams(params);

        for (QuestionnaireModel item : questionnaireList) {

            /*
            * create radio group
            */
            if (item.getType() == 0 || item.getType() == 2) {

                // create radio button
                AppCompatRadioButton radioButton = new AppCompatRadioButton(this);
                radioButton.setId(item.getID());
                radioButton.setText(item.getDescription());
                radioButton.setPadding(0, Constant.dpToPx(10, this), 0, Constant.dpToPx(10, this));

                if (item.getAllowInputText() == 1) {

                    // create linearlayout add radio button
                    LinearLayout linearLayout2 = new LinearLayout(this);
                    linearLayout2.setOrientation(LinearLayout.VERTICAL);
                    linearLayout2.addView(radioButton, params);

                    // create linearlayout add edittext
                    AppCompatEditText editText = new AppCompatEditText(this);
                    editText.setId(item.getID() * 10);
                    editText.setVisibility(View.GONE);
                    linearLayout2.addView(editText, params);

                    // add linearlayout to mainview
                    linearLayout1.addView(linearLayout2, params);
                } else {
                    // create radio button
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        radioButton.setBackground(getResources().getDrawable(R.drawable.radio_group_divider));
                    }

                    // add it to radio group
                    linearLayout1.addView(radioButton, params);
                }

                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int i = 0;
                        for (QuestionnaireModel item : questionnaireList) {
                            i++;
                            if (i != mSelectedPosition && mSelectedRB != null) {
                                mSelectedRB.setChecked(false);
                            }
                            mSelectedPosition = i;
                            AppCompatRadioButton radioButton = (AppCompatRadioButton) buttonView;
                            radioButton.setChecked(isChecked);
                            mSelectedRB = (AppCompatRadioButton) buttonView;

                            // set allow input
                            AppCompatEditText editText;
                            if (buttonView.getId() == item.getID() && buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                editText.setVisibility(View.VISIBLE);
                                editText.requestFocus();
                            } else if (buttonView.getId() == item.getID() && !buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                editText.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            } else if (item.getType() == 1) {
                // create radio button
                AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
                checkBox.setId(item.getID());
                checkBox.setText(item.getDescription());
                checkBox.setPadding(0, Constant.dpToPx(10, this), 0, Constant.dpToPx(10, this));

                if (item.getAllowInputText() == 1) {
                    // create linearlayout add checbox
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.addView(checkBox, params);

                    // create linearlayout add edittext
                    AppCompatEditText editText = new AppCompatEditText(this);
                    editText.setId(item.getID() * 10);
                    editText.setVisibility(View.GONE);
                    linearLayout.addView(editText, params);

                    // add linearlayout to mainview
                    linearLayout1.addView(linearLayout, params);
                } else {
                    // create radio button
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        checkBox.setBackground(getResources().getDrawable(R.drawable.radio_group_divider));
                    }
                    // add it to radio group
                    linearLayout1.addView(checkBox, params);
                }

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        for (QuestionnaireModel item : questionnaireList) {
                            // set allow input
                            AppCompatEditText editText;
                            if (buttonView.getId() == item.getID() && buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                editText.setVisibility(View.VISIBLE);
                                editText.requestFocus();
                            } else if (buttonView.getId() == item.getID() && !buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                editText.setVisibility(View.GONE);
                            }
                        }
                    }
                });

            }
        }

        // add group to main view
        if (mainView != null)
            mainView.removeView(linearLayout1);
        mainView.addView(linearLayout1);
    }

    // save survey api
    public void saveResultSurvey(final int isCompleted) {
        String inputValue = String.format("<InputValue SecrectToken=\"%s\" UserID=\"%d\" ProjectID=\"%d\" IsCompleted=\"%d\" Action=\"INSERT\"/>",
                currentMember.getSecrectToken(), currentMember.getID(), projectModel.getID(), isCompleted) + strResponseOption;

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

                // collect data from view
                for (QuestionnaireModel item : questionnaireModelList) {
                    if (questionModel.getType() == 0 || questionModel.getType() == 2) {
                        RadioButton radioButon = (RadioButton) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(radioButon.isChecked() == true ? 1 : 0);
                        if (radioButon.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                        }
                    } else {
                        CheckBox checkBox = (CheckBox) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(checkBox.isChecked() == true ? 1 : 0);
                        if (checkBox.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                        }
                    }
                }

                //////////////////////////////////////////////////////////////////
                //check maxAnswer
                boolean isMaxAnswer = checkMaxMaxResponse(questionModel.getType(), questionModel.getMaxResponseCount(), questionnaireModelList);
                if (!isMaxAnswer) {
                    dialog.setMessage(String.format(getResources().getString(R.string.txt_over_max_answer), questionModel.getMaxResponseCount()));
                    dialog.show();
                    return;
                }

                //check emty
                boolean isEmtyAnswer = checkEmtyAnswer(questionModel.getType(), questionnaireModelList);
                if (!isEmtyAnswer) {
                    dialog.setMessage(getResources().getString(R.string.txt_emty_answer));
                    dialog.show();
                    return;
                }

                // check logic
                boolean isLogicAnswer = checkLogicAnswer(questionModel.getType(), questionnaireModelList);
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
                        for (QuestionnaireModel item : questionnaireModelList) {
                            String valueOption;
                            if (item.getIsSelected() == 1) {
                                if (item.getAllowInputText() == 1 && !TextUtils.isEmpty(item.getOtherOption()))
                                    valueOption = String.format(patternString, questionModel.getID(), item.getValue(), item.getOtherOption());
                                else
                                    valueOption = String.format(patternString, questionModel.getID(), item.getValue(), item.getDescription());
                                strInputValue += valueOption;
                                strResponseOption += valueOption;
                            }
                        }
                        pathList.add(questionModel.getID());
                        getNextQuestion(projectQuestionnaireModels, mCurrentQuestion);
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
                    if (item.getIsSelected() == 1)
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
                    if (item.getIsSelected() == 1 && item.getAllowInputText() == 1 && TextUtils.isEmpty(item.getOtherOption()))
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
            if (item.getIsSelected() == 1)
                count++;
        }
        if (count > maxAnswer)
            return false;
        return true;
    }

    private String showMessageCompletedProject(AppMessageModel message) {
        switch (message.getCode()) {
            case "USER_RESPONSE_LOTTERYCODE":
            case "USER_RESPONSE_GIFT":
            case "OK":
                return message.getDescription();
            case "USER_RESPONSE_MARK":
                return String.format(getResources().getString(R.string.txt_completed_survey_mark), message.getResult());
        }
        return getResources().getString(R.string.txt_completed_survey);
    }
}