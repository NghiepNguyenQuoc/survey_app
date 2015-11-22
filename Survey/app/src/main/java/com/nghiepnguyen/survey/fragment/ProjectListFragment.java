package com.nghiepnguyen.survey.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.adapter.ProjectListAdapter;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;

import java.util.List;

/**
 * Created by nghiep on 10/29/15.
 */
public class ProjectListFragment extends Fragment {

    private final static String TAG = "ProjectListFragment";
    private ProgressBar loadingWebview;
    private Activity mActivity;
    private Context mContext;

    private ListView mProjectListListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

        UserInfoModel userInfo = UserInfoManager.getUserInfo(mActivity);
        SurveyApiWrapper.getProjectList(mActivity, userInfo.getID(), userInfo.getSecrectToken(), new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<ProjectModel> projectList = (List<ProjectModel>) data;
                        ProjectListAdapter adapter = new ProjectListAdapter(mActivity, projectList);
                        mProjectListListView.setAdapter(adapter);
                        loadingWebview.setVisibility(View.GONE);

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

    // Init view
    private void initView() {
        mProjectListListView = (ListView) getView().findViewById(R.id.lv_project_list);
        loadingWebview = (ProgressBar) getView().findViewById(R.id.loading_webview);

        loadingWebview.setVisibility(View.VISIBLE);

    }
}
