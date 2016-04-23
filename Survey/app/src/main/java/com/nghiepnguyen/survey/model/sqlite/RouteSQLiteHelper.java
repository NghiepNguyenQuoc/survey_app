package com.nghiepnguyen.survey.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;

import com.nghiepnguyen.survey.model.QuestionnaireModel;
import com.nghiepnguyen.survey.model.RouteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghiep on 4/9/16.
 */
public class RouteSQLiteHelper extends MySQLiteHelper {

    // Table Names
    public static final String TABLE_ROUTE = "route";

    // TABLE_QUESTIONNAIRE Columns names
    private static final String KEY_IDENTITY = "identity";
    private static final String KEY_CONDITION_ID = "ConditionID";
    private static final String KEY_QUESTIONNAIRE_ID = "QuestionnaireID";
    private static final String KEY_PROJECT_ID = "ProjectID";
    private static final String KEY_RESPONSE_VALUE = "ResponseValue";

    private static final String[] COLUMNS = {KEY_IDENTITY, KEY_CONDITION_ID,  KEY_QUESTIONNAIRE_ID,KEY_PROJECT_ID, KEY_RESPONSE_VALUE};

    // Table Create Statements

    // TABLE_QUESTIONNAIRE table create statement
    public static final String CREATE_ROUTE_TABLE = "CREATE TABLE " + TABLE_ROUTE  + " ( " +
            KEY_IDENTITY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CONDITION_ID + " INTEGER, " +
            KEY_QUESTIONNAIRE_ID + " INTEGER, " +
            KEY_PROJECT_ID + " INTEGER, " +
            KEY_RESPONSE_VALUE + " TEXT)";

    public RouteSQLiteHelper(Context context) {
        super(context);
    }


    // ------------------------ Questionnaire table methods ----------------//

    /**
     * Creating a Questionnaire
     */
    public void addQuestionnaire(RouteModel routeModel, int projectId) {
        Log.d("questionnaireModel", routeModel.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CONDITION_ID, routeModel.getQuestionnaireConditionsID());
        values.put(KEY_QUESTIONNAIRE_ID, routeModel.getQuestionnaireID());
        values.put(KEY_PROJECT_ID, projectId);
        values.put(KEY_RESPONSE_VALUE, routeModel.getResponseValue());

        // 3. insert
        db.insert(TABLE_ROUTE , // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    /**
     * get number record by projectid
     */
    public int countRouteByProjectId(int projectId) {
        // 1. build the query
        String query = "SELECT  count(*) FROM " + TABLE_ROUTE + " WHERE " + KEY_PROJECT_ID + "=" + projectId;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row
        if (cursor.moveToFirst()) {
            return Integer.parseInt(cursor.getString(0));
        }
        return 0;
    }

        /**
     * get Route
     */
    public List<RouteModel> getRoutesByQuestionId(int id) {
        List<RouteModel> routeModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE + " WHERE " + KEY_QUESTIONNAIRE_ID + "=" + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        RouteModel routeModel = null;
        if (cursor.moveToFirst()) {
            do {
                routeModel= new RouteModel();
                routeModel.setQuestionnaireConditionsID(Integer.parseInt(cursor.getString(1)));
                routeModel.setQuestionnaireID(Integer.parseInt(cursor.getString(2)));
                routeModel.setProjectID(Integer.parseInt(cursor.getString(3)));
                routeModel.setResponseValue(cursor.getString(4));

                // Add route to routes
                routeModels.add(routeModel);
            } while (cursor.moveToNext());
        }

        // return routes
        return routeModels;

    }

    /**
     * getting all route
     */
    public List<RouteModel> getAllRoutes() {
        List<RouteModel> routeModels=new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build route and add it to list
        RouteModel routeModel=null;
        if (cursor.moveToFirst()) {
            do {
                routeModel=new RouteModel();
                routeModel.setQuestionnaireConditionsID(Integer.parseInt(cursor.getString(1)));
                routeModel.setQuestionnaireID(Integer.parseInt(cursor.getString(2)));
                routeModel.setProjectID(Integer.parseInt(cursor.getString(3)));
                routeModel.setResponseValue(cursor.getString(4));

                // Add route to routes
                routeModels.add(routeModel);
            } while (cursor.moveToNext());
        }

        // return routes
        return routeModels;
    }

    /**
     * Deleting a Route
     */
    public void deleteRoute(RouteModel routeModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ROUTE,
                KEY_CONDITION_ID + " = ?",
                new String[]{String.valueOf(routeModel.getQuestionnaireConditionsID())});

        // 3. close
        db.close();
    }
}
