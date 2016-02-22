package com.nghiepnguyen.survey.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.adapter.OptionQuestionnaireAdapter;
import com.nghiepnguyen.survey.adapter.ProjectListAdapter;
import com.nghiepnguyen.survey.model.QuestionModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.utils.Constant;

import java.util.List;

/**
 * Created by 08670_000 on 21/02/2016.
 */
public class SAQuestionFragment extends Fragment {
    private final static String TAG = "SAQuestionFragment";
    private Activity mActivity;
    private List<QuestionnaireModel> questionnaireList;
    private QuestionModel questionModel;
    private ListView mOptionListView;

    public SAQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.questionnaireList = getArguments().getParcelableArrayList(Constant.BUNDLE_QUESTIONNAIRE);
            this.questionModel = getArguments().getParcelable(Constant.BUNDLE_QUESTION);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sa_question, container, false);
    }

    // Init view
    private void initView() {
        mOptionListView = (ListView) getView().findViewById(R.id.fragment_sa_question_listview);
        OptionQuestionnaireAdapter adapter = new OptionQuestionnaireAdapter(mActivity, questionModel, questionnaireList);
        mOptionListView.setAdapter(adapter);
    }
}
