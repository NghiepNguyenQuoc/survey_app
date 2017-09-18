package com.nghiepnguyen.survey.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;

import com.nghiepnguyen.survey.model.QuestionnaireModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghiep on 4/9/16.
 */
public class QuestionaireSQLiteHelper extends MySQLiteHelper {

    // Table Names
    public static final String TABLE_QUESTIONNAIRE = "questionnaire";

    // TABLE_QUESTIONNAIRE Columns names
    private static final String KEY_IDENTITY = "identity";
    private static final String KEY_ID = "ID";
    private static final String KEY_PROJECT_ID = "ProjectID";
    private static final String KEY_QUESTIONNAIRE_ID = "QuestionnaireID";
    private static final String KEY_DEPENDENT_ID = "DependentID";
    private static final String KEY_PARENT_ID = "ParentID";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_ZORDERQUESTION = "ZOrderQuestion";
    private static final String KEY_VALUE = "Value";
    private static final String KEY_ALLOW_INPUT_TEXT = "AllowInputText";
    private static final String KEY_IS_SELECTED = "isSelected";
    private static final String KEY_MAXRESPONSECOUNT = "MaxResponseCount";
    private static final String KEY_EXCLUSION = "exclusion";
    private static final String KEY_DEPENDENT_TYPE = "DependentType";
    private static final String KEY_DISPLAY_RANDOM_RESPONSE = "DisplayRandomResponse";
    private static final String KEY_FLAG_GPS = "flagGPS";
    private static final String KEY_FLAG_QUE_RECORD = "FlagQueRecord";
    private static final String KEY_MAX_TIME = "MaxTime";
    private static final String KEY_CODE = "Code";
    private static final String KEY_QUESTIONTEXT = "QuestionText";
    private static final String KEY_CAPTION = "Caption";
    private static final String KEY_DESCRIPTION = "Description";
    private static final String KEY_ZORDEROPTION = "ZOrderOption";
    private static final String KEY_OTHEROPTION = "otherOption";

    // Table Create Statements

    // TABLE_QUESTIONNAIRE table create statement
    public static final String CREATE_QUESTIONNAIRE_TABLE = "CREATE TABLE " + TABLE_QUESTIONNAIRE + " ( " +
            KEY_IDENTITY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_ID + " INTEGER, " +
            KEY_PROJECT_ID + " INTEGER, " +
            KEY_QUESTIONNAIRE_ID + " INTEGER, " +
            KEY_DEPENDENT_ID + " INTEGER, " +
            KEY_PARENT_ID + " INTEGER, " +
            KEY_TYPE + " INTEGER, " +
            KEY_ZORDERQUESTION + " INTEGER, " +
            KEY_VALUE + " INTEGER, " +
            KEY_ALLOW_INPUT_TEXT + " INTEGER, " +
            KEY_IS_SELECTED + " INTEGER, " +
            KEY_MAXRESPONSECOUNT + " INTEGER, " +
            KEY_EXCLUSION + " INTEGER, " +
            KEY_DEPENDENT_TYPE + " INTEGER, " +
            KEY_DISPLAY_RANDOM_RESPONSE + " INTEGER, " +
            KEY_FLAG_GPS + " INTEGER, " +
            KEY_FLAG_QUE_RECORD + " INTEGER, " +
            KEY_MAX_TIME + " INTEGER, " +
            KEY_CODE + " TEXT, " +
            KEY_QUESTIONTEXT + " TEXT, " +
            KEY_CAPTION + " TEXT, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_ZORDEROPTION + " TEXT, " +
            KEY_OTHEROPTION + " TEXT)";

    public QuestionaireSQLiteHelper(Context context) {
        super(context);
    }


    // ------------------------ Questionnaire table methods ----------------//

