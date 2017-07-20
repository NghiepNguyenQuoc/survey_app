package com.nghiepnguyen.survey.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.adapter.ProjectListAdapter;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.sqlite.ProjectSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.QuestionaireSQLiteHelper;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;

import java.util.List;

/**
 * Created by nghiep on 10/29/15.
 */
public class ProjectListFragment extends Fragment {

    private final static String TAG = "ProjectListFragment";
    private ProgressBar loadingProgressBar;
    private Activity mActivity;
    //UserInfoModel userInfo;
    MemberModel memberInfo;

    private RecyclerView mProjectListListView;
    private ProjectSQLiteHelper projectSQLiteHelper;
    private QuestionaireSQLiteHelper questionaireSQLiteHelper;

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
        memberInfo = UserInfoManager.getMemberInfo(mActivity);
        projectSQLiteHelper = new ProjectSQLiteHelper(mActivity);
        questionaireSQLiteHelper = new QuestionaireSQLiteHelper(mActivity);

        callApiGetProjectList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Init view
    private void initView() {
        mProjectListListView = (RecyclerView) getView().findViewById(R.id.lv_project_list);
        mProjectListListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mActivity);
        mProjectListListView.setLayoutManager(llm);

        loadingProgressBar = (ProgressBar) getView().findViewById(R.id.pb_loading);
        loadingProgressBar.setVisibility(View.VISIBLE);

    }

    private void callApiGetProjectList() {
        SurveyApiWrapper.getProjectList(mActivity, memberInfo.getID(), memberInfo.getSecrectToken(), new ICallBack() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(final Object data) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<ProjectModel> projectList = (List<ProjectModel>) data;
                        if (projectList != null && projectList.size() > 0) {
                            for (ProjectModel item : projectList)
                                projectSQLiteHelper.addProject(item);

                            ProjectListAdapter adapter = new ProjectListAdapter(mActivity, ProjectListFragment.this, projectList);
                            mProjectListListView.setAdapter(adapter);
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }

            @Override
            public void onFailure(CommonErrorModel error) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<ProjectModel> projectList = projectSQLiteHelper.getAllProject();
                        if (projectList.size() > 0) {
                            ProjectListAdapter adapter = new ProjectListAdapter(mActivity, ProjectListFragment.this, projectList);
                            mProjectListListView.setAdapter(adapter);
                        } else {
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
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ProjectListAdapter.REQUEST_CODE) {
            mProjectListListView.getAdapter().notifyDataSetChanged();
        }
    }
}
