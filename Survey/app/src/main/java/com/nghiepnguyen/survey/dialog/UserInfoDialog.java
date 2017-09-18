package com.nghiepnguyen.survey.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.SaveAnswerModel;

/**
 * Created by nghiep on 6/3/16.
 */
public class UserInfoDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "CancelManagementDialog";
    private Context mContext;
    private ICallBackSaveUserInfo iCallBackSaveUserInfo;

    private View mDialogView;
    private TextView mFullNameTextView;
    private TextView mNumberIdTextView;
    private TextView mPhoneNumberTextView;
    private TextView mAddressTextView;
    private TextView mEmailTextView;
    private Button mCancelButton;
    private Button mOkButton;
    private Animation mAnimationIn;
    private Animation mAnimationOut;

    private SaveAnswerModel saveAnswerModel;

    public UserInfoDialog(Context context, ICallBackSaveUserInfo iCallBackSaveUserInfo) {
        super(context, android.R.style.Theme_Holo_Light_DarkActionBar);
        this.mContext = context;
        this.iCallBackSaveUserInfo = iCallBackSaveUserInfo;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.dialog_user_info);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.cl_float_transparent)));
        initComponent();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.9);
        FrameLayout.LayoutParams layoutPara = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutPara.gravity = Gravity.CENTER;
        mDialogView.setLayoutParams(layoutPara);

        isEnableAnimation();
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAnimationIn != null)
            mDialogView.startAnimation(mAnimationIn);

    }

    @Override
    public void dismiss() {
        if (mAnimationOut != null)
            mDialogView.startAnimation(mAnimationOut);
        else
            UserInfoDialog.super.dismiss();
    }

    private void initComponent() {
        mDialogView = findViewById(R.id.dialog_user_info_view);
        mFullNameTextView = (TextView) findViewById(R.id.dialog_user_info_full_name_edittext);
        mNumberIdTextView = (TextView) findViewById(R.id.dialog_user_info_number_id_edittext);
        mPhoneNumberTextView = (TextView) findViewById(R.id.dialog_user_info_phone_number_edittext);
        mAddressTextView = (TextView) findViewById(R.id.dialog_user_info_address_edittext);
        mEmailTextView = (TextView) findViewById(R.id.dialog_user_info_email_edittext);

        mCancelButton = (Button) findViewById(R.id.dialog_user_info_cancel_button);
        mOkButton = (Button) findViewById(R.id.dialog_user_info_ok_button);

        mNumberIdTextView.setText(String.valueOf(System.currentTimeMillis()));
        mEmailTextView.setText(String.valueOf(System.currentTimeMillis() + "@gmail.com"));
        mPhoneNumberTextView.setText(String.valueOf(System.currentTimeMillis()));
        mCancelButton.setOnClickListener(this);
        mOkButton.setOnClickListener(this);
    }

    private void isEnableAnimation() {
        mAnimationIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_dialog_in);
        mAnimationOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_dialog_out);
        mAnimationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        UserInfoDialog.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void setICallBack(ICallBackSaveUserInfo iCallBack) {
        this.iCallBackSaveUserInfo = iCallBack;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.dialog_user_info_cancel_button:
                UserInfoDialog.this.dismiss();
                ((Activity) mContext).finish();
                break;
            case R.id.dialog_user_info_ok_button:
                if (validateInformation()) {
                    saveAnswerModel = new SaveAnswerModel(
                            mFullNameTextView.getText().toString(),
                            mNumberIdTextView.getText().toString(),
                            mPhoneNumberTextView.getText().toString(),
                            mAddressTextView.getText().toString(),
                            mEmailTextView.getText().toString());

                    UserInfoDialog.this.dismiss();

                    if (iCallBackSaveUserInfo != null)
                        iCallBackSaveUserInfo.onSaveUserInfo(saveAnswerModel);

                }
                break;
        }
    }

    private boolean validateInformation() {
        if (TextUtils.isEmpty(mFullNameTextView.getText().toString())) {
            String message_error = mContext.getString(R.string.message_fullname_empty);
            mFullNameTextView.requestFocus();
            mFullNameTextView.setError(message_error);
            return false;
        }

        if (TextUtils.isEmpty(mAddressTextView.getText().toString())) {
            String message_error = mContext.getString(R.string.message_address_empty);
            mAddressTextView.requestFocus();
            mAddressTextView.setError(message_error);
            return false;
        }

        String email = mEmailTextView.getText().toString();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String message_error = mContext.getString(R.string.message_email_empty);
            mEmailTextView.requestFocus();
            mEmailTextView.setError(message_error);
            return false;
        }
        return true;
    }

    public interface ICallBackSaveUserInfo {
        public void onSaveUserInfo(SaveAnswerModel saveAnswerModel);
    }
}