    /**
     * Creating a Questionnaire
     */
    public void addQuestionnaire(QuestionnaireModel questionnaireModel, int projectId) {
        Log.d("questionnaireModel", questionnaireModel.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, questionnaireModel.getID());
        values.put(KEY_PROJECT_ID, projectId);
        values.put(KEY_TYPE, questionnaireModel.getType());
        values.put(KEY_QUESTIONNAIRE_ID, questionnaireModel.getQuestionnaireID());
        values.put(KEY_DEPENDENT_ID, questionnaireModel.getDependentID());
        values.put(KEY_PARENT_ID, questionnaireModel.getParentID());
        values.put(KEY_ZORDERQUESTION, questionnaireModel.getZOrderQuestion());
        values.put(KEY_VALUE, questionnaireModel.getValue());
        values.put(KEY_ALLOW_INPUT_TEXT, questionnaireModel.getAllowInputText());
        values.put(KEY_IS_SELECTED, questionnaireModel.getIsSelected());
        values.put(KEY_MAXRESPONSECOUNT, questionnaireModel.getMaxResponseCount());
        values.put(KEY_EXCLUSION, questionnaireModel.getExclusion());
        values.put(KEY_DEPENDENT_TYPE, questionnaireModel.getDependentType());
        values.put(KEY_DISPLAY_RANDOM_RESPONSE, questionnaireModel.getDisplayRandomResponse());
        values.put(KEY_FLAG_GPS, questionnaireModel.getFlagGPS());
        values.put(KEY_FLAG_QUE_RECORD, questionnaireModel.getFlagQueRecord());
        values.put(KEY_MAX_TIME, questionnaireModel.getMaxTime());
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_CAPTION, questionnaireModel.getCaption());
        values.put(KEY_DESCRIPTION, questionnaireModel.getDescription());
        values.put(KEY_ZORDEROPTION, questionnaireModel.getZOrderOption());
        values.put(KEY_OTHEROPTION, questionnaireModel.getOtherOption());

