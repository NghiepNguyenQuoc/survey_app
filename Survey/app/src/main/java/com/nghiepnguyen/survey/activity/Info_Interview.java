package com.nghiepnguyen.survey.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nghiepnguyen.survey.R;

/**
 * A login screen that offers login via email/password.
 */
public class Info_Interview extends AppCompatActivity {

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_interview);

        radioSexGroup=(RadioGroup)findViewById(R.id.rgGioiTinh);
    }

    public void onNext(View view){
        String name, phone, cmnd, gt, email;

        name = ((TextView)findViewById(R.id.etName)).getText().toString();
        phone = ((TextView)findViewById(R.id.etPhone)).getText().toString();
        cmnd = ((TextView)findViewById(R.id.etCMND)).getText().toString();
        email = ((TextView)findViewById(R.id.etEmail)).getText().toString();

        int selectedId=radioSexGroup.getCheckedRadioButtonId();
        radioSexButton=(RadioButton)findViewById(selectedId);
        gt = radioSexButton.getText().toString();

        gt = gt.equals("Nữ")?"0":"1";

        System.out.println(gt);
    }

    public void SaveToDB(){

    }
}

