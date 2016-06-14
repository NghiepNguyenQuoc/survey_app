package com.nghiepnguyen.survey.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nghiepnguyen.survey.model.AnswerModel;
import com.nghiepnguyen.survey.model.RouteModel;
import com.nghiepnguyen.survey.model.SaveAnswerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nghiep on 4/9/16.
 */
public class AnswerSQLiteHelper extends MySQLiteHelper {

    // Table Names
    public static final String TABLE_ANSWER = "answer";

    // TABLE_QUESTIONNAIRE Columns names
    private static final String KEY_IDENTITY = "identity";
    private static final String KEY_FULL_NAME = "FullName";
    private static final String KEY_NUMBER_ID = "NumberID";
    private static final String KEY_PHONE_NUMBER = "PhoneNumber";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_PROJECT_ID = "ProjectID";
    private static final String KEY_IS_COMPELETED = "IsCompeleted";
    private static final String KEY_DATA = "Data";

    // TABLE_QUESTIONNAIRE table create statement
    public static final String CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_ANSWER + " ( " +
            KEY_IDENTITY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_FULL_NAME + " STRING, " +
            KEY_NUMBER_ID + " STRING, " +
            KEY_PHONE_NUMBER + " STRING, " +
            KEY_ADDRESS + " STRING, " +
            KEY_EMAIL + " STRING, " +
            KEY_PROJECT_ID + " INTEGER, " +
            KEY_IS_COMPELETED + " INTEGER, " +
            KEY_DATA + " STRING)";

    public AnswerSQLiteHelper(Context context) {
        super(context);
    }


    // ------------------------ Answer table methods ----------------//

    /**
     * Creating a Answer
     */
    public void addAnswer(SaveAnswerModel saveAnswerModel) {
        Log.d("saveAnswerModel", saveAnswerModel.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_FULL_NAME, saveAnswerModel.getFullName());
        values.put(KEY_NUMBER_ID, saveAnswerModel.getNumberID());
        values.put(KEY_PHONE_NUMBER, saveAnswerModel.getPhoneNumber());
        values.put(KEY_ADDRESS, saveAnswerModel.getAddress());
        values.put(KEY_EMAIL, saveAnswerModel.getEmail());
        values.put(KEY_PROJECT_ID, saveAnswerModel.getProjectID());
        values.put(KEY_IS_COMPELETED, saveAnswerModel.getIsCompeleted());
        values.put(KEY_DATA, saveAnswerModel.getData());

        // 3. insert
        db.insert(TABLE_ANSWER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    /**
     * get SaveAnswerModels
     */
    public SaveAnswerModel getSaveAnswerModelByProjectId(int id) {
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ANSWER + " WHERE " + KEY_PROJECT_ID + "=" + id + " ORDER BY " + KEY_IDENTITY + " ASC LIMIT 1";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build SaveAnswerModel and add it to list
        SaveAnswerModel saveAnswerModel = null;
        if (cursor.moveToFirst()) {

            saveAnswerModel = new SaveAnswerModel();
            saveAnswerModel.setIdentity(Integer.parseInt(cursor.getString(0)));
            saveAnswerModel.setFullName(cursor.getString(1));
            saveAnswerModel.setNumberID(cursor.getString(2));
            saveAnswerModel.setPhoneNumber(cursor.getString(3));
            saveAnswerModel.setAddress(cursor.getString(4));
            saveAnswerModel.setEmail(cursor.getString(5));
            saveAnswerModel.setProjectID(Integer.parseInt(cursor.getString(6)));
            saveAnswerModel.setIsCompeleted(Integer.parseInt(cursor.getString(7)));
            saveAnswerModel.setData(cursor.getString(8));
        }

        // return saveAnswerModels
        return saveAnswerModel;

    }

    /**
     * Deleting a Answer
     */
    public void deleteAnswer(int identity) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ANSWER,
                KEY_IDENTITY + " = ?",
                new String[]{String.valueOf(identity)});

        // 3. close
        db.close();
    }
}