        // 3. insert
        db.insert(TABLE_QUESTIONNAIRE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    /**
     * get number record by projectid
     */
    public int getCountQuestionnaireByProjectId(int projectId) {
        // 1. build the query
        String query = "SELECT  count(*) FROM " + TABLE_QUESTIONNAIRE + " WHERE " + KEY_PROJECT_ID + "=" + projectId;

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
     * get Questionnaires
     */
    public List<QuestionnaireModel> getListQuestionnaireByQuestionId(int id) {
        List<QuestionnaireModel> questionnaireModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_QUESTIONNAIRE + " WHERE " + KEY_QUESTIONNAIRE_ID + "=" + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        QuestionnaireModel questionnaireModel = null;
        if (cursor.moveToFirst()) {
            do {
                questionnaireModel = new QuestionnaireModel(Parcel.obtain());
                questionnaireModel.setID(Integer.parseInt(cursor.getString(1)));
                questionnaireModel.setProjectID(Integer.parseInt(cursor.getString(2)));
                questionnaireModel.setQuestionnaireID(Integer.parseInt(cursor.getString(3)));
                questionnaireModel.setDependentID(Integer.parseInt(cursor.getString(4)));
                questionnaireModel.setParentID(Integer.parseInt(cursor.getString(5)));
                questionnaireModel.setType(Integer.parseInt(cursor.getString(6)));
                questionnaireModel.setZOrderQuestion(Integer.parseInt(cursor.getString(7)));
                questionnaireModel.setValue(Integer.parseInt(cursor.getString(8)));
                questionnaireModel.setAllowInputText(Integer.parseInt(cursor.getString(9)));
                questionnaireModel.setIsSelected(Integer.parseInt(cursor.getString(10)));
                questionnaireModel.setMaxResponseCount(Integer.parseInt(cursor.getString(11)));
                questionnaireModel.setExclusion(Integer.parseInt(cursor.getString(12)));
                questionnaireModel.setDependentType(Integer.parseInt(cursor.getString(13)));
                questionnaireModel.setDisplayRandomResponse(Integer.parseInt(cursor.getString(14)));
                questionnaireModel.setFlagGPS(Integer.parseInt(cursor.getString(15)));
                questionnaireModel.setFlagQueRecord(Integer.parseInt(cursor.getString(16)));
                questionnaireModel.setMaxTime(Integer.parseInt(cursor.getString(17)));
                questionnaireModel.setCode(cursor.getString(18));
                questionnaireModel.setQuestionText(cursor.getString(19));
                questionnaireModel.setCaption(cursor.getString(20));
                questionnaireModel.setDescription(cursor.getString(21));
                questionnaireModel.setZOrderOption(cursor.getString(22));
                questionnaireModel.setOtherOption(cursor.getString(23));

                // Add questionnaireModel to questionnaireModel
                questionnaireModels.add(questionnaireModel);
            } while (cursor.moveToNext());
        }

        // return questionnaireModels
        return questionnaireModels;
    }

    /**
     * get Questionnaires by parent questionaire ID
     */
    public List<QuestionnaireModel> getListQuestionnaireByParentQuestionId(int id) {
        List<QuestionnaireModel> questionnaireModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_QUESTIONNAIRE + " WHERE " + KEY_PARENT_ID + "=" + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        QuestionnaireModel questionnaireModel = null;
        if (cursor.moveToFirst()) {
            do {
                questionnaireModel = new QuestionnaireModel(Parcel.obtain());
                questionnaireModel.setID(Integer.parseInt(cursor.getString(1)));
                questionnaireModel.setProjectID(Integer.parseInt(cursor.getString(2)));
                questionnaireModel.setQuestionnaireID(Integer.parseInt(cursor.getString(3)));
                questionnaireModel.setDependentID(Integer.parseInt(cursor.getString(4)));
                questionnaireModel.setParentID(Integer.parseInt(cursor.getString(5)));
                questionnaireModel.setType(Integer.parseInt(cursor.getString(6)));
                questionnaireModel.setZOrderQuestion(Integer.parseInt(cursor.getString(7)));
                questionnaireModel.setValue(Integer.parseInt(cursor.getString(8)));
                questionnaireModel.setAllowInputText(Integer.parseInt(cursor.getString(9)));
                questionnaireModel.setIsSelected(Integer.parseInt(cursor.getString(10)));
                questionnaireModel.setMaxResponseCount(Integer.parseInt(cursor.getString(11)));
                questionnaireModel.setExclusion(Integer.parseInt(cursor.getString(12)));
                questionnaireModel.setDependentType(Integer.parseInt(cursor.getString(13)));
                questionnaireModel.setDisplayRandomResponse(Integer.parseInt(cursor.getString(14)));
                questionnaireModel.setFlagGPS(Integer.parseInt(cursor.getString(15)));
                questionnaireModel.setFlagQueRecord(Integer.parseInt(cursor.getString(16)));
                questionnaireModel.setMaxTime(Integer.parseInt(cursor.getString(17)));
                questionnaireModel.setCode(cursor.getString(18));
                questionnaireModel.setQuestionText(cursor.getString(19));
                questionnaireModel.setCaption(cursor.getString(20));
                questionnaireModel.setDescription(cursor.getString(21));
                questionnaireModel.setZOrderOption(cursor.getString(22));
                questionnaireModel.setOtherOption(cursor.getString(23));

                // Add questionnaireModel to questionnaireModel
                questionnaireModels.add(questionnaireModel);
            } while (cursor.moveToNext());
        }

        // return questionnaireModels
        return questionnaireModels;
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
                questionnaireModel = new QuestionnaireModel(Parcel.obtain());
                questionnaireModel.setID(Integer.parseInt(cursor.getString(1)));
                questionnaireModel.setProjectID(Integer.parseInt(cursor.getString(2)));
                questionnaireModel.setQuestionnaireID(Integer.parseInt(cursor.getString(3)));
                questionnaireModel.setDependentID(Integer.parseInt(cursor.getString(4)));
                questionnaireModel.setParentID(Integer.parseInt(cursor.getString(5)));
                questionnaireModel.setType(Integer.parseInt(cursor.getString(6)));
                questionnaireModel.setZOrderQuestion(Integer.parseInt(cursor.getString(7)));
                questionnaireModel.setValue(Integer.parseInt(cursor.getString(8)));
                questionnaireModel.setAllowInputText(Integer.parseInt(cursor.getString(9)));
                questionnaireModel.setIsSelected(Integer.parseInt(cursor.getString(10)));
                questionnaireModel.setMaxResponseCount(Integer.parseInt(cursor.getString(11)));
                questionnaireModel.setExclusion(Integer.parseInt(cursor.getString(12)));
                questionnaireModel.setDependentType(Integer.parseInt(cursor.getString(13)));
                questionnaireModel.setDisplayRandomResponse(Integer.parseInt(cursor.getString(14)));
                questionnaireModel.setFlagGPS(Integer.parseInt(cursor.getString(15)));
                questionnaireModel.setFlagQueRecord(Integer.parseInt(cursor.getString(16)));
                questionnaireModel.setMaxTime(Integer.parseInt(cursor.getString(17)));
                questionnaireModel.setCode(cursor.getString(18));
                questionnaireModel.setQuestionText(cursor.getString(19));
                questionnaireModel.setCaption(cursor.getString(20));
                questionnaireModel.setDescription(cursor.getString(21));
                questionnaireModel.setZOrderOption(cursor.getString(22));
                questionnaireModel.setOtherOption(cursor.getString(23));

                // Add questionnaireModel to questionnaireModel
                questionnaireModels.add(questionnaireModel);
            } while (cursor.moveToNext());
        }

        // return questionnaireModels
        return questionnaireModels;
    }


