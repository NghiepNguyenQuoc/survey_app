package com.nghiepnguyen.survey.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by nghiep on 10/29/15.
 */
public class ScreenTransitionUtil {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================
    public static void startActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class activity, boolean isClearTask) {
        Intent intent = new Intent(context, activity);
        if(isClearTask)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((FragmentActivity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void startActivityWithBundle(Context context, Class activity, Bundle extras) {
        Intent intent = new Intent(context, activity);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void startActivityWithBundle(Context context, Class activity, Bundle extras, boolean isClearTask) {
        Intent intent = new Intent(context, activity);
        intent.putExtras(extras);
        if(isClearTask)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((FragmentActivity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Start activity with result - There are not bundle for the event
     *
     * @param context
     * @param activity
     * @param requestCode
     */
    public static void startActivityForResult(Context context, Class activity, int requestCode) {
        Intent intent = new Intent(context, activity);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    /**
     * Start activity with result - Input with an event and its bundle
     *
     * @param context
     * @param intent
     * @param requestCode
     */
    public static void startActivityForResult(Context context, Intent intent, int requestCode) {
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void startGooglePlayStore(Context context){
        final String appPackageName = "com.deliveree.user"; // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void switchFragmentWithAnimation(Fragment fragment,
                                                   FragmentManager manager,int layout, Integer enter, Integer exit) {

        if (enter == null && exit == null)
            manager.beginTransaction()
                    .replace(layout, fragment, fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        else
            manager.beginTransaction().setCustomAnimations(enter, exit)
                    .replace(layout, fragment, fragment.getClass().toString())
                    .commitAllowingStateLoss();
    }

    public static void removeCurrentFragment(FragmentManager manager, int frame_layout) {
        try {
            manager.beginTransaction().remove(manager.findFragmentById(frame_layout)).commit();
        } catch (Exception exp) {
        }
    }

    public static Fragment getCurrentFragmentTag(FragmentManager manager, int frame_layout) {
        return manager.findFragmentById(frame_layout);
    }

    public static void switchFragmentWithAnimation(Fragment fragment,
                                                   FragmentManager manager, int layout) {
        switchFragmentWithAnimation(fragment, manager, layout, null, null);
    }

    public static void addFragmentWithAnimation(Fragment fragment,
                                                FragmentManager manager, int layout, Integer enter, Integer exit,
                                                String fragmentTag) {

        if (enter == null) {
            enter = android.R.anim.fade_in;
        }

        if (exit == null) {
            exit = android.R.anim.fade_out;
        }

        manager.beginTransaction().setCustomAnimations(enter, exit)
                .add(layout, fragment, "PlaceHolder")
                .addToBackStack(fragmentTag).commitAllowingStateLoss();
    }
    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
