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
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SurveyManager";

    // Table Names
    private static final String TABLE_QUESTIONNAIRE = "questionnaire";
    private static final String TABLE_ROUTE = "route";
    private static final String TABLE_QUESTION = "question";

    // TABLE_QUESTIONNAIRE Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_CODE = "Code";
    private static final String KEY_QUESTIONTEXT = "QuestionText";
    private static final String KEY_ZORDER = "ZOrder";
    private static final String KEY_VALUE = "Value";
    private static final String KEY_CAPTION = "Caption";
    private static final String KEY_DESCRIPTION = "Description";

    private static final String[] COLUMNS = {KEY_ID, KEY_CODE, KEY_QUESTIONTEXT,
            KEY_ZORDER, KEY_VALUE, KEY_CAPTION, KEY_DESCRIPTION};

    // Table Create Statements
    // TABLE_QUESTIONNAIRE table create statement
    private static final String CREATE_QUESTIONNAIRE_TABLE = "CREATE TABLE " + TABLE_QUESTIONNAIRE + " ( " +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Code TEXT, " +
            "QuestionText TEXT, " +
            "zOrder integer, " +
            "Value integer, " +
            "Caption TEXT, " +
            "Description TEXT)";

    // TABLE_ROUTE table create statement
    // TABLE_QUESTION table create statement


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create questionnaireModel table
        db.execSQL(CREATE_QUESTIONNAIRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older questionnaireModels table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONNAIRE);

        // create fresh questionnaireModels table
        this.onCreate(db);
    }

    // ------------------------ Questionnaire table methods ----------------//

    /**
     * Creating a Questionnaire
     */
    public void addQuestionnaire(QuestionnaireModel questionnaireModel) {
        Log.d("questionnaireModel", questionnaireModel.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_ZORDER, questionnaireModel.getZOrder());
        values.put(KEY_VALUE, questionnaireModel.getValue());
        values.put(KEY_CAPTION, questionnaireModel.getCaption());
        values.put(KEY_DESCRIPTION, questionnaireModel.getDescription());

        // 3. insert
        db.insert(TABLE_QUESTIONNAIRE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    /**
     * get single Questionnaire
     */
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
        questionnaireModel.setCode(cursor.getString(1));
        questionnaireModel.setQuestionText(cursor.getString(2));
        questionnaireModel.setZOrder(Integer.parseInt(cursor.getString(3)));
        questionnaireModel.setValue(Integer.parseInt(cursor.getString(4)));
        questionnaireModel.setCaption(cursor.getString(5));
        questionnaireModel.setDescription(cursor.getString(6));

        Log.d("getQuestionnaire(" + id + ")", questionnaireModel.toString());

        // 5. return questionnaire
        return questionnaireModel;
    }

    /**
     * getting all Questionnaires
     */
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
                questionnaireModel.setCode(cursor.getString(1));
                questionnaireModel.setQuestionText(cursor.getString(2));
                questionnaireModel.setZOrder(Integer.parseInt(cursor.getString(3)));
                questionnaireModel.setValue(Integer.parseInt(cursor.getString(4)));
                questionnaireModel.setCaption(cursor.getString(5));
                questionnaireModel.setDescription(cursor.getString(6));

                // Add questionnaireModel to questionnaireModel
                questionnaireModels.add(questionnaireModel);
            } while (cursor.moveToNext());
        }

        Log.d("getAllQuestionnaire()", questionnaireModels.toString());

        // return questionnaireModels
        return questionnaireModels;
    }

    /**
     * Updating a Questionnaire
     */
    public int updatequestionnaireModel(QuestionnaireModel questionnaireModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_ZORDER, questionnaireModel.getZOrder());
        values.put(KEY_VALUE, questionnaireModel.getValue());
        values.put(KEY_CAPTION, questionnaireModel.getCaption());
        values.put(KEY_DESCRIPTION, questionnaireModel.getDescription());

        // 3. updating row
        int i = db.update(TABLE_QUESTIONNAIRE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(questionnaireModel.getID())}); //selection args

        // 4. close
        db.close();

        return i;

    }

    /**
     * Deleting a Questionnaire
     */
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

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
