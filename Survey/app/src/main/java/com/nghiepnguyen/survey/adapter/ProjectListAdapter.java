package com.nghiepnguyen.survey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nghiepnguyen.survey.Interface.ApiInterface;
import com.nghiepnguyen.survey.Interface.ICallBack;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.activity.ProjectSurveyActivity;
import com.nghiepnguyen.survey.model.CommonErrorModel;
import com.nghiepnguyen.survey.model.GoogleAPI.GeoLocation;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.SaveAnswerModel;
import com.nghiepnguyen.survey.model.sqlite.AnswerSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.QuestionaireSQLiteHelper;
import com.nghiepnguyen.survey.model.sqlite.RouteSQLiteHelper;
import com.nghiepnguyen.survey.networking.GoogleApiWrapper;
import com.nghiepnguyen.survey.networking.SurveyApiWrapper;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;
import com.nghiepnguyen.survey.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by nghiep on 11/22/15.
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {
    public final static int REQUEST_CODE = 1001;
    private static final String TAG = "ProjectListAdapter";
    private static final String UPLOAD_PATH = "/Audio_Record_Storage";
    private static final String HOST_IP = "112.213.89.21";
    private static final String USER_NAME = "6saovn";
    private static final String PASSWORD = "NKVF2764etxq";
    private static final int PORT = 21;

    final Handler handler = new Handler();
    public ImageLoader imageLoader;
    DisplayImageOptions options;
    int numberOfUpload = 0;
    Runnable runable;
    private Context mContext;
    private Fragment mainFragment;
    private List<ProjectModel> projectList;
    private QuestionaireSQLiteHelper questionaireSQLiteHelper;
    private RouteSQLiteHelper routeSQLiteHelper;
    private AnswerSQLiteHelper answerSQLiteHelper;

    @Inject
    Retrofit retrofit;
    public ProjectListAdapter( Context mContext,Fragment fragment, List<ProjectModel> projectList) {
        this.mContext = mContext;
        this.mainFragment = fragment;
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


        Map<String, Integer> strings = UserInfoManager.getUploadTimesOfProject(mContext);
        int totalUpload = 0;
        if (strings.size() > 0 && strings.containsKey(String.valueOf(project.getID())))
            totalUpload = strings.get(String.valueOf(project.getID()));
        String uploadAndTotal = "Số lần khảo sát: ".concat(String.valueOf(answerSQLiteHelper.countAnswerModelByProjectId(project.getID()))) +
                " / Số lần upload thành công: ".concat(String.valueOf(totalUpload));
        holder.tvNumberResult.setText(uploadAndTotal);
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
                            numberOfUpload = 0;
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionaireSQLiteHelper.getAllQuestionIDByProjectId(project.getID()).size() > 0) {
                    Intent intent = new Intent(mContext, ProjectSurveyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.BUNDLE_QUESTION, project);
                    intent.putExtras(bundle);
                    mainFragment.startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Utils.showToastLong(mContext, mContext.getString(R.string.message_project_not_ready));
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    private void display(ImageView img, String url) {
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

    private void updateProjectData(final ProjectModel project, final View loadingView, final CardView itemView) {
        questionaireSQLiteHelper.deleteAllQuestionnaire(project.getID());
        routeSQLiteHelper.deleteAllRoutes(project.getID());
        downloadProjectData(project, loadingView, itemView);
    }

    private void downloadProjectData(final ProjectModel project, final View loadingView, final CardView itemView) {
        Call<List<QuestionnaireModel>> listCall=retrofit.create(ApiInterface.class).getProjectData(project.getID());
        listCall.enqueue(new Callback<List<QuestionnaireModel>>() {
            @Override
            public void onResponse(Call<List<QuestionnaireModel>> call, Response<List<QuestionnaireModel>> response) {
                List<QuestionnaireModel> allDataOfProject = response.body();
                if (allDataOfProject != null && allDataOfProject.size() > 0) {
                    if (questionaireSQLiteHelper.getCountQuestionnaireByProjectId(project.getID()) == 0)
                        for (QuestionnaireModel item : allDataOfProject) {
                            questionaireSQLiteHelper.addQuestionnaire(item, project.getID());
                        }
                }

                // download project route
                downloadProjectRoute(project, loadingView, itemView);
            }

            @Override
            public void onFailure(Call<List<QuestionnaireModel>> call, Throwable t) {
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

    private void downloadProjectRoute(final ProjectModel project, final View loadingView, final CardView itemView) {
        Call<List<RouteModel>> listCall=retrofit.create(ApiInterface.class).getProjectRoute(project.getID());
        listCall.enqueue(new Callback<List<RouteModel>>() {
            @Override
            public void onResponse(Call<List<RouteModel>> call, Response<List<RouteModel>> response) {
                List<RouteModel> routeModels = response.body();
                if (routeModels != null && routeModels.size() > 0) {
                    if (routeSQLiteHelper.countRouteByProjectId(project.getID()) == 0)
                        for (RouteModel item : routeModels)
                            routeSQLiteHelper.addQuestionnaire(item, project.getID());

                }

                loadingView.setVisibility(View.GONE);
                itemView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cl_default_trans));
                Utils.showToastLong(mContext, String.format(mContext.getString(R.string.message_download_compeleted), project.getName()));
            }

            @Override
            public void onFailure(Call<List<RouteModel>> call, Throwable t) {
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

    private void uploadProjectData(final Context mContext, final View view, final int projectId) {
        final SaveAnswerModel saveAnswerModel = answerSQLiteHelper.getSaveAnswerModelByProjectId(projectId);
        if (saveAnswerModel == null) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    // show the number uploaded successfully
                    Toast.makeText(mContext, String.format(mContext.getString(R.string.message_upload_compeleted), numberOfUpload), Toast.LENGTH_LONG).show();

                    if (numberOfUpload > 0) {
                        // save data to cache
                        Map<String, Integer> strings = UserInfoManager.getUploadTimesOfProject(mContext);

                        if (strings.size() == 0 || !strings.containsKey(String.valueOf(projectId)))
                            strings.put(String.valueOf(projectId), numberOfUpload);
                        else {
                            strings.put(String.valueOf(projectId), strings.get(String.valueOf(projectId)) + numberOfUpload);
                        }
                        UserInfoManager.saveUploadTimesOfProject(mContext, strings);
                    }

                    //update view
                    view.setVisibility(View.GONE);
                    notifyDataSetChanged();
                    if (runable != null)
                        handler.removeCallbacks(runable);

                }
            });
        } else {
            if (saveAnswerModel.getGeoLatitude() != 0 && saveAnswerModel.getGeoLongitude() != 0) {
                GoogleApiWrapper.getGeocodeAddressTextSearch(mContext, saveAnswerModel.getGeoLatitude() + "," + saveAnswerModel.getGeoLongitude(), new ICallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data instanceof List) {
                            List<GeoLocation> geoLocationList = (List<GeoLocation>) data;
                            if (geoLocationList.size() > 0) {
                                saveAnswerModel.setGeoAddress(geoLocationList.get(0).getFullText());
                                callApiToUpload(saveAnswerModel, view, projectId);
                            } else {
                                callApiToUpload(saveAnswerModel, view, projectId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(CommonErrorModel error) {
                    }

                    @Override
                    public void onCompleted() {
                    }
                });
            } else {
                callApiToUpload(saveAnswerModel, view, projectId);
            }

        }
    }

    private void callApiToUpload(final SaveAnswerModel saveAnswerModel, final View view, final int projectId) {
        SurveyApiWrapper.saveResultSurvey(mContext, saveAnswerModel, new ICallBack() {
            @Override
            public void onSuccess(Object data) {
                uploadAudio(saveAnswerModel, new ICallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        answerSQLiteHelper.deleteAnswer(saveAnswerModel.getIdentity());
                        numberOfUpload++;
                        uploadProjectData(mContext, view, projectId);
                    }

                    @Override
                    public void onFailure(CommonErrorModel error) {
                        answerSQLiteHelper.deleteAnswer(saveAnswerModel.getIdentity());
                        numberOfUpload++;
                        uploadProjectData(mContext, view, projectId);
                    }

                    @Override
                    public void onCompleted() {

                    }
                });

                /*runable = new Runnable() {
                    public void run() {
                        answerSQLiteHelper.deleteAnswer(saveAnswerModel.getIdentity());
                        numberOfUpload++;
                        uploadProjectData(mContext, view, projectId);
                    }
                };*/
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


    private void uploadAudio(SaveAnswerModel saveAnswerModel, final ICallBack iCallBack) {
        String srcFileName = saveAnswerModel.getProjectID() + "_" + saveAnswerModel.getStartRecordingTime() + "_" + saveAnswerModel.getFullName() + ".wav";
        String desFileName = saveAnswerModel.getProjectID() + "_" + saveAnswerModel.getEndRecordingTime() + "_" + saveAnswerModel.getFullName() + ".ax";
        String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + srcFileName;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(HOST_IP, PORT);
            ftpClient.login(USER_NAME, PASSWORD);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (Utils.makeDirectories(ftpClient, UPLOAD_PATH)) {
                File localFile = new File(outputFile);
                InputStream inputStream = new FileInputStream(localFile);

                System.out.println("Start uploading second file");
                OutputStream outputStream = ftpClient.storeFileStream(desFileName);
                byte[] bytesIn = new byte[4096];
                int read;

                while ((read = inputStream.read(bytesIn)) != -1) {
                    outputStream.write(bytesIn, 0, read);
                }
                inputStream.close();
                outputStream.close();

                boolean completed = ftpClient.completePendingCommand();
                if (completed) {
                    Log.d(TAG, "Upload success");
                    iCallBack.onSuccess(null);
                    boolean abc = localFile.delete();
                    Log.d(TAG, abc + "");
                } else {
                    Log.d(TAG, "Upload failed");
                    iCallBack.onFailure(null);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            iCallBack.onFailure(null);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvProjectName;
        TextView tvProjectDescription;
        TextView tvNumberResult;
        ImageView ivProjectImage;
        ImageView ivMenuPopup;
        ProgressBar pbLoading;

        ProjectViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvProjectName = (TextView) itemView.findViewById(R.id.tv_project_name);
            tvProjectDescription = (TextView) itemView.findViewById(R.id.tv_project_description);
            tvNumberResult = (TextView) itemView.findViewById(R.id.tv_number_result);
            ivProjectImage = (ImageView) itemView.findViewById(R.id.iv_project_image);
            ivMenuPopup = (ImageView) itemView.findViewById(R.id.iv_menu_popup);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
        }
    }
}
