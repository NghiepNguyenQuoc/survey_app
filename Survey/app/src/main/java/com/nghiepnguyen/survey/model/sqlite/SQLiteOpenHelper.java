package com.nghiepnguyen.survey.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nghiepnguyen.survey.model.resultModel.QuestionnaireModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 08670_000 on 21/03/2016.
 */
class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SurveyDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create questionnaireModel table
        String CREATE_QUESTIONNAIRE_TABLE = "CREATE TABLE questionnaire ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "parentID integer, " +
                "projectID integer, " +
                "type integer, " +
                "zOrder integer, " +
                "status integer, " +
                "code TEXT, " +
                "questionText TEXT, " +
                "typeCode TEXT, " +
                "typeName TEXT)";
        // create questionnaireModel table
        db.execSQL(CREATE_QUESTIONNAIRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older questionnaireModels table if existed
        db.execSQL("DROP TABLE IF EXISTS questionnaire");

        // create fresh questionnaireModels table
        this.onCreate(db);
    }

    /**
     * CRUD operations (create "add", read "get", update, delete) questionnaireModel + get all questionnaireModels + delete all questionnaireModels
     */

    // questionnaireModels table name
    private static final String TABLE_QUESTIONNAIRE = "questionnaire";

    // questionnaireModels Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PARENTID = "parentID";
    private static final String KEY_PROJECTID = "projectID";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ZORDER = "zOrder";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CODE = "code";
    private static final String KEY_QUESTIONTEXT = "questionText";
    private static final String KEY_TYPECODE = "typeCode";
    private static final String KEY_TYPENAME = "typeName";

    private static final String[] COLUMNS = {KEY_ID, KEY_PARENTID, KEY_PROJECTID,
            KEY_TYPE, KEY_ZORDER, KEY_STATUS,
            KEY_CODE, KEY_QUESTIONTEXT, KEY_TYPECODE, KEY_TYPENAME};

    public void addQuestionnaire(QuestionnaireModel questionnaireModel) {
        Log.d("questionnaireModel", questionnaireModel.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PARENTID, questionnaireModel.getParentID());
        values.put(KEY_PROJECTID, questionnaireModel.getProjectID());
        values.put(KEY_TYPE, questionnaireModel.getType());
        values.put(KEY_ZORDER, questionnaireModel.getZOrder());
        values.put(KEY_STATUS, questionnaireModel.getStatus());
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_TYPECODE, questionnaireModel.getTypeCode());
        values.put(KEY_TYPENAME, questionnaireModel.getTypeName());

        // 3. insert
        db.insert(TABLE_QUESTIONNAIRE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public QuestionnaireModel getQuestionnaireModel(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_QUESTIONNAIRE, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build questionnaireModel object
        QuestionnaireModel questionnaireModel = new QuestionnaireModel();
        questionnaireModel.setID(Integer.parseInt(cursor.getString(0)));
        questionnaireModel.setParentID(Integer.parseInt(cursor.getString(1)));
        questionnaireModel.setProjectID(Integer.parseInt(cursor.getString(2)));
        questionnaireModel.setType(Integer.parseInt(cursor.getString(3)));
        questionnaireModel.setZOrder(Integer.parseInt(cursor.getString(4)));
        questionnaireModel.setStatus(Integer.parseInt(cursor.getString(5)));
        questionnaireModel.setCode(cursor.getString(6));
        questionnaireModel.setQuestionText(cursor.getString(7));
        questionnaireModel.setTypeCode(cursor.getString(8));
        questionnaireModel.setTypeName(cursor.getString(9));

        Log.d("getQuestionnaire(" + id + ")", questionnaireModel.toString());

        // 5. return questionnaire
        return questionnaireModel;
    }

    // Get All QuestionnaireModels
    public List<QuestionnaireModel> getAllquestionnaireModels() {
        List<QuestionnaireModel> questionnaireModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_QUESTIONNAIRE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        QuestionnaireModel questionnaireModel = null;
        if (cursor.moveToFirst()) {
            do {
                questionnaireModel = new QuestionnaireModel();


                questionnaireModel.setID(Integer.parseInt(cursor.getString(0)));
                questionnaireModel.setParentID(Integer.parseInt(cursor.getString(1)));
                questionnaireModel.setProjectID(Integer.parseInt(cursor.getString(2)));
                questionnaireModel.setType(Integer.parseInt(cursor.getString(3)));
                questionnaireModel.setZOrder(Integer.parseInt(cursor.getString(4)));
                questionnaireModel.setStatus(Integer.parseInt(cursor.getString(5)));
                questionnaireModel.setCode(cursor.getString(6));
                questionnaireModel.setQuestionText(cursor.getString(7));
                questionnaireModel.setTypeCode(cursor.getString(8));
                questionnaireModel.setTypeName(cursor.getString(9));

                // Add questionnaireModel to questionnaireModel
                questionnaireModels.add(questionnaireModel);
            } while (cursor.moveToNext());
        }

        Log.d("getAllQuestionnaire()", questionnaireModels.toString());

        // return questionnaireModels
        return questionnaireModels;
    }

    // Updating single questionnaireModel
    public int updatequestionnaireModel(QuestionnaireModel questionnaireModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PARENTID, questionnaireModel.getParentID());
        values.put(KEY_PROJECTID, questionnaireModel.getProjectID());
        values.put(KEY_TYPE, questionnaireModel.getType());
        values.put(KEY_ZORDER, questionnaireModel.getZOrder());
        values.put(KEY_STATUS, questionnaireModel.getStatus());
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_TYPECODE, questionnaireModel.getTypeCode());
        values.put(KEY_TYPENAME, questionnaireModel.getTypeName());

        // 3. updating row
        int i = db.update(TABLE_QUESTIONNAIRE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(questionnaireModel.getID())}); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single questionnaireModel
    public void deletequestionnaireModels(QuestionnaireModel questionnaireModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_QUESTIONNAIRE,
                KEY_ID + " = ?",
                new String[]{String.valueOf(questionnaireModel.getID())});

        // 3. close
        db.close();

        Log.d("deletequestionnaire", questionnaireModel.toString());

    }

}
