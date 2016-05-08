package com.nghiepnguyen.survey.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
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
import android.util.Log;
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
import com.nghiepnguyen.survey.model.AnswerModel;
import com.nghiepnguyen.survey.model.AppMessageModel;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.CompletedProject;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.sqlite.QuestionaireSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.RouteSQLiteHelper;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ProjectSurveyActivity extends BaseActivity implements View.OnClickListener, LocationListener {
    private static String TAG = "ProjectSurveyActivity";

    private ProgressBar mProgressBar;
    private TextView mQuestionContentTextView;
    private LinearLayout mOptionLinearLayout;


    //private UserInfoModel currentUser;
    private MemberModel currentMember;
    private List<QuestionnaireModel> questionnaireModelList;
    private QuestionModel questionModel;
    private ProjectModel projectModel;

    private String strInputValue = "";
    private String strResponseOption = "";
    private List<Integer> pathList;
    private List<Integer> questionnaireIds;
    private Map<Integer, AnswerModel> answerModels;
    private int currentIndexQuestionID = 0;

    private AppCompatRadioButton mSelectedRB;// current RadioButton when user focus
    private int mSelectedPosition = -1;// current position in adapter

    private QuestionaireSQLiteHelper questionaireSQLiteHelper;
    private LocationManager locationManager;

    // flag for GPS status
    private boolean isGPSEnabled = false;
    // flag for network status
    private boolean isNetworkEnabled = false;

    private Location location;
    private double latitude = 0; // latitude
    private double longitude = 0; // longitude

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_survey);
        initView();
        initDataToComponets();

        answerModels = new ArrayMap<>();
        //callApiToCheckCompletedProject(currentMember.getSecrectToken(), currentMember.getID(), projectModel.getID());
        questionnaireIds = questionaireSQLiteHelper.getAllQuestionIDByProjectId(projectModel.getID());
        getNextQuestion();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        location = getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
        customBuilder.setTitle(getString(R.string.title_confirm));
        customBuilder.setMessage(getString(R.string.txt_save_project_message));
        customBuilder.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProjectSurveyActivity.this.finish();
            }
        });
        customBuilder.setNegativeButton(getString(R.string.button_save_survey), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // save survey ...
            }
        });
        customBuilder.show();
    }


    private void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_project_survey_toolbar);
        mQuestionContentTextView = (TextView) findViewById(R.id.activity_project_survey_question_content_textview);
        Button mNextQuestionButton = (Button) findViewById(R.id.activity_project_survey_next_question_button);
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
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(projectModel.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //
        //currentUser = UserInfoManager.getUserInfo(this);
        currentMember = UserInfoManager.getMemberInfo(this);

        // Database Helper
        questionaireSQLiteHelper = new QuestionaireSQLiteHelper(getApplicationContext());
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
                            customBuilder.setTitle(getString(R.string.title_notice));
                            customBuilder.setMessage(completedProject.getDescription());
                            customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProjectSurveyActivity.this.finish();
                                }
                            });
                            customBuilder.show();
                        } else {
                            //callApiToDownloadProjectData(projectModel.getID());
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

    private void getNextQuestion() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (currentIndexQuestionID < questionnaireIds.size()) {
            // clear questionnaireModelList
            if (questionnaireModelList == null)
                questionnaireModelList = new ArrayList<>();
            questionnaireModelList.clear();

            // get all option and question
            List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(questionnaireIds.get(currentIndexQuestionID));
            if (questionnaireModels.get(0).getDependentID() != 0) {
                String questionText = questionnaireModels.get(0).getQuestionText();
                if (questionText.contains("[LIKED]")) {
                    AnswerModel answerModel = answerModels.get(questionnaireIds.get(currentIndexQuestionID - 1));// get answer from dependent question
                    for (Map.Entry<Integer, String> entry : answerModel.getArrText().entrySet()) {// duyet qua tac ca cac dap an da chon
                        questionText.replace("[LIKED]", entry.getValue());
                        break;
                    }
                }

                Spannable wordtoSpan = new SpannableString(questionnaireModels.get(0).getCode() + ". " + questionText);
                wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionnaireModels.get(0).getCode().length() + 1, 0); // set size
                wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cl_pink)), 0, questionnaireModels.get(0).getCode().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mQuestionContentTextView.setText(wordtoSpan);

                questionModel = new QuestionModel(Parcel.obtain());
                questionModel.setID(questionnaireModels.get(0).getID());
                questionModel.setQuestionText(questionnaireModels.get(0).getQuestionText());
                questionModel.setZOrder(questionnaireModels.get(0).getZOrderQuestion());
                questionModel.setCode(questionnaireModels.get(0).getCode());
                questionModel.setType(questionnaireModels.get(0).getType());
                questionModel.setMaxResponseCount(questionnaireModels.get(0).getMaxResponseCount());

                // get depend list option
                findQuestionnaireModelsByDependentID(questionnaireModels.get(0).getQuestionnaireID(), questionnaireModels.get(0).getDependentID(), questionnaireModels.get(0).getDependentID(), answerModels, questionModel, questionnaireModelList);

            } else {// get new questionnaireModelList
                for (int i = 0; i < questionnaireModels.size(); i++) {
                    if (i == 0) {

                        // cap nhau tieu de cau khoi khi co ki tu [LIKED]
                        String questionText = questionnaireModels.get(0).getQuestionText();
                        if (questionText.contains("[LIKED]")) {
                            AnswerModel answerModel = answerModels.get(questionnaireIds.get(currentIndexQuestionID - 1));// get answer from dependent question
                            for (Map.Entry<Integer, String> entry : answerModel.getArrText().entrySet()) {// duyet qua tac ca cac dap an da chon
                                questionText = questionText.replace("[LIKED]", entry.getValue());
                                break;
                            }
                        }

                        Spannable wordtoSpan = new SpannableString(questionnaireModels.get(i).getCode() + ". " + questionText);
                        wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionnaireModels.get(i).getCode().length() + 1, 0); // set size
                        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cl_pink)), 0, questionnaireModels.get(i).getCode().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mQuestionContentTextView.setText(wordtoSpan);

                        questionModel = new QuestionModel(Parcel.obtain());
                        questionModel.setID(questionnaireModels.get(i).getID());
                        questionModel.setQuestionText(questionnaireModels.get(i).getQuestionText());
                        questionModel.setZOrder(questionnaireModels.get(i).getZOrderQuestion());
                        questionModel.setCode(questionnaireModels.get(i).getCode());
                        questionModel.setType(questionnaireModels.get(i).getType());
                        questionModel.setMaxResponseCount(questionnaireModels.get(i).getMaxResponseCount());
                    }
                    questionnaireModelList.add(questionnaireModels.get(i));
                }
            }

            // generate options
            if (questionnaireModelList != null && questionnaireModelList.size() > 0)
                generateOption(mOptionLinearLayout, questionnaireModelList);

            // move cursor to next question
            currentIndexQuestionID++;
        } else if (questionnaireIds.size() == 0) {
            AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
            customBuilder.setCancelable(false);
            customBuilder.setTitle(getString(R.string.title_notice));
            customBuilder.setMessage(getString(R.string.message_project_not_ready));
            customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ProjectSurveyActivity.this.finish();
                }
            });
            customBuilder.show();
        } else {
           /* // finished survey
            if (pathList.size() == projectModel.getQuestionCount()) {
                saveResultSurvey(1);
            } else {// kick out of survey
                saveResultSurvey(0);
            }*/
            AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
            customBuilder.setCancelable(false);
            customBuilder.setTitle(getString(R.string.title_notice));
            customBuilder.setMessage(getString(R.string.txt_completed_survey));
            customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ProjectSurveyActivity.this.finish();
                }
            });
            customBuilder.show();
        }
        mProgressBar.setVisibility(View.GONE);

    }

    private void findQuestionnaireModelsByDependentID(int rootQuestionnaireID, int rootDependentID, int dependentID,
                                                      Map<Integer, AnswerModel> answerModels, QuestionModel questionModel,
                                                      List<QuestionnaireModel> questionnaireModelList) {
        List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(dependentID);
        if (questionnaireModels.get(0).getDependentID() != 0)
            findQuestionnaireModelsByDependentID(rootQuestionnaireID, rootDependentID, questionnaireModels.get(0).getDependentID(), answerModels, questionModel, questionnaireModelList);

        for (int i = 0; i < questionnaireModels.size(); i++) {
            AnswerModel answerModel = answerModels.get(rootDependentID);// get answer from dependent question
            for (Map.Entry<Integer, String> entry : answerModel.getArrValue().entrySet()) {// duyet qua tac ca cac dap an da chon
                if (entry.getKey() == questionnaireModels.get(i).getValue()) {
                    questionnaireModels.get(i).setQuestionnaireID(rootQuestionnaireID);
                    questionnaireModels.get(i).setType(questionModel.getType());
                    questionnaireModels.get(i).setIsSelected(0);
                    questionnaireModelList.add(questionnaireModels.get(i));
                }
            }
        }
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        radioButton.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_group_divider));


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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        checkBox.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_group_divider));

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
        assert mainView != null;
        mainView.removeAllViews();
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
                            customBuilder.setTitle(getString(R.string.title_confirm));
                            customBuilder.setMessage(showMessageCompletedProject(messageModel));
                            customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProjectSurveyActivity.this.finish();
                                }
                            });

                            AlertDialog dialog = customBuilder.create();
                            dialog.show();
                            Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            if (b != null) {
                                b.setTextColor(ContextCompat.getColor(ProjectSurveyActivity.this, R.color.colorPrimary));
                            }
                        } else {
                            // check more condition in here, IsSucessfull=false;
                            customBuilder.setTitle(getString(R.string.title_notice));
                            customBuilder.setMessage(getString(R.string.txt_not_suitable_option));
                            customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
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
                dialog.setTitle(getString(R.string.title_attention));
                dialog.setPositiveButton(getString(R.string.button_ok), null);

                Map<Integer, String> selectedValueMap = new ArrayMap<>();
                Map<Integer, String> selectedTextMap = new ArrayMap<>();
                // collect data from view
                for (QuestionnaireModel item : questionnaireModelList) {
                    if (questionModel.getType() == 0 || questionModel.getType() == 2) {
                        RadioButton radioButon = (RadioButton) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(radioButon.isChecked() ? 1 : 0);

                        if (radioButon.isChecked()) {
                            selectedValueMap.put(item.getValue(), "");
                            selectedTextMap.put(item.getValue(), item.getDescription());
                        }

                        if (radioButon.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                            selectedValueMap.put(item.getValue(), editText.getText().toString());
                        }
                    } else {
                        CheckBox checkBox = (CheckBox) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(checkBox.isChecked() ? 1 : 0);

                        if (checkBox.isChecked()) {
                            selectedValueMap.put(item.getValue(), "");
                            selectedTextMap.put(item.getValue(), item.getDescription());
                        }

                        if (checkBox.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                            selectedValueMap.put(item.getValue(), editText.getText().toString());
                        }
                    }
                }

                // save to answer list
                answerModels.put(questionnaireModelList.get(0).getQuestionnaireID(),
                        new AnswerModel(questionnaireModelList.get(0).getQuestionnaireID(), selectedTextMap, selectedValueMap));

                //////////////////////////////////////////////////////////////////
                //check maxAnswer
                boolean isMaxAnswer = checkMaxMaxResponse(questionModel.getType(), questionModel.getMaxResponseCount(), questionnaireModelList);
                if (!isMaxAnswer) {
                    dialog.setMessage(String.format(getString(R.string.txt_over_max_answer), questionModel.getMaxResponseCount()));
                    dialog.show();
                    return;
                }

                //check emty
                boolean isEmtyAnswer = checkEmtyAnswer(questionModel.getType(), questionnaireModelList);
                if (!isEmtyAnswer) {
                    dialog.setMessage(getString(R.string.txt_emty_answer));
                    dialog.show();
                    return;
                }

                // check logic
                boolean isLogicAnswer = checkLogicAnswer(questionModel.getType(), questionnaireModelList);
                if (!isLogicAnswer) {
                    dialog.setMessage(getString(R.string.txt_emty_other_option));
                    dialog.show();
                    return;
                }

                // check logic
                List<String> arrOptionExclusion = new ArrayList<>();
                boolean isExclusion = checkExclusion(questionModel.getType(), questionnaireModelList, arrOptionExclusion);
                if (!isExclusion) {
                    dialog.setMessage(String.format(getString(R.string.txt_exclusion), arrOptionExclusion.toString()));
                    dialog.show();
                    return;
                }

                //check route
                boolean isPassLogic = checkStopLogic(answerModels, questionnaireModelList);
                if (!isPassLogic) {
                    AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                    customBuilder.setCancelable(false);
                    customBuilder.setTitle(getString(R.string.title_notice));
                    customBuilder.setMessage(getString(R.string.txt_not_suitable_option));
                    customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ProjectSurveyActivity.this.finish();
                        }
                    });
                    customBuilder.show();
                } else {
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
                            getNextQuestion();
                            break;
                    }
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

    private boolean checkExclusion(int typeQuesion, List<QuestionnaireModel> questionnaireList, List<String> arrOptionExclusion) {
        switch (typeQuesion) {
            case 1:
            case 7:
                int numResponse = 0;
                for (QuestionnaireModel item : questionnaireList) {
                    if (item.getIsSelected() == 1) {
                        numResponse++;
                        if (item.getExclusion() == 1)
                            arrOptionExclusion.add(item.getDescription());
                    }
                }

                if ((arrOptionExclusion.size() > 0 && arrOptionExclusion.size() != numResponse) || arrOptionExclusion.size() > 1)
                    return false;
                break;
        }
        return true;
    }

    private boolean checkMaxMaxResponse(int typeQuesion, int maxAnswer, List<QuestionnaireModel> questionnaireList) {
        if ((!(typeQuesion == 6 || typeQuesion == 1)) || maxAnswer == 0)
            return true;

        int count = 0;
        for (QuestionnaireModel item : questionnaireList) {
            if (item.getIsSelected() == 1)
                count++;
        }
        return count <= maxAnswer;
    }

    private boolean checkStopLogic(Map<Integer, AnswerModel> answerModels, List<QuestionnaireModel> questionnaireList) {
        RouteSQLiteHelper routeSQLiteHelper = new RouteSQLiteHelper(this);

        List<RouteModel> routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireList.get(0).getQuestionnaireID());
        if (routeModels != null && routeModels.size() > 0) {// check stop logic
            for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                if (routeModels.get(i).getNextQuestionnaireID() < 0) {// Dieu kien dung
                    AnswerModel answerModel = answerModels.get(routeModels.get(i).getQuestionnaireID_Check_Option());
                    for (Map.Entry<Integer, String> entry : answerModel.getArrValue().entrySet()) {// duyet qua tac ca cac dap an da chon
                        if (routeModels.get(i).getMethod() == 0 || routeModels.get(i).getMethod() == 2) {// OR
                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            for (String s : arrResponseValue) {// duyet qua tac ca cac response value
                                if (entry.getKey() == Integer.valueOf(s)) {
                                    return false;
                                }
                            }
                        }
                    }
                } else {

                }
            }
        }

        // check next logic
        if (currentIndexQuestionID < questionnaireIds.size()) {
            int passLogicCount = 0;
            routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireIds.get(currentIndexQuestionID));
            if (routeModels != null && routeModels.size() > 0) {// check stop logic
                for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                    AnswerModel answerModel = answerModels.get(routeModels.get(i).getQuestionnaireID_Check_Option());
                    for (Map.Entry<Integer, String> entry : answerModel.getArrValue().entrySet()) {// duyet qua tac ca cac dap an da chon
                        boolean passLogic = false;
                        if (routeModels.get(i).getMethod() == 0 || routeModels.get(i).getMethod() == 2) {// OR
                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            for (String s : arrResponseValue) {// duyet qua tac ca cac response value
                                if (routeModels.get(i).getNextQuestionnaireID() > 0) {// Dieu kien thoa
                                    if (entry.getKey() == Integer.valueOf(s)) {
                                        passLogicCount++;
                                        passLogic = true;
                                        break;
                                    }
                                }
                            }
                        }

                        // duyet qua route khac khi route hien tai da thoa logic
                        if (passLogic)
                            break;
                    }
                }
                if (passLogicCount == routeModels.size())
                    return true;
            } else
                return true;
        } else
            return true;
        return false;
    }

    private String showMessageCompletedProject(AppMessageModel message) {
        switch (message.getCode()) {
            case "USER_RESPONSE_LOTTERYCODE":
            case "USER_RESPONSE_GIFT":
            case "OK":
                return message.getDescription();
            case "USER_RESPONSE_MARK":
                return String.format(getString(R.string.txt_completed_survey_mark), message.getResult());
        }
        return getString(R.string.txt_completed_survey);
    }

    // Get location
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                AlertDialog.Builder gpsAlertDialog = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                gpsAlertDialog.setCancelable(false);
                gpsAlertDialog.setTitle(getString(R.string.title_notice));

                // Show dialog open location in setting
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // In some cases, a matching Activity may not exist, so ensure you safeguard against this.
                // So, we must check packageManager & resolveInfo still exist
                PackageManager packageManager = getPackageManager();
                ResolveInfo resolveInfo = packageManager.resolveActivity(callGPSSettingIntent, PackageManager.GET_META_DATA);
                if (packageManager != null && resolveInfo != null) {

                    gpsAlertDialog.setMessage(getString(R.string.message_error_gps));
                    gpsAlertDialog.setPositiveButton(getString(R.string.button_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(callGPSSettingIntent, PackageManager.GET_META_DATA);
                        }
                    });
                    gpsAlertDialog.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProjectSurveyActivity.this.finish();
                        }
                    });
                    gpsAlertDialog.show();
                } else {
                    gpsAlertDialog.setMessage(getString(R.string.message_error_warning));
                    gpsAlertDialog.setPositiveButton(getString(R.string.button_ok), null);
                    gpsAlertDialog.show();
                }
            } else {
                if (isNetworkEnabled) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constant.MIN_TIME_BW_UPDATES, Constant.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled && location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.MIN_TIME_BW_UPDATES, Constant.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}