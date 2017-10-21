package com.nghiepnguyen.survey.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nghiepnguyen.survey.Interface.ApiInterface;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.application.MainApplication;
import com.nghiepnguyen.survey.model.LoginModel;
import com.nghiepnguyen.survey.storage.UserInfoManager;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar loadingWebview;
    private View mLoginFormView;

    @Inject
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((MainApplication) getApplication()).getNetComponent().inject(this);

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
                Call<LoginModel> userInfoModelCall = retrofit.create(ApiInterface.class).postLogin(email, password);
                userInfoModelCall.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        LoginModel loginModel = response.body();
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

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        loadingWebview = (ProgressBar) findViewById(R.id.loading_webview);

    }


}

