package com.nghiepnguyen.survey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

/**
 * Created by nghiep on 11/22/15.
 */
public class ProjectListAdapter extends ArrayAdapter<ProjectModel> {
    private static final String TAG = "ProjectListAdapter";
    private Context mContext;

    private List<ProjectModel> projectList;

    public ProjectListAdapter(Context mContext, List<ProjectModel> projectList) {
        super(mContext,0,projectList);
        this.mContext = mContext;
        this.projectList=projectList;


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

            viewHolder.tvProjectName= (TextView) view.findViewById(R.id.tv_project_name);
            viewHolder.tvProjectDescription= (TextView) view.findViewById(R.id.tv_project_description);
            viewHolder.ivProjectImage = (ImageView) view.findViewById(R.id.iv_project_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }



        viewHolder.tvProjectName.setText(project.getName());
        viewHolder.tvProjectDescription.setText(Html.fromHtml(project.getDescription()));
        String urlImage="6sao.vn"+project.getImage1();
        Log.e(TAG, urlImage);
        ImageUtil.loadImageView(mContext,urlImage,viewHolder.ivProjectImage,R.drawable.logo_6sao);
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
