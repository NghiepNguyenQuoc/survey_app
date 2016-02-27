package com.nghiepnguyen.survey.fragment;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.activity.ProjectSurveyActivity;
import com.nghiepnguyen.survey.adapter.ProjectListAdapter;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;

import java.util.List;

/**
 * Created by nghiep on 10/29/15.
 */
public class ProjectListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final static String TAG = "ProjectListFragment";
    private ProgressBar loadingProgressBar;
    private Activity mActivity;
    UserInfoModel userInfo;

    private ListView mProjectListListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
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
    public void onResume() {
        super.onResume();
        userInfo = UserInfoManager.getUserInfo(mActivity);
        callApiGetProjectList();
    }

    // Init view
    private void initView() {
        mProjectListListView = (ListView) getView().findViewById(R.id.lv_project_list);
        mProjectListListView.setOnItemClickListener(this);
        loadingProgressBar = (ProgressBar) getView().findViewById(R.id.pb_loading);

        loadingProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<ProjectModel> projectList = ((ProjectListAdapter) mProjectListListView.getAdapter()).getProjectList();
        Intent intent = new Intent(mActivity, ProjectSurveyActivity.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable(Constant.BUNDLE_QUESTION,projectList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void callApiGetProjectList() {
        SurveyApiWrapper.getProjectList(mActivity, userInfo.getID(), userInfo.getSecrectToken(), new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<ProjectModel> projectList = (List<ProjectModel>) data;
                        ProjectListAdapter adapter = new ProjectListAdapter(mActivity, projectList);
                        mProjectListListView.setAdapter(adapter);
                        loadingProgressBar.setVisibility(View.GONE);

                    }
                });

            }

            @Override
            public void onFailure(CommonErrorModel error) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingProgressBar.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                        dialog.setCancelable(false);
                        dialog.setTitle(getResources().getString(R.string.title_attention));
                        dialog.setMessage(getResources().getString(R.string.message_can_not_get_project_list));
                        dialog.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mActivity.finish();
                            }
                        });
                        dialog.show();
                    }
                });
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
