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

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.UserInfoDialog;
import com.nghiepnguyen.survey.model.AnswerModel;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.SaveAnswerModel;
import com.nghiepnguyen.survey.model.SelectedOption;
import com.nghiepnguyen.survey.model.sqlite.AnswerSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.QuestionaireSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.RouteSQLiteHelper;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private SaveAnswerModel saveAnswerModel;
    private List<QuestionnaireModel> questionnaireModelList;
    private QuestionModel questionModel;
    private ProjectModel projectModel;

    private List<Integer> pathList;
    private List<Integer> questionnaireIds;
    private List<AnswerModel> answerModels;
    private int currentIndexQuestionID = 0;

    private AppCompatRadioButton mSelectedRB;// current RadioButton when user focus
    private int mSelectedPosition = -1;// current position in adapter

    private QuestionaireSQLiteHelper questionaireSQLiteHelper;
    private RouteSQLiteHelper routeSQLiteHelper;
    private AnswerSQLiteHelper answerSQLiteHelper;
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

        answerModels = new ArrayList<>();
        questionnaireIds = questionaireSQLiteHelper.getAllQuestionIDByProjectId(projectModel.getID());

        UserInfoDialog userInfoDialog = new UserInfoDialog(this, new UserInfoDialog.ICallBackSaveUserInfo() {
            @Override
            public void onSaveUserInfo(SaveAnswerModel saveAnswerModel) {
                ProjectSurveyActivity.this.saveAnswerModel = saveAnswerModel;
                getNextQuestion();
            }
        });
        userInfoDialog.show();
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
        customBuilder.show();
    }


    private void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_project_survey_toolbar);
        mQuestionContentTextView = (TextView) findViewById(R.id.activity_project_survey_question_content_textview);
        Button mNextQuestionButton = (Button) findViewById(R.id.activity_project_survey_next_question_button);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_project_survey_loading_progress_bar);
        mOptionLinearLayout = (LinearLayout) findViewById(R.id.activity_project_survey_option_linearlayout);

        setSupportActionBar(mToolbar);

        assert mNextQuestionButton != null;
        mNextQuestionButton.setOnClickListener(this);
        assert mToolbar != null;
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
        routeSQLiteHelper = new RouteSQLiteHelper(this);
        answerSQLiteHelper = new AnswerSQLiteHelper(this);
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
                // cap nhau tieu de cau khoi khi co ki tu [LIKED]
                generateTitleQuestion(questionnaireModels.get(0).getQuestionText(), answerModels, questionnaireIds, currentIndexQuestionID - 1, questionnaireModels.get(0).getCode(), mQuestionContentTextView);

                questionModel = new QuestionModel(Parcel.obtain());
                questionModel.setID(questionnaireModels.get(0).getID());
                questionModel.setQuestionText(questionnaireModels.get(0).getQuestionText());
                questionModel.setZOrder(questionnaireModels.get(0).getZOrderQuestion());
                questionModel.setCode(questionnaireModels.get(0).getCode());
                questionModel.setType(questionnaireModels.get(0).getType());
                questionModel.setMaxResponseCount(questionnaireModels.get(0).getMaxResponseCount());

                // get depend list option
                findQuestionnaireModelsByDependentID(questionnaireModels.get(0).getQuestionnaireID(),
                        questionnaireModels.get(0).getDependentID(), questionnaireModels.get(0).getDependentID(),
                        answerModels, questionModel, questionnaireModelList);

            } else {// get new questionnaireModelList
                for (int i = 0; i < questionnaireModels.size(); i++) {
                    if (i == 0) {
                        // cap nhau tieu de cau khoi khi co ki tu [LIKED]
                        generateTitleQuestion(questionnaireModels.get(0).getQuestionText(), answerModels, questionnaireIds, currentIndexQuestionID - 1, questionnaireModels.get(i).getCode(), mQuestionContentTextView);

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
            finishSurvey(true);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void findQuestionnaireModelsByDependentID(int rootQuestionnaireID, int rootDependentID, int dependentID,
                                                      List<AnswerModel> answerModels, QuestionModel questionModel,
                                                      List<QuestionnaireModel> questionnaireModelList) {

        List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(dependentID);
        if (questionnaireModels.get(0).getDependentID() != 0) {
            findQuestionnaireModelsByDependentID(rootQuestionnaireID, rootDependentID, questionnaireModels.get(0).getDependentID(), answerModels, questionModel, questionnaireModelList);
        }


        for (int i = 0; i < questionnaireModels.size(); i++) {
            AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, rootDependentID);// get answer from dependent question
            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {// duyet qua tac ca cac dap an da chon
                if (selectedOption.getValue() == questionnaireModels.get(i).getValue()) {
                    questionnaireModels.get(i).setQuestionnaireID(rootQuestionnaireID);
                    questionnaireModels.get(i).setType(questionModel.getType());
                    questionnaireModels.get(i).setIsSelected(0);
                    questionnaireModelList.add(questionnaireModels.get(i));
                }
            }
        }
    }

    private void generateTitleQuestion(String oldTitle, List<AnswerModel> answerModels, List<Integer> questionnaireIds, int preQuestionId, String questionCode, TextView textView) {
        if (oldTitle.contains("[LIKED]")) {
            AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, questionnaireIds.get(preQuestionId));// get answer from dependent question
            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {// duyet qua tac ca cac dap an da chon
                if (selectedOption.getAllowInputText() == 1)
                    oldTitle = oldTitle.replace("[LIKED]", selectedOption.getText());
                else
                    oldTitle = oldTitle.replace("[LIKED]", selectedOption.getOtherValue());
            }
        }

        Spannable wordtoSpan = new SpannableString(questionCode + ". " + oldTitle);
        wordtoSpan.setSpan(new RelativeSizeSpan(2f), 0, questionCode.length() + 1, 0); // set size
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cl_pink)), 0, questionCode.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(wordtoSpan);
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
                radioButton.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
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
                                assert editText != null;
                                editText.setVisibility(View.VISIBLE);
                                editText.requestFocus();
                            } else if (buttonView.getId() == item.getID() && !buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                assert editText != null;
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
                checkBox.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
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
                                assert editText != null;
                                editText.setVisibility(View.VISIBLE);
                                editText.requestFocus();
                            } else if (buttonView.getId() == item.getID() && !buttonView.isChecked() && item.getAllowInputText() == 1) {
                                editText = (AppCompatEditText) findViewById(buttonView.getId() * 10);
                                assert editText != null;
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_project_survey_next_question_button:

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                dialog.setTitle(getString(R.string.title_attention));
                dialog.setPositiveButton(getString(R.string.button_ok), null);

                List<SelectedOption> selectedOptions = new ArrayList<>();
                // collect data from view
                for (QuestionnaireModel item : questionnaireModelList) {
                    if (questionModel.getType() == 0 || questionModel.getType() == 2) {
                        RadioButton radioButon = (RadioButton) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(radioButon.isChecked() ? 1 : 0);

                        if (radioButon.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                            selectedOptions.add(new SelectedOption(item.getValue(), editText.getText().toString(), item.getDescription(), 1));
                        } else if (radioButon.isChecked()) {
                            selectedOptions.add(new SelectedOption(item.getValue(), "", item.getDescription(), 0));
                        }
                    } else {
                        CheckBox checkBox = (CheckBox) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(checkBox.isChecked() ? 1 : 0);


                        if (checkBox.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                            selectedOptions.add(new SelectedOption(item.getValue(), editText.getText().toString(), item.getDescription(), 1));
                        } else if (checkBox.isChecked()) {
                            selectedOptions.add(new SelectedOption(item.getValue(), "", item.getDescription(), 0));
                        }
                    }
                }

                // save to answer list
                answerModels.add(new AnswerModel(questionnaireModelList.get(0).getQuestionnaireID(), selectedOptions));

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
                    finishSurvey(false);
                } else {
                    switch (questionModel.getType()) {
                        case 0:
                        case 1:
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

    private boolean checkStopLogic(List<AnswerModel> answerModels, List<QuestionnaireModel> questionnaireList) {
        List<RouteModel> routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireList.get(0).getQuestionnaireID());
        if (routeModels != null && routeModels.size() > 0) {// check stop logic
            for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                if (routeModels.get(i).getNextQuestionnaireID() < 0) {// Dieu kien dung
                    if (routeModels.get(i).getMethod() == 0) {// OR
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            Integer[] arrTest = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrTest[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> set = new HashSet<>();
                            Collections.addAll(set, arrTest);

                            set.retainAll(setAnswer);
                            if (set.size() > 0)
                                return false;
                        }


                    } else if (routeModels.get(i).getMethod() == 2) {// OR NOT
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            setAnswer.retainAll(setResponseValueInteger);
                            if (setAnswer.size() > 0)
                                return true;
                            else
                                return false;
                        }
                    } else if (routeModels.get(i).getMethod() == 1) {// AND
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            setAnswer.retainAll(setResponseValueInteger);
                            if (setAnswer.containsAll(setResponseValueInteger))
                                return false;
                        }
                    } else if (routeModels.get(i).getMethod() == 3) {// AND NOT
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            if (!setResponseValueInteger.containsAll(setAnswer))
                                return false;
                        }
                    }
                }
            }
        }

        return checkNextLogic();
    }

    private boolean checkNextLogic() {
        // check next logic
        List<Boolean> arrResultLogic = new ArrayList<>();

        if (currentIndexQuestionID < questionnaireIds.size()) {
            List<RouteModel> routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireIds.get(currentIndexQuestionID));

            // init for arrResultLogic
            for (int i = 0; i < routeModels.size(); i++)
                arrResultLogic.add(false);

            if (routeModels != null && routeModels.size() > 0) {// check stop logic

                // 1.check question logic
                for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                    if (routeModels.get(i).getMethod() == 0) {// OR
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrTest = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrTest[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> set = new HashSet<>();
                            Collections.addAll(set, arrTest);
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            set.retainAll(setAnswer);
                            if (set.size() > 0)
                                arrResultLogic.set(i, true);
                        }
                    } else if (routeModels.get(i).getMethod() == 2) {// OR NOT
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            setAnswer.retainAll(setResponseValueInteger);
                            if (setAnswer.size() > 0)
                                return false;
                            else
                                arrResultLogic.set(i, true);
                        }
                    } else if (routeModels.get(i).getMethod() == 1) {// AND
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            setAnswer.retainAll(setResponseValueInteger);
                            if (setAnswer.containsAll(setResponseValueInteger))
                                arrResultLogic.set(i, true);
                        }
                    } else if (routeModels.get(i).getMethod() == 3) {// AND NOT
                        AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                        if (answerModel != null) {
                            List<Integer> setAnswer = new ArrayList<>();
                            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                                setAnswer.add(selectedOption.getValue());
                            }

                            String[] arrResponseValue = routeModels.get(i).getResponseValue().trim().split(",");
                            Integer[] arrResponseValueInteger = new Integer[arrResponseValue.length];
                            for (int k = 0; k < arrResponseValue.length; k++)
                                arrResponseValueInteger[k] = Integer.parseInt(arrResponseValue[k]);

                            Set<Integer> setResponseValueInteger = new HashSet<>();
                            Collections.addAll(setResponseValueInteger, arrResponseValueInteger);

                            setAnswer.retainAll(setResponseValueInteger);
                            if (setAnswer.containsAll(setResponseValueInteger))
                                return false;
                            else
                                arrResultLogic.set(i, true);
                        }
                    }
                }
                if (arrResultLogic.size() == routeModels.size()) {
                    if (arrResultLogic.indexOf(false) == -1)
                        return true;
                }

                // 2.check group logic
                int countQuestionOfGroup = 0;
                for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                    if (routeModels.get(0).getQuestionnaireConditionsID() == routeModels.get(i).getQuestionnaireConditionsID())
                        countQuestionOfGroup++;
                }
                if (countQuestionOfGroup > 1) {
                    List<Boolean> arResultGroupLogic = new ArrayList<>();
                    for (int i = 0; i < arrResultLogic.size() - 1; i++) {

                        // GROUP OR, Only 1 chilđ true, return true.
                        if (routeModels.get(i + 1).getGroupMethod() == 0 && (arrResultLogic.get(i) || arrResultLogic.get(i + 1))) {
                            return true;
                        } else if (routeModels.get(i + 1).getGroupMethod() == 1 && (arrResultLogic.get(i) && arrResultLogic.get(i + 1))) {
                            // GROUP AND, [A,B], [B,C]
                            arResultGroupLogic.add(true);
                        } else {
                            arResultGroupLogic.add(false);
                        }
                    }

                    if (arResultGroupLogic.indexOf(false) == -1)
                        return true;
                }
            } else
                return true;
        } else
            return true;
        currentIndexQuestionID++;
        return checkNextLogic();
    }


    private AnswerModel getAnswerByQuestionaireId(List<AnswerModel> answerModels, int questionaireId) {
        for (AnswerModel answerModel : answerModels) {
            if (answerModel.getQuestionaireID() == questionaireId)
                return answerModel;
        }
        return null;
    }

    private void finishSurvey(boolean isCompeleted) {
        /////////////////////////////////////////////////////////////////
        // collect data to send to server
        String patternString = "<R QID=\'%s\' V=\'%s\' T=\'%s\'/>";
        String resultData = "";
        for (AnswerModel answerModel : answerModels) {
            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                if (selectedOption.getAllowInputText() == 1)
                    resultData += String.format(patternString, answerModel.getQuestionaireID(), selectedOption.getValue(), selectedOption.getOtherValue());
                else
                    resultData += String.format(patternString, answerModel.getQuestionaireID(), selectedOption.getValue(), selectedOption.getText());
            }
        }

        Log.e("RESULT", resultData);

        saveAnswerModel.setIsCompeleted(isCompeleted ? 1 : 0);
        saveAnswerModel.setProjectID(projectModel.getID());
        saveAnswerModel.setData(resultData);
        answerSQLiteHelper.addAnswer(saveAnswerModel);


        // show alert
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
        customBuilder.setCancelable(false);
        customBuilder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(RESULT_OK);
                ProjectSurveyActivity.this.finish();
            }
        });
        if (isCompeleted) {
            customBuilder.setTitle(getString(R.string.title_notice));
            customBuilder.setMessage(getString(R.string.txt_completed_survey));
        } else {
            customBuilder.setTitle(getString(R.string.title_notice));
            customBuilder.setMessage(getString(R.string.txt_not_suitable_option));
        }
        customBuilder.show();
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
                if (resolveInfo != null) {

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