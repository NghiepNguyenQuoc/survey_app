package com.nghiepnguyen.survey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.model.ProjectModel;
import com.nghiepnguyen.survey.utils.ImageUtil;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
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

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.tvProjectName.setText(project.getName());
        viewHolder.tvProjectDescription.setText(Html.fromHtml(project.getDescription()));
        String urlImage = "http://6sao.vn" + project.getImage1();
        //ImageUtil.loadImageView(mContext, urlImage, viewHolder.ivProjectImage, R.drawable.logo_6sao);

        display(viewHolder.ivProjectImage, urlImage);
        return view;
    }


    private static class ViewHolder {
        public TextView tvProjectName;
        public TextView tvProjectDescription;
        public ImageView ivProjectImage;
    }

    public List<ProjectModel> getProjectList() {
        return projectList;
    }

    public void setBookings(List<ProjectModel> projectList) {
        this.projectList = projectList;
    }

}
