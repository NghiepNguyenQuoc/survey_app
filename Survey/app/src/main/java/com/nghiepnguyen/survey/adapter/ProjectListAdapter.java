package com.nghiepnguyen.survey.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.sqlite.QuestionaireSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.RouteSQLiteHelper;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by nghiep on 11/22/15.
 */
public class ProjectListAdapter extends ArrayAdapter<ProjectModel> {
    private static final String TAG = "ProjectListAdapter";
    public ImageLoader imageLoader;
    DisplayImageOptions options;
    private Context mContext;

    private List<ProjectModel> projectList;
    private QuestionaireSQLiteHelper questionaireSQLiteHelper;

    public ProjectListAdapter(Context mContext, List<ProjectModel> projectList) {
        super(mContext, 0, projectList);
        this.mContext = mContext;
        this.projectList = projectList;

        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.logo_6sao)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        questionaireSQLiteHelper = new QuestionaireSQLiteHelper(mContext);
    }


    public void display(ImageView img, String url) {
        imageLoader.displayImage(url, img, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }


    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public ProjectModel getItem(int index) {
        return projectList.get(index);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ProjectModel project = projectList.get(i);

        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_project, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);
            viewHolder.tvProjectDescription = (TextView) view.findViewById(R.id.tv_project_description);
            viewHolder.ivProjectImage = (ImageView) view.findViewById(R.id.iv_project_image);
            viewHolder.ivMenuPopup = (ImageView) view.findViewById(R.id.iv_menu_popup);
            viewHolder.pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.tvProjectName.setText(project.getName());
        viewHolder.tvProjectDescription.setText(Html.fromHtml(project.getDescription()));
        String urlImage = "http://6sao.vn" + project.getImage1();

        display(viewHolder.ivProjectImage, urlImage);
        final View finalView = view;
        viewHolder.ivMenuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final PopupMenu popup = new PopupMenu(mContext, viewHolder.ivMenuPopup);
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.menu_popup_download) {
                            viewHolder.pbLoading.setVisibility(View.VISIBLE);
                            downloadProjectData(project, viewHolder.pbLoading, finalView);
                            return true;
                        } else if (i == R.id.menu_popup_upload) {
                            //do something
                            return true;
                        } else {
                            return onMenuItemClick(item);
                        }
                    }
                });

                popup.show();
            }
        });

        if(questionaireSQLiteHelper .getCountQuestionnaireByProjectId(project.getID())>0)
            view.setBackgroundColor(mContext.getResources().getColor(R.color.cl_default_trans));
        else
            view.setBackgroundColor(mContext.getResources().getColor(R.color.cl_white));
        return view;
    }


    private static class ViewHolder {
        public TextView tvProjectName;
        public TextView tvProjectDescription;
        public ImageView ivProjectImage;
        public ImageView ivMenuPopup;
        public ProgressBar pbLoading;
    }

    public List<ProjectModel> getProjectList() {
        return projectList;
    }

    public void setBookings(List<ProjectModel> projectList) {
        this.projectList = projectList;
    }

    private void downloadProjectData(final ProjectModel project, final View loadingView, final View itemView) {
        SurveyApiWrapper.downloadProjectData(mContext, project.getID(), new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        List<QuestionnaireModel> allDataOfProject = (List<QuestionnaireModel>) data;
                        if (allDataOfProject != null && allDataOfProject.size() > 0) {
                            if (questionaireSQLiteHelper.getCountQuestionnaireByProjectId(project.getID()) == 0)
                                for (QuestionnaireModel item : allDataOfProject)
                                    questionaireSQLiteHelper.addQuestionnaire(item, project.getID());

                        }

                        // download project route
                        downloadProjectRoute(project, loadingView,itemView);
                    }
                });
            }

            @Override
            public void onFailure(CommonErrorModel error) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        dialog.setCancelable(false);
                        dialog.setTitle(mContext.getString(R.string.title_attention));
                        dialog.setMessage(mContext.getString(R.string.message_can_not_get_questionnaire_list));
                        dialog.setPositiveButton(mContext.getString(R.string.button_ok), null);
                        dialog.show();

                        loadingView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private void downloadProjectRoute(final ProjectModel project, final View loadingView, final View itemView) {
        SurveyApiWrapper.downloadProjectRoute(mContext, project.getID(), new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        RouteSQLiteHelper routeSQLiteHelper = new RouteSQLiteHelper(mContext);
                        List<RouteModel> routeModels = (List<RouteModel>) data;
                        if (routeModels != null && routeModels.size() > 0) {
                            if (routeSQLiteHelper.countRouteByProjectId(project.getID()) == 0)
                                for (RouteModel item : routeModels)
                                    routeSQLiteHelper.addQuestionnaire(item, project.getID());

                        }

                        loadingView.setVisibility(View.GONE);
                        itemView.setBackgroundColor(mContext.getResources().getColor(R.color.cl_default_trans));
                        Utils.showToastLong(mContext,String.format(mContext.getString(R.string.message_download_compeleted),project.getName()));
                    }
                });
            }

            @Override
            public void onFailure(CommonErrorModel error) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        dialog.setCancelable(false);
                        dialog.setTitle(mContext.getString(R.string.title_attention));
                        dialog.setMessage(mContext.getString(R.string.message_can_not_get_route_list));
                        dialog.setPositiveButton(mContext.getString(R.string.button_ok), null);
                        dialog.show();

                        loadingView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCompleted() {

            }
        });
    }

}
