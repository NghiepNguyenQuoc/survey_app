package com.nghiepnguyen.survey.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.SaveAnswerModel;
import com.nghiepnguyen.survey.model.sqlite.AnswerSQLiteHelper;
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
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {
    private static final String TAG = "ProjectListAdapter";
    public ImageLoader imageLoader;
    DisplayImageOptions options;
    private Context mContext;

    private List<ProjectModel> projectList;
    private QuestionaireSQLiteHelper questionaireSQLiteHelper;
    private RouteSQLiteHelper routeSQLiteHelper;
    private AnswerSQLiteHelper answerSQLiteHelper;
    private static RecyclerViewClickListener itemListener;

    public ProjectListAdapter(Context mContext, List<ProjectModel> projectList, RecyclerViewClickListener itemListener) {
        this.mContext = mContext;
        this.projectList = projectList;
        this.itemListener = itemListener;

        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.logo_6sao)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        questionaireSQLiteHelper = new QuestionaireSQLiteHelper(mContext);
        routeSQLiteHelper = new RouteSQLiteHelper(mContext);
        answerSQLiteHelper = new AnswerSQLiteHelper(mContext);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProjectViewHolder holder, int position) {
        final ProjectModel project = projectList.get(position);

        holder.tvProjectName.setText(project.getName());
        holder.tvProjectDescription.setText(Html.fromHtml(project.getDescription()));
        holder.tvNumberResult.setText("Số lần khảo sát: " + answerSQLiteHelper.countAnswerModelByProjectId(project.getID()));
        String urlImage = "http://6sao.vn" + project.getImage1();

        display(holder.ivProjectImage, urlImage);
        holder.ivMenuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final PopupMenu popup = new PopupMenu(mContext, holder.ivMenuPopup);
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.menu_popup_download) {
                            holder.pbLoading.setVisibility(View.VISIBLE);
                            downloadProjectData(project, holder.pbLoading, holder.cardView);
                            return true;
                        } else if (i == R.id.menu_popup_update) {
                            updateProjectData(project, holder.pbLoading, holder.cardView);
                            return true;
                        } else if (i == R.id.menu_popup_upload) {
                            holder.pbLoading.setVisibility(View.VISIBLE);
                            uploadProjectData(mContext, holder.pbLoading, project.getID());

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

        if (questionaireSQLiteHelper.getCountQuestionnaireByProjectId(project.getID()) > 0)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cl_default_trans));
        else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cl_white));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public List<ProjectModel> getProjectList() {
        return projectList;
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cardView;
        public TextView tvProjectName;
        public TextView tvProjectDescription;
        public TextView tvNumberResult;
        public ImageView ivProjectImage;
        public ImageView ivMenuPopup;
        public ProgressBar pbLoading;

        public ProjectViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvProjectName = (TextView) itemView.findViewById(R.id.tv_project_name);
            tvProjectDescription = (TextView) itemView.findViewById(R.id.tv_project_description);
            tvNumberResult = (TextView) itemView.findViewById(R.id.tv_number_result);
            ivProjectImage = (ImageView) itemView.findViewById(R.id.iv_project_image);
            ivMenuPopup = (ImageView) itemView.findViewById(R.id.iv_menu_popup);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, this.getAdapterPosition());

        }
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

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    private void updateProjectData(final ProjectModel project, final View loadingView, final CardView itemView) {
        questionaireSQLiteHelper.deleteAllQuestionnaire(project.getID());
        routeSQLiteHelper.deleteAllRoutes(project.getID());
        downloadProjectData(project, loadingView, itemView);
    }

    private void downloadProjectData(final ProjectModel project, final View loadingView, final CardView itemView) {
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
                                for (QuestionnaireModel item : allDataOfProject) {
                                    questionaireSQLiteHelper.addQuestionnaire(item, project.getID());
                                }
                        }

                        // download project route
                        downloadProjectRoute(project, loadingView, itemView);
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

    private void downloadProjectRoute(final ProjectModel project, final View loadingView, final CardView itemView) {
        SurveyApiWrapper.downloadProjectRoute(mContext, project.getID(), new ICallBack() {
            @Override
            public void onSuccess(final Object data) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        List<RouteModel> routeModels = (List<RouteModel>) data;
                        if (routeModels != null && routeModels.size() > 0) {
                            if (routeSQLiteHelper.countRouteByProjectId(project.getID()) == 0)
                                for (RouteModel item : routeModels)
                                    routeSQLiteHelper.addQuestionnaire(item, project.getID());

                        }

                        loadingView.setVisibility(View.GONE);
                        itemView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cl_default_trans));
                        Utils.showToastLong(mContext, String.format(mContext.getString(R.string.message_download_compeleted), project.getName()));
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

    int numberOfUpload = 0;
    final Handler handler = new Handler();
    Runnable runable;
    private void uploadProjectData(final Context mContext, final View view, final int projectId) {
        final SaveAnswerModel saveAnswerModel = answerSQLiteHelper.getSaveAnswerModelByProjectId(projectId);
        if (saveAnswerModel == null) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, String.format(mContext.getString(R.string.message_upload_compeleted), numberOfUpload), Toast.LENGTH_LONG).show();
                    view.setVisibility(View.GONE);
                    notifyDataSetChanged();
                    if(runable !=null)
                            handler.removeCallbacks(runable);
                }
            });
        } else {
            SurveyApiWrapper.saveResultSurvey(mContext, saveAnswerModel, new ICallBack() {
                @Override
                public void onSuccess(Object data) {
                    runable = new Runnable() {
                        public void run() {
                            answerSQLiteHelper.deleteAnswer(saveAnswerModel.getIdentity());
                            uploadProjectData(mContext, view, projectId);
                            numberOfUpload++;
                        }
                    };
                    handler.postDelayed(runable, 1000);
                }

                @Override
                public void onFailure(CommonErrorModel error) {
                    error.getError();
                    Utils.showToastLong(mContext, mContext.getString(R.string.lost_internet_connection_message));
                }

                @Override
                public void onCompleted() {

                }
            });
        }
    }
}
