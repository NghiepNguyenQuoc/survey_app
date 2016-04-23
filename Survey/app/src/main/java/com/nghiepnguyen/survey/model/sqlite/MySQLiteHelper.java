package com.nghiepnguyen.survey.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 08670_000 on 05/04/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SurveyManager";

    // Table Names
    public static final String TABLE_QUESTIONNAIRE = "questionnaire";
    public static final String TABLE_ROUTE = "route";
    public static final String TABLE_PROJECT = "project";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create questionnaireModel table
        db.execSQL(ProjectSQLiteHelper.CREATE_PROJECT_TABLE);
        db.execSQL(QuestionaireSQLiteHelper.CREATE_QUESTIONNAIRE_TABLE);
        db.execSQL(RouteSQLiteHelper.CREATE_ROUTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older questionnaireModels table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONNAIRE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE);

        // create fresh questionnaireModels table
        this.onCreate(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
