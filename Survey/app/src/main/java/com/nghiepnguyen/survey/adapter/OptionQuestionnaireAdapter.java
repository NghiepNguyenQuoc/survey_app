package com.nghiepnguyen.survey.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

    public OptionQuestionnaireAdapter(Context context, List<QuestionnaireModel> optionList) {
        super(context, 0, optionList);
        this.mContext = context;
        this.optionList = optionList;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final QuestionnaireModel option = optionList.get(i);

        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_option_question, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.optionCheckbox = (CheckBox) view.findViewById(R.id.item_option_question_option_checbox);
            viewHolder.otherOptionTextView = (TextView) view.findViewById(R.id.item_option_question_other_option_edittext);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.optionCheckbox.setText(option.getDescription());
        if (option.getAllowInputText() == 1)
            viewHolder.otherOptionTextView.setVisibility(View.VISIBLE);
        else
            viewHolder.otherOptionTextView.setVisibility(View.GONE);
        return view;
    }

    private static class ViewHolder {
        public CheckBox optionCheckbox;
        public TextView otherOptionTextView;
    }

    public List<QuestionnaireModel> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<QuestionnaireModel> optionList) {
        this.optionList = optionList;
    }
}