    /**
     * getting all QuestionID
     */
    public List<Integer> getAllQuestionIDByProjectId(int projectId) {
        List<Integer> questionIds = new ArrayList<>();

        // 1. build the query
        String query = "SELECT " + KEY_QUESTIONNAIRE_ID +
                " FROM " + TABLE_QUESTIONNAIRE +
                " WHERE " + KEY_PROJECT_ID + " = " + projectId +
                " GROUP BY " + KEY_QUESTIONNAIRE_ID +
                " ORDER BY " + KEY_IDENTITY;

        /*" AND " + KEY_QUESTIONNAIRE_ID + " != 4322" +
                " AND " + KEY_QUESTIONNAIRE_ID + " != 4323" +
                " AND " + KEY_QUESTIONNAIRE_ID + " != 4324" +
                " AND " + KEY_QUESTIONNAIRE_ID + " != 4357" +
                " AND " + KEY_QUESTIONNAIRE_ID + " != 4325" +*/

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        int id;
        if (cursor.moveToFirst()) {
            do {
                id = Integer.parseInt(cursor.getString(0));
                questionIds.add(id);
            } while (cursor.moveToNext());
        }

        // return questionIds
        return questionIds;
    }

    /**
     * Updating a Questionnaire
     */
    public int updatequestionnaireModel(QuestionnaireModel questionnaireModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, questionnaireModel.getID());
        values.put(KEY_PROJECT_ID, questionnaireModel.getProjectID());
        values.put(KEY_TYPE, questionnaireModel.getType());
        values.put(KEY_QUESTIONNAIRE_ID, questionnaireModel.getQuestionnaireID());
        values.put(KEY_DEPENDENT_ID, questionnaireModel.getDependentID());
        values.put(KEY_PARENT_ID, questionnaireModel.getParentID());
        values.put(KEY_ZORDERQUESTION, questionnaireModel.getZOrderQuestion());
        values.put(KEY_VALUE, questionnaireModel.getValue());
        values.put(KEY_ALLOW_INPUT_TEXT, questionnaireModel.getAllowInputText());
        values.put(KEY_IS_SELECTED, questionnaireModel.getIsSelected());
        values.put(KEY_MAXRESPONSECOUNT, questionnaireModel.getMaxResponseCount());
        values.put(KEY_EXCLUSION, questionnaireModel.getExclusion());
        values.put(KEY_DEPENDENT_TYPE, questionnaireModel.getDependentType());
        values.put(KEY_DISPLAY_RANDOM_RESPONSE, questionnaireModel.getDisplayRandomResponse());
        values.put(KEY_FLAG_GPS, questionnaireModel.getFlagGPS());
        values.put(KEY_FLAG_QUE_RECORD, questionnaireModel.getFlagQueRecord());
        values.put(KEY_MAX_TIME, questionnaireModel.getMaxTime());
        values.put(KEY_CODE, questionnaireModel.getCode());
        values.put(KEY_QUESTIONTEXT, questionnaireModel.getQuestionText());
        values.put(KEY_CAPTION, questionnaireModel.getCaption());
        values.put(KEY_DESCRIPTION, questionnaireModel.getDescription());
        values.put(KEY_ZORDEROPTION, questionnaireModel.getZOrderOption());
        values.put(KEY_OTHEROPTION, questionnaireModel.getOtherOption());

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

    /*
    * Delete all questionarie by projectID
    * */
    public void deleteAllQuestionnaire(int projectID) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_QUESTIONNAIRE,
                KEY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectID)});

        // 3. close
        db.close();
    }
}
