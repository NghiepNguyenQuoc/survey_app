package com.nghiepnguyen.survey.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.republicofgavin.pauseresumeaudiorecorder.PauseResumeAudioRecorder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.nghiepnguyen.survey.utils.TimestampUtils;
import com.nghiepnguyen.survey.utils.Utils;

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
 * -5 Matrix SA
 * -6 CheckBok,Arrangements
 * -7 CheckBok
 * - 8 CheckSum
 * -9 Matrix MA
 * - 10 Matrix Column
 */
public class ProjectSurveyActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static String TAG = "ProjectSurveyActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 0x1;
    private final static int REQUEST_CODE_LOCATION_SETTING = 100;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastUpdatedLocation;
    private AlertDialog gpsAlert;

    // Record Audio
    private int isRecording = 0;
    private PauseResumeAudioRecorder mediaRecorder;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


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
                checkGPS();
            }
        });
        userInfoDialog.show();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupLocationClient();
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
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null && (mediaRecorder.getCurrentState() == PauseResumeAudioRecorder.RECORDING_STATE ||
                mediaRecorder.getCurrentState() == PauseResumeAudioRecorder.PAUSED_STATE)) {
            mediaRecorder.stopRecording();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                boolean userAllowed = true;
                for (final int result : grantResults) {
                    userAllowed &= result == PackageManager.PERMISSION_GRANTED;
                }
                if (userAllowed) {
                    startRecordAudio();
                } else {
                    showMessage(getString(R.string.message_no_permissions));
                }
                break;
            default:
                break;
        }
    }


    /**
     * init vivew
     */
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

    /**
     * get data from Intent or Storage
     */
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

    /**
     * check GPS
     */
    private void checkGPS() {
        List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(questionnaireIds.get(currentIndexQuestionID));
        if (questionnaireModels.get(0).getFlagGPS() == 1) {
            AlertDialog.Builder requireGpsAlertDialog = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
            requireGpsAlertDialog.setCancelable(false);
            requireGpsAlertDialog.setTitle(getString(R.string.title_notice));
            requireGpsAlertDialog.setMessage(getString(R.string.message_request_gps));
            requireGpsAlertDialog.setPositiveButton(getString(R.string.button_checking), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getLocation();
                }
            });
            requireGpsAlertDialog.setNegativeButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            requireGpsAlertDialog.show();
        } else {
            getNextQuestion();
        }
    }

    private void getNextQuestion() {
        mProgressBar.setVisibility(View.VISIBLE);

        if (currentIndexQuestionID < questionnaireIds.size()) {
            // clear questionnaireModelList
            if (questionnaireModelList == null)
                questionnaireModelList = new ArrayList<>();
            questionnaireModelList.clear();

            // hide keyboard
            Utils.hideSoftKeyBoard(this);


            // get all option and question
            List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(questionnaireIds.get(currentIndexQuestionID));

            if (questionnaireModels.get(0).getFlagQueRecord() == 1) {
                tryStartRecordAudio();
            } else {
                isRecording = -1;
                if(mediaRecorder != null)
                    mediaRecorder.pauseRecording();
                saveAnswerModel.setEndRecordingTime(TimestampUtils.getDate(Constant.FORMAT_24_HOURS_DAY_SHORT, System.currentTimeMillis(), ProjectSurveyActivity.this));
            }

            if (questionnaireModels.get(0).getParentID() == 0) {
                if (questionnaireModels.get(0).getDependentID() != 0) {
                    // cap nhau tieu de cau khoi khi co ki tu [LIKED]
                    generateTitleQuestion(questionnaireModels.get(0).getQuestionText(), answerModels, questionnaireIds, currentIndexQuestionID - 1, questionnaireModels.get(0).getCode(), mQuestionContentTextView);

                    // Question information
                    questionModel = new QuestionModel(Parcel.obtain());
                    questionModel.setID(questionnaireModels.get(0).getID());
                    questionModel.setQuestionText(questionnaireModels.get(0).getQuestionText());
                    questionModel.setZOrder(questionnaireModels.get(0).getZOrderQuestion());
                    questionModel.setCode(questionnaireModels.get(0).getCode());
                    questionModel.setType(questionnaireModels.get(0).getType());
                    questionModel.setMaxResponseCount(questionnaireModels.get(0).getMaxResponseCount());


                    // options information
                    // get depend list option
                    if (questionnaireModels.get(0).getDependentType() == 0) {
                        findQuestionnaireModelsByDependentID(questionnaireModels.get(0).getQuestionnaireID(),
                                questionnaireModels.get(0).getDependentID(), questionnaireModels.get(0).getDependentID(),
                                answerModels, questionModel, questionnaireModelList);
                    } else {
                        findQuestionnaireModelsByDependentIDAndExclusion(questionnaireModels.get(0).getQuestionnaireID(),
                                questionnaireModels.get(0).getDependentID(), questionnaireModels.get(0).getDependentID(),
                                answerModels, questionModel, questionnaireModelList);
                    }

                } else {// get new questionnaireModelList
                    for (int i = 0; i < questionnaireModels.size(); i++) {
                        if (i == 0) {
                            // cap nhau tieu de cau khoi khi co ki tu [LIKED]
                            generateTitleQuestion(questionnaireModels.get(0).getQuestionText(), answerModels, questionnaireIds, currentIndexQuestionID - 1, questionnaireModels.get(i).getCode(), mQuestionContentTextView);

                            // Question information
                            questionModel = new QuestionModel(Parcel.obtain());
                            questionModel.setID(questionnaireModels.get(i).getID());
                            questionModel.setQuestionText(questionnaireModels.get(i).getQuestionText());
                            questionModel.setZOrder(questionnaireModels.get(i).getZOrderQuestion());
                            questionModel.setCode(questionnaireModels.get(i).getCode());
                            questionModel.setType(questionnaireModels.get(i).getType());
                            questionModel.setMaxResponseCount(questionnaireModels.get(i).getMaxResponseCount());
                        }

                        // options information
                        questionnaireModelList.add(questionnaireModels.get(i));
                    }
                }

                // generate options
                if (questionnaireModelList != null && questionnaireModelList.size() > 0)
                    generateOption(mOptionLinearLayout, questionnaireModelList);

                // move cursor to next question
                currentIndexQuestionID++;
            } else {
                finishSurvey(true);
            }
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

    /**
     * get dependent questionaire
     */
    private void findQuestionnaireModelsByDependentID(int rootQuestionnaireID, int rootDependentID, int dependentID,
                                                      List<AnswerModel> answerModels, QuestionModel questionModel,
                                                      List<QuestionnaireModel> questionnaireModelList) {

        List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(dependentID);
        if (questionnaireModels.get(0).getDependentID() != 0) {
            findQuestionnaireModelsByDependentID(rootQuestionnaireID, rootDependentID, questionnaireModels.get(0).getDependentID(), answerModels, questionModel, questionnaireModelList);
        }

        for (int i = 0; i < questionnaireModels.size(); i++) {
            AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, rootDependentID);// get answer from dependent question
            assert answerModel != null;
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

    /**
     * get dependent questionaire
     */
    private void findQuestionnaireModelsByDependentIDAndExclusion(int rootQuestionnaireID, int rootDependentID, int dependentID,
                                                                  List<AnswerModel> answerModels, QuestionModel questionModel,
                                                                  List<QuestionnaireModel> questionnaireModelList) {

        // get all questionaire of root question
        List<QuestionnaireModel> questionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByQuestionId(dependentID);
        if (questionnaireModels.get(0).getDependentID() != 0) {
            findQuestionnaireModelsByDependentIDAndExclusion(rootQuestionnaireID, rootDependentID, questionnaireModels.get(0).getDependentID(), answerModels, questionModel, questionnaireModelList);
        }

        if (questionnaireModelList.size() == 0) {
            //List<Integer> arr1 = new ArrayList<>();
            Integer[] arr1 = new Integer[questionnaireModels.size()];
            for (int i = 0; i < questionnaireModels.size(); i++) {
                arr1[i] = (questionnaireModels.get(i).getValue());
            }

            AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, rootDependentID);// get answer from dependent question
            assert answerModel != null;
            //Integer[] arr2 = new Integer[answerModel.getSelectedOptions().size()];
            List<Integer> arr2 = new ArrayList<>();
            for (int j = 0; j < answerModel.getSelectedOptions().size(); j++) {
                arr2.add(answerModel.getSelectedOptions().get(j).getValue());
            }

            Set<Integer> set = new HashSet<>();
            Collections.addAll(set, arr1);
            set.retainAll(arr2);

            for (int i = 0; i < questionnaireModels.size(); i++) {
                if (!set.contains(questionnaireModels.get(i).getValue())) {
                    questionnaireModels.get(i).setQuestionnaireID(rootQuestionnaireID);
                    questionnaireModels.get(i).setType(questionModel.getType());
                    questionnaireModels.get(i).setIsSelected(0);
                    questionnaireModelList.add(questionnaireModels.get(i));
                }
            }
        }
    }

    /**
     * generate title question
     */
    private void generateTitleQuestion(String oldTitle, List<AnswerModel> answerModels, List<Integer> questionnaireIds, int preQuestionId, String questionCode, TextView textView) {
        if (oldTitle.contains("[LIKED]")) {
            AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, questionnaireIds.get(preQuestionId));// get answer from dependent question
            assert answerModel != null;
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

    /**
     * generate options
     */
    private void generateOption(LinearLayout mainView, final List<QuestionnaireModel> questionnaireList) {
        LinearLayout linearLayout1 = new LinearLayout(ProjectSurveyActivity.this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);

        // add para to radio group
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout1.setLayoutParams(params);

        if (questionnaireList.get(0).getDisplayRandomResponse() == 1)
            Collections.shuffle(questionnaireList);
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

            } else if (item.getType() == 5 || item.getType() == 9) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels / (questionnaireList.size() + 1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);

                List<QuestionnaireModel> childQuestionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByParentQuestionId(questionnaireList.get(0).getQuestionnaireID());
                for (int i = 0; i < childQuestionnaireModels.size() + 1; i++) {
                    LinearLayout horizontalLinearLayout = new LinearLayout(this);
                    horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    if (i == 0) {
                        for (int j = 0; j < questionnaireList.size() + 1; j++) {
                            TextView textView = new TextView(this);
                            textView.setLayoutParams(layoutParams);
                            textView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
                            if (j != 0) {
                                textView.setText(questionnaireList.get(j - 1).getDescription());
                            }
                            horizontalLinearLayout.addView(textView);
                        }
                    } else {
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                        radioGroup.setId(childQuestionnaireModels.get(i - 1).getQuestionnaireID());
                        radioGroup.setPadding(Constant.dpToPx(0, this), Constant.dpToPx(5, this), Constant.dpToPx(0, this), Constant.dpToPx(5, this));
                        for (int j = 0; j < questionnaireList.size() + 1; j++) {
                            if (j == 0) {
                                TextView textView = new TextView(this);
                                textView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
                                textView.setText(childQuestionnaireModels.get(i - 1).getQuestionText());
                                textView.setLayoutParams(layoutParams);
                                radioGroup.addView(textView);
                            } else {
                                AppCompatRadioButton radioButton = new AppCompatRadioButton(this);
                                radioButton.setId(childQuestionnaireModels.get(i - 1).getQuestionnaireID() * 100 + questionnaireList.get(j - 1).getValue());
                                radioButton.setLayoutParams(layoutParams);
                                radioGroup.addView(radioButton);
                            }
                        }
                        linearLayout1.addView(radioGroup);
                    }

                    linearLayout1.addView(horizontalLinearLayout);

                }
                break;
            } else if (item.getType() == 10) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels / (questionnaireList.size() + 1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);

                LinearLayout horizontalLayout = new LinearLayout(this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                List<QuestionnaireModel> childQuestionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByParentQuestionId(questionnaireList.get(0).getQuestionnaireID());
                for (int i = 0; i < questionnaireList.size() + 1; i++) {
                    LinearLayout verticalLinearLayout = new LinearLayout(this);
                    verticalLinearLayout.setOrientation(LinearLayout.VERTICAL);

                    if (i == 0) {
                        for (int j = 0; j < childQuestionnaireModels.size() + 1; j++) {
                            TextView textView = new TextView(this);
                            textView.setLayoutParams(layoutParams);
                            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.cl_default_trans));
                            textView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
                            if (j != 0) {
                                textView.setText(childQuestionnaireModels.get(j - 1).getQuestionText());
                            }
                            verticalLinearLayout.addView(textView);
                        }
                    } else {
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setOrientation(RadioGroup.VERTICAL);
                        radioGroup.setId(questionnaireList.get(i - 1).getQuestionnaireID());
                        for (int j = 0; j < childQuestionnaireModels.size() + 1; j++) {
                            if (j == 0) {
                                TextView textView = new TextView(this);
                                textView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
                                textView.setText(" ".concat(questionnaireList.get(i - 1).getDescription()));
                                textView.setLayoutParams(layoutParams);
                                radioGroup.addView(textView);
                            } else {
                                AppCompatRadioButton radioButton = new AppCompatRadioButton(this);
                                radioButton.setId(childQuestionnaireModels.get(j - 1).getQuestionnaireID() * 100 + questionnaireList.get(i - 1).getValue());
                                radioButton.setLayoutParams(layoutParams);
                                radioButton.setBackgroundColor(ContextCompat.getColor(this, R.color.cl_bg_upcoming));
                                radioGroup.addView(radioButton);
                            }
                        }
                        horizontalLayout.addView(radioGroup);
                    }

                    horizontalLayout.addView(verticalLinearLayout);

                }
                linearLayout1.addView(horizontalLayout);
                break;
            } else if (item.getType() == 6) {
                // create checkbox
                final AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
                checkBox.setId(item.getID());
                checkBox.setText(item.getDescription());
                checkBox.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
                checkBox.setPadding(0, Constant.dpToPx(10, this), 0, Constant.dpToPx(10, this));

                TextView textView = new TextView(this);
                textView.setTextColor(ContextCompat.getColor(this, R.color.red));
                textView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
                textView.setId(item.getID() * 10);
                LinearLayout horizontalLayout = new LinearLayout(this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.addView(checkBox);
                horizontalLayout.addView(textView);

                // create radio button
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    horizontalLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_group_divider));

                // add it to radio group
                linearLayout1.addView(horizontalLayout, params);

                horizontalLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBox.performClick();
                    }
                });
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundView, boolean isChecked) {
                        int maxvalue = 0;
                        int uncheckValue = 0;
                        for (QuestionnaireModel item : questionnaireList) {
                            // set allow input
                            TextView textview = (TextView) findViewById(item.getID() * 10);
                            assert textview != null;
                            if (!TextUtils.isEmpty(textview.getText().toString())) {
                                if (Integer.valueOf(textview.getText().toString()) > maxvalue)
                                    maxvalue = Integer.valueOf(textview.getText().toString());

                                if (!compoundView.isChecked()) {
                                    if (compoundView.getId() == item.getID())
                                        uncheckValue = Integer.valueOf(textview.getText().toString());
                                }
                            }
                        }

                        for (QuestionnaireModel item : questionnaireList) {
                            // set allow input
                            TextView textview = (TextView) findViewById(item.getID() * 10);
                            assert textview != null;
                            if (compoundView.isChecked()) {
                                if (compoundView.getId() == item.getID()) {
                                    textview.setText(String.valueOf(maxvalue + 1));
                                    break;
                                }
                            } else if (compoundView.getId() == item.getID() && !compoundView.isChecked()) {
                                textview.setText("");
                            } else if (!compoundView.isChecked()) {
                                int value = 0;
                                if (!TextUtils.isEmpty(textview.getText().toString()))
                                    value = Integer.valueOf(textview.getText().toString());
                                if (uncheckValue < value)
                                    textview.setText(value > 1 ? String.valueOf(value - 1) : "");
                            }
                        }
                    }
                });
            } else if (item.getType() == 3) {
                // create radio button
                AppCompatEditText editText = new AppCompatEditText(this);
                editText.setId(item.getID());
                editText.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
                editText.setPadding(Constant.dpToPx(10, this), Constant.dpToPx(10, this), Constant.dpToPx(10, this), Constant.dpToPx(10, this));

                // create radio button
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    editText.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_group_divider));

                // add it to radio group
                linearLayout1.addView(editText, params);
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
                    } else if (questionModel.getType() == 1) {
                        CheckBox checkBox = (CheckBox) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(checkBox.isChecked() ? 1 : 0);


                        if (checkBox.isChecked() && item.getAllowInputText() == 1) {
                            EditText editText = (EditText) mOptionLinearLayout.findViewById(item.getID() * 10);
                            item.setOtherOption(editText.getText().toString());
                            selectedOptions.add(new SelectedOption(item.getValue(), editText.getText().toString(), item.getDescription(), 1));
                        } else if (checkBox.isChecked()) {
                            selectedOptions.add(new SelectedOption(item.getValue(), "", item.getDescription(), 0));
                        }
                    } else if (questionModel.getType() == 5 || questionModel.getType() == 9) {
                        List<AnswerModel> answerModels = new ArrayList<>();
                        List<QuestionnaireModel> childQuestionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByParentQuestionId(item.getQuestionnaireID());
                        for (int i = 0; i < childQuestionnaireModels.size(); i++) {
                            List<SelectedOption> childSelectedOptions = new ArrayList<>();
                            RadioGroup radioGroup = (RadioGroup) mOptionLinearLayout.findViewById(childQuestionnaireModels.get(i).getQuestionnaireID());
                            int selectedValue = radioGroup.getCheckedRadioButtonId() % 100;
                            childSelectedOptions.add(new SelectedOption(selectedValue, "", childQuestionnaireModels.get(i).getQuestionText(), 0));
                            answerModels.add(new AnswerModel(childQuestionnaireModels.get(i).getQuestionnaireID(), childSelectedOptions));
                        }
                        selectedOptions.add(new SelectedOption(answerModels));
                        break;
                    } else if (questionModel.getType() == 10) {
                        List<AnswerModel> answerModels = new ArrayList<>();
                        List<QuestionnaireModel> childQuestionnaireModels = questionaireSQLiteHelper.getListQuestionnaireByParentQuestionId(item.getQuestionnaireID());
                        for (int i = 0; i < childQuestionnaireModels.size(); i++) {
                            List<SelectedOption> childSelectedOptions = new ArrayList<>();
                            RadioGroup radioGroup = (RadioGroup) mOptionLinearLayout.findViewById(childQuestionnaireModels.get(i).getQuestionnaireID());
                            int selectedValue = radioGroup.getCheckedRadioButtonId() % 100;
                            childSelectedOptions.add(new SelectedOption(selectedValue, "", childQuestionnaireModels.get(i).getQuestionText(), 0));
                            answerModels.add(new AnswerModel(childQuestionnaireModels.get(i).getQuestionnaireID(), childSelectedOptions));
                        }
                        selectedOptions.add(new SelectedOption(answerModels));
                        break;
                    } else if (questionModel.getType() == 6) {
                        CheckBox checkBox = (CheckBox) mOptionLinearLayout.findViewById(item.getID());
                        item.setIsSelected(checkBox.isChecked() ? 1 : 0);
                        if (checkBox.isChecked()) {
                            TextView textView = (TextView) mOptionLinearLayout.findViewById(item.getID() * 10);
                            selectedOptions.add(new SelectedOption(item.getValue(), "", textView.getText().toString(), 0));
                        }
                    } else if (questionModel.getType() == 3) {
                        // create radio button
                        item.setIsSelected(1);
                        AppCompatEditText editText = (AppCompatEditText) mOptionLinearLayout.findViewById(item.getID());
                        selectedOptions.add(new SelectedOption(item.getValue(), "", editText.getText().toString(), 0));
                    }
                }

                // save to answer list
                int index = -1;
                for (int i = 0; i < answerModels.size(); i++) {
                    if (answerModels.get(i).getQuestionaireID() == questionnaireModelList.get(0).getQuestionnaireID()) {
                        index = i;
                    }
                }
                if (index == -1)
                    answerModels.add(new AnswerModel(questionnaireModelList.get(0).getQuestionnaireID(), selectedOptions));
                else
                    answerModels.set(index, new AnswerModel(questionnaireModelList.get(0).getQuestionnaireID(), selectedOptions));

                //////////////////////////////////////////////////////////////////
                //check maxAnswer
                boolean isMaxAnswer = checkMaxMaxResponse(questionModel.getType(), questionModel.getMaxResponseCount(), questionnaireModelList);
                if (!isMaxAnswer) {
                    dialog.setMessage(String.format(getString(R.string.txt_over_max_answer), questionModel.getMaxResponseCount()));
                    dialog.show();
                    return;
                }

                //check emty
                boolean isEmtyAnswer = checkEmtyAnswer(questionModel.getType(), questionnaireModelList, answerModels);
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

                // check MA logic
                int isMALogicAnswer = checkMALogicAnswer(questionModel.getType(), answerModels);
                if (isMALogicAnswer != 0) {
                    dialog.setMessage(getString(R.string.txt_ma_logic, isMALogicAnswer));
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
                        case 2:
                        case 3:
                        case 5:
                        case 6:
                        case 9:
                        case 10:
                            pathList.add(questionModel.getID());
                            getNextQuestion();
                            break;
                    }
                }
                break;
        }
    }

    private boolean checkEmtyAnswer(int typeQuesion, List<QuestionnaireModel> questionnaireList, List<AnswerModel> answerModels) {
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
                for (QuestionnaireModel item : questionnaireList) {
                    AppCompatEditText editText = (AppCompatEditText) mOptionLinearLayout.findViewById(item.getID());
                    if (!TextUtils.isEmpty(editText.getText().toString()))
                        return true;
                }
                break;
            case 5:
            case 9:
                for (AnswerModel answerModel : answerModels) {
                    if (answerModel.getSelectedOptions().get(0).getAnswerModelList() != null && answerModel.getSelectedOptions().get(0).getAnswerModelList().size() > 0) {
                        for (AnswerModel model : answerModel.getSelectedOptions().get(0).getAnswerModelList()) {
                            if (model.getSelectedOptions().get(0).getValue() != -1)
                                return true;
                        }
                    }
                }
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

    private int checkMALogicAnswer(int typeQuesion, List<AnswerModel> answerModels) {
        switch (typeQuesion) {
            case 5:
                for (AnswerModel answerModel : answerModels) {
                    if (answerModel.getSelectedOptions().get(0).getAnswerModelList() != null && answerModel.getSelectedOptions().get(0).getAnswerModelList().size() > 0) {
                        int count = 0;
                        for (AnswerModel model : answerModel.getSelectedOptions().get(0).getAnswerModelList()) {
                            if (model.getSelectedOptions().get(0).getValue() != -1)
                                count++;
                        }
                        if (count != answerModel.getSelectedOptions().get(0).getAnswerModelList().size())
                            return answerModel.getSelectedOptions().get(0).getAnswerModelList().size();
                    }
                }

                break;
        }
        return 0;
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

    /**
     * check stop logic
     */
    private boolean checkStopLogic(List<AnswerModel> answerModels, List<QuestionnaireModel> questionnaireList) {
        List<RouteModel> routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireList.get(0).getQuestionnaireID());

        // Do not need to check logic when current questionare doesn't have any route
        if (routeModels.size() > 0) {// check stop logic
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
                            return setAnswer.size() > 0;
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

    /**
     * check next logic
     */
    private boolean checkNextLogic() {
        // check next logic
        List<Boolean> arrResultLogic = new ArrayList<>();

        if (currentIndexQuestionID < questionnaireIds.size()) {
            List<RouteModel> routeModels = routeSQLiteHelper.getRoutesByQuestionaireId(questionnaireIds.get(currentIndexQuestionID));

            // init for arrResultLogic
            for (int i = 0; i < routeModels.size(); i++)
                arrResultLogic.add(false);

            if (routeModels.size() > 0) {// check stop logic

                // 1.check question logic
                for (int i = 0; i < routeModels.size(); i++) {// duyet qua tat ca cac route
                    AnswerModel answerModel = getAnswerByQuestionaireId(answerModels, routeModels.get(i).getQuestionnaireID_Check_Option());
                    if (answerModel != null) {
                        if (routeModels.get(i).getMethod() == 0) {// OR
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
                        } else if (routeModels.get(i).getMethod() == 2) {// OR NOT
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
                        } else if (routeModels.get(i).getMethod() == 1) {// AND
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
                        } else if (routeModels.get(i).getMethod() == 3) {// AND NOT
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
                    } else
                        arrResultLogic.set(i, true);
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

    private void finishSurvey(final boolean isCompeleted) {
        // stop recording when finish
        if (isRecording == 1) {
            isRecording = -1;
            mediaRecorder.pauseRecording();
            saveAnswerModel.setEndRecordingTime(TimestampUtils.getDate(Constant.FORMAT_24_HOURS_DAY_SHORT, System.currentTimeMillis(), ProjectSurveyActivity.this));
        }

        /////////////////////////////////////////////////////////////////
        // collect data to send to server
        String patternString = "<R QID=\'%s\' V=\'%s\' T=\'%s\'/>";
        String resultData = "";

        for (AnswerModel answerModel : answerModels) {
            for (SelectedOption selectedOption : answerModel.getSelectedOptions()) {
                if (selectedOption.getAnswerModelList() != null) {
                    for (AnswerModel model : selectedOption.getAnswerModelList()) {
                        model.getSelectedOptions().get(0).getValue();
                        resultData += String.format(patternString, model.getQuestionaireID(), model.getSelectedOptions().get(0).getValue(), model.getSelectedOptions().get(0).getText());
                    }
                    break;
                } else {
                    if (selectedOption.getAllowInputText() == 1)
                        resultData += String.format(patternString, answerModel.getQuestionaireID(), selectedOption.getValue(), selectedOption.getOtherValue());
                    else
                        resultData += String.format(patternString, answerModel.getQuestionaireID(), selectedOption.getValue(), selectedOption.getText());
                }
            }
        }

        Log.e("RESULT", resultData);

        saveAnswerModel.setIsCompeleted(isCompeleted ? 1 : 0);
        saveAnswerModel.setProjectID(projectModel.getID());
        saveAnswerModel.setData(resultData);
        if (isGPSEnabled && mLastUpdatedLocation != null) {
            if ((mLastUpdatedLocation.getLatitude() == 0 || mLastUpdatedLocation.getLongitude() == 0)) {
                AlertDialog.Builder gpsAlertBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
                gpsAlertBuilder.setTitle(getString(R.string.title_attention));
                gpsAlertBuilder.setMessage(getString(R.string.message_not_detected_gps_error_warning));
                gpsAlertBuilder.setPositiveButton(getString(R.string.title_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishSurvey(isCompeleted);
                    }
                });
                gpsAlertBuilder.setCancelable(false);
                gpsAlert = gpsAlertBuilder.create();

                if (!gpsAlert.isShowing()) {
                    //if its visibility is not showing then show here
                    gpsAlert.show();
                }
                return;
            } else {
                saveAnswerModel.setGeoLatitude(mLastUpdatedLocation.getLatitude());
                saveAnswerModel.setGeoLongitude(mLastUpdatedLocation.getLongitude());
                saveAnswerModel.setGeoTime(TimestampUtils.getDate(Constant.FORMAT_24_HOURS_DAY, System.currentTimeMillis(), ProjectSurveyActivity.this));
            }
        }

        answerSQLiteHelper.addAnswer(saveAnswerModel);
        showAlertFinishSurvey(isCompeleted);
    }

    private void showAlertFinishSurvey(boolean isCompeleted) {
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

    /**
     * Get location
     */
    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
                    gpsAlertDialog.setMessage(getString(R.string.message_open_location_setting));
                    gpsAlertDialog.setPositiveButton(getString(R.string.button_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(callGPSSettingIntent, REQUEST_CODE_LOCATION_SETTING);
                        }
                    });
                    gpsAlertDialog.show();
                } else {
                    gpsAlertDialog.setMessage(getString(R.string.message_error_warning));
                    gpsAlertDialog.setPositiveButton(getString(R.string.button_ok), null);
                    gpsAlertDialog.show();
                }
            } else {
                isGPSEnabled = true;
                getNextQuestion();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTING) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                getLocation();
            } else {
                isGPSEnabled = true;
                getNextQuestion();
            }
        }
    }

    // We need to setup location client to update current location
    private void setupLocationClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Log.d(TAG, "mGoogleApiClient.isConnected() = false");

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "mGoogleApiClient.isConnected() = false");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }

        if ((location.getLatitude() == 0 || location.getLongitude() == 0)) {
            AlertDialog.Builder gpsAlertBuilder = new AlertDialog.Builder(ProjectSurveyActivity.this, R.style.AppCompatAlertDialogStyle);
            gpsAlertBuilder.setTitle(getString(R.string.title_attention));
            gpsAlertBuilder.setMessage(getString(R.string.message_not_detected_gps_error_warning));
            gpsAlertBuilder.setPositiveButton(getString(R.string.title_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            gpsAlertBuilder.setCancelable(false);
            gpsAlert = gpsAlertBuilder.create();

            if (!gpsAlert.isShowing()) {
                //if its visibility is not showing then show here
                gpsAlert.show();
            }
        } else {
            mLastUpdatedLocation = location;
        }
    }

    private void showMessage(String message) {
        final View root = findViewById(R.id.activity_project_main_view);
        if (root != null) {
            final Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void tryStartRecordAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int checkAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            final int checkStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkAudio != PackageManager.PERMISSION_GRANTED || checkStorage != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    showMessage(getString(R.string.message_no_permissions));
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessage(getString(R.string.message_no_permissions));
                } else {
                    requestPermissions(new String[]{
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSIONS);
                }
            } else {
                startRecordAudio();
            }
        } else {
            startRecordAudio();
        }
    }

    private void startRecordAudio() {
        if (isRecording == 0) {
            // save start recording time
            String startRecordingTime = TimestampUtils.getDate(Constant.FORMAT_24_HOURS_DAY_SHORT, System.currentTimeMillis(), ProjectSurveyActivity.this);
            saveAnswerModel.setStartRecordingTime(startRecordingTime);

            String fileName = projectModel.getID() + "_" + startRecordingTime + "_" + saveAnswerModel.getFullName();
            String mOutputFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
                    /*
                    isRecording=0: start recording
                    isRecording=1: recording
                    isRecording=-1: pause
                    */
            mediaRecorder = new PauseResumeAudioRecorder();
            mediaRecorder.setAudioFile(mOutputFileName);
            mediaRecorder.startRecording();
            isRecording = 1;
        } else if (isRecording == -1) {
            mediaRecorder.resumeRecording();
        }
    }
}