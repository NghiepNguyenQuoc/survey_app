package com.nghiepnguyen.survey.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.LoginModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar loadingWebview;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingWebview.setVisibility(View.VISIBLE);

                // Store values at the time of the login attempt.
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                /*SurveyApiWrapper.loginToServer(LoginActivity.this, email, password, new ICallBack() {
                    @Override
                    public void onSuccess(final Object data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfoModel user = new Gson().fromJson(data.toString(), UserInfoModel.class);
                                UserInfoManager.saveUserInfo(LoginActivity.this, user);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onFailure(final CommonErrorModel error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCompleted() {

                    }
                });*/

                SurveyApiWrapper.memberLogin(LoginActivity.this, email, password, new ICallBack() {
                    @Override
                    public void onSuccess(final Object data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoginModel loginModel = new Gson().fromJson(data.toString(), LoginModel.class);
                                if (!loginModel.isSuccessfull()) {
                                    loadingWebview.setVisibility(View.GONE);

                                    AlertDialog.Builder customBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
                                    customBuilder.setCancelable(false);
                                    customBuilder.setTitle(getResources().getString(R.string.title_confirm));
                                    customBuilder.setMessage(loginModel.getDescription());
                                    customBuilder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });

                                    customBuilder.show();
                                } else {
                                    UserInfoManager.saveMemberInfo(LoginActivity.this, loginModel.getMember());

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(final CommonErrorModel error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCompleted() {

                    }
                });

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        loadingWebview = (ProgressBar) findViewById(R.id.loading_webview);

    }


}

