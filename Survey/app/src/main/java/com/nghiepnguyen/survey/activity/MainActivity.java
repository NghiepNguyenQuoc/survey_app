package com.nghiepnguyen.survey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nghiepnguyen.survey.R;
import com.nghiepnguyen.survey.fragment.ProjectListFragment;
import com.nghiepnguyen.survey.model.MemberModel;
import com.nghiepnguyen.survey.model.UserInfoModel;
import com.nghiepnguyen.survey.storage.UserInfoManager;
import com.nghiepnguyen.survey.utils.Constant;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    public Fragment fragment;
    private Toolbar toolbar;
    private NavigationView navigationView;

    //UserInfoModel currentUser;
    MemberModel memberInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        fragment = new ProjectListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment, Constant.PROJECT_LIST).commit();

        toolbar.setTitle(getResources().getString(R.string.nav_item_project_list));
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_project_list) {
            if (fragment == null || !fragment.getTag().equalsIgnoreCase(Constant.PROJECT_LIST)) {
                fragment = new ProjectListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment, Constant.PROJECT_LIST).commit();
                toolbar.setTitle(getResources().getString(R.string.nav_item_project_list));
            }
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            UserInfoManager.saveUserInfo(this, null);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tvUsername = (TextView) headerView.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = (TextView) headerView.findViewById(R.id.tv_user_email);


        /*currentUser = UserInfoManager.getUserInfo(this);
        tvUsername.setText(currentUser.getFullName());
        tvUserEmail.setText(currentUser.getLoginName());*/

        memberInfo = UserInfoManager.getMemberInfo(this);
        tvUsername.setText(memberInfo.getFirstName() + " " + memberInfo.getLastName());
        tvUserEmail.setText(memberInfo.getLoginName());

    }
}
