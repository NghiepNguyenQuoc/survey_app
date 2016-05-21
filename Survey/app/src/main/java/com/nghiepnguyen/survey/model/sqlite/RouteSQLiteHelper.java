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
    private static final String KEY_QUESTIONNAIRE_CHECK = "QuestionnaireID_Check";
    private static final String KEY_QUESTIONNAIRE_CHECK_OPTION = "QuestionnaireID_Check_Option";
    private static final String KEY_PROJECT_ID = "ProjectID";
    private static final String KEY_NEXT_QUESTION_ID = "NextQuestionnaireID";
    private static final String KEY_METHOD = "Method";
    private static final String KEY_GROUP_METHOD = "GroupMethod";
    private static final String KEY_GROUP_ZORDER = "GroupZOrder";
    private static final String KEY_RESPONSE_VALUE = "ResponseValue";
    private static final String KEY_GROUP_METHOD_NAME = "GroupMethodName";
    private static final String KEY_METHOD_NAME = "MethodName";

    // TABLE_QUESTIONNAIRE table create statement
    public static final String CREATE_ROUTE_TABLE = "CREATE TABLE " + TABLE_ROUTE + " ( " +
            KEY_IDENTITY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CONDITION_ID + " INTEGER, " +
            KEY_QUESTIONNAIRE_CHECK + " INTEGER, " +
            KEY_QUESTIONNAIRE_CHECK_OPTION + " INTEGER, " +
            KEY_PROJECT_ID + " INTEGER, " +
            KEY_NEXT_QUESTION_ID + " INTEGER, " +
            KEY_METHOD + " TEXT, " +
            KEY_GROUP_METHOD + " INTEGER, " +
            KEY_GROUP_ZORDER + " INTEGER," +
            KEY_RESPONSE_VALUE + " TEXT," +
            KEY_GROUP_METHOD_NAME + " INTEGER, " +
            KEY_METHOD_NAME + " INTEGER)";

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
        values.put(KEY_QUESTIONNAIRE_CHECK, routeModel.getQuestionnaireID_Check());
        values.put(KEY_QUESTIONNAIRE_CHECK_OPTION, routeModel.getQuestionnaireID_Check_Option());
        values.put(KEY_PROJECT_ID, projectId);
        values.put(KEY_NEXT_QUESTION_ID, routeModel.getNextQuestionnaireID());
        values.put(KEY_METHOD, routeModel.getMethod());
        values.put(KEY_GROUP_METHOD, routeModel.getGroupMethod());
        values.put(KEY_GROUP_ZORDER, routeModel.getGroupZOrder());
        values.put(KEY_RESPONSE_VALUE, routeModel.getResponseValue());
        values.put(KEY_GROUP_METHOD_NAME, routeModel.getGroupMethodName());
        values.put(KEY_METHOD_NAME, routeModel.getMethodName());

        // 3. insert
        db.insert(TABLE_ROUTE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    /**
     * get number record by projectid
     */
    public List<RouteModel> getAllRoute() {
        List<RouteModel> routeModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        RouteModel routeModel = null;
        if (cursor.moveToFirst()) {
            do {
                routeModel = new RouteModel();
                routeModel.setQuestionnaireConditionsID(Integer.parseInt(cursor.getString(1)));
                routeModel.setQuestionnaireID_Check(Integer.parseInt(cursor.getString(2)));
                routeModel.setQuestionnaireID_Check_Option(Integer.parseInt(cursor.getString(3)));
                routeModel.setProjectID(Integer.parseInt(cursor.getString(4)));
                routeModel.setNextQuestionnaireID(Integer.parseInt(cursor.getString(5)));
                routeModel.setMethod(Integer.parseInt(cursor.getString(6)));
                routeModel.setGroupMethod(Integer.parseInt(cursor.getString(7)));
                routeModel.setGroupZOrder(Integer.parseInt(cursor.getString(8)));
                routeModel.setResponseValue(cursor.getString(9));
                routeModel.setGroupMethodName(cursor.getString(10));
                routeModel.setMethodName(cursor.getString(11));

                // Add route to routes
                routeModels.add(routeModel);
            } while (cursor.moveToNext());
        }

        // return routes
        return routeModels;
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
    public List<RouteModel> getRoutesByQuestionaireId(int id) {
        List<RouteModel> routeModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE + " WHERE " + KEY_QUESTIONNAIRE_CHECK + "=" + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        RouteModel routeModel = null;
        if (cursor.moveToFirst()) {
            do {
                routeModel = new RouteModel();
                routeModel.setQuestionnaireConditionsID(Integer.parseInt(cursor.getString(1)));
                routeModel.setQuestionnaireID_Check(Integer.parseInt(cursor.getString(2)));
                routeModel.setQuestionnaireID_Check_Option(Integer.parseInt(cursor.getString(3)));
                routeModel.setProjectID(Integer.parseInt(cursor.getString(4)));
                routeModel.setNextQuestionnaireID(Integer.parseInt(cursor.getString(5)));
                routeModel.setMethod(Integer.parseInt(cursor.getString(6)));
                routeModel.setGroupMethod(Integer.parseInt(cursor.getString(7)));
                routeModel.setGroupZOrder(Integer.parseInt(cursor.getString(8)));
                routeModel.setResponseValue(cursor.getString(9));
                routeModel.setGroupMethodName(cursor.getString(10));
                routeModel.setMethodName(cursor.getString(11));

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
        List<RouteModel> routeModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build route and add it to list
        RouteModel routeModel = null;
        if (cursor.moveToFirst()) {
            do {
                routeModel = new RouteModel();
                routeModel.setQuestionnaireConditionsID(Integer.parseInt(cursor.getString(1)));
                routeModel.setQuestionnaireID_Check(Integer.parseInt(cursor.getString(2)));
                routeModel.setQuestionnaireID_Check_Option(Integer.parseInt(cursor.getString(3)));
                routeModel.setProjectID(Integer.parseInt(cursor.getString(4)));
                routeModel.setNextQuestionnaireID(Integer.parseInt(cursor.getString(5)));
                routeModel.setMethod(Integer.parseInt(cursor.getString(6)));
                routeModel.setGroupMethod(Integer.parseInt(cursor.getString(7)));
                routeModel.setGroupZOrder(Integer.parseInt(cursor.getString(8)));
                routeModel.setResponseValue(cursor.getString(9));
                routeModel.setGroupMethodName(cursor.getString(10));
                routeModel.setMethodName(cursor.getString(11));

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

    /**
     * Deleting all Route by projectID
     */
    public void deleteAllRoutes(int projectID) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ROUTE,
                KEY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectID)});

        // 3. close
        db.close();
    }

}
