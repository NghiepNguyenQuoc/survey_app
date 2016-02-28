package com.nghiepnguyen.survey.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;

import java.util.List;

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
public class OptionQuestionnaireAdapter extends ArrayAdapter<QuestionnaireModel> {
    private static final String TAG = "OptionQuestionnaireAdapter";
    private Context mContext;
    private List<QuestionnaireModel> optionList;
    private QuestionModel questionModel;

    private RadioButton mSelectedRB;// current RadioButton when user focus
    private int mSelectedPosition = -1;// current position in adapter


    public OptionQuestionnaireAdapter(Context context, QuestionModel questionModel, List<QuestionnaireModel> optionList) {
        super(context, 0, optionList);
        this.mContext = context;
        this.optionList = optionList;
        this.questionModel = questionModel;
    }


    @Override
    public int getCount() {
        return optionList.size();
    }

    @Override
    public QuestionnaireModel getItem(int index) {
        return optionList.get(index);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final QuestionnaireModel option = optionList.get(i);

        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_option_question, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.optionCheckbox = (CheckBox) view.findViewById(R.id.item_option_question_option_checbox);
            viewHolder.optionRadioButton = (RadioButton) view.findViewById(R.id.item_option_question_option_radio_button);
            viewHolder.otherOptionTextView = (TextView) view.findViewById(R.id.item_option_question_other_option_edittext);

            view.setTag(viewHolder);
            view.setTag(R.id.item_option_question_option_checbox, viewHolder.optionCheckbox);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ///////////////////////////////////////////////////////////////////
        if (questionModel.getType() == 0 || questionModel.getType() == 2) {
            viewHolder.optionRadioButton.setVisibility(View.VISIBLE);
            viewHolder.optionCheckbox.setVisibility(View.GONE);

            if (option.getAllowInputText() == 1) {
                viewHolder.otherOptionTextView.setVisibility(View.VISIBLE);

            } else {
                viewHolder.otherOptionTextView.setVisibility(View.GONE);
            }

            //set check for radio button
            viewHolder.optionRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i != mSelectedPosition && mSelectedRB != null) {
                        mSelectedRB.setChecked(false);
                        optionList.get(mSelectedPosition).setIsSelected(false);

                    }
                    mSelectedPosition = i;
                    optionList.get(mSelectedPosition).setIsSelected(true);
                    mSelectedRB = (RadioButton) view;
                }
            });

        } else if (questionModel.getType() == 1) {
            viewHolder.optionCheckbox.setVisibility(View.VISIBLE);
            viewHolder.optionRadioButton.setVisibility(View.GONE);

            if (option.getAllowInputText() == 1) {
                viewHolder.otherOptionTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.otherOptionTextView.setVisibility(View.GONE);

            }
        } else {
            viewHolder.optionCheckbox.setVisibility(View.VISIBLE);
            viewHolder.optionRadioButton.setVisibility(View.GONE);
            if (option.getAllowInputText() == 1) {
                viewHolder.otherOptionTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.otherOptionTextView.setVisibility(View.GONE);
            }
        }

        viewHolder.optionRadioButton.setText(option.getDescription());
        viewHolder.optionCheckbox.setText(option.getDescription());

        //set check for radio button
        viewHolder.optionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int getPosition = (Integer) compoundButton.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                optionList.get(getPosition).setIsSelected(viewHolder.optionCheckbox.isChecked());
            }
        });

        viewHolder.otherOptionTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                option.setOtherOption(editable.toString());
            }
        });
        viewHolder.optionCheckbox.setTag(i); // This line is important.
        viewHolder.optionCheckbox.setChecked(optionList.get(i).isSelected());
        return view;
    }

    private static class ViewHolder {
        public CheckBox optionCheckbox;
        public RadioButton optionRadioButton;
        public TextView otherOptionTextView;
    }

    public List<QuestionnaireModel> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<QuestionnaireModel> optionList) {
        this.optionList = optionList;
    }
}
