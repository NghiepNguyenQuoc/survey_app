package com.nghiepnguyen.survey.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import com.nghiepnguyen.survey.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghiep on 4/9/16.
 */
public class ProjectSQLiteHelper extends MySQLiteHelper {

    // TABLE_PROJECT Columns names
    private static final String KEY_PROJECT_INDEX = "ProjectIndex";
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "Name";
    private static final String KEY_DESCRIPTION = "Description";
    private static final String KEY_IMAGE_URL = "ImageUrl";

    // Table Create Statements

    // TABLE_PROJECT table create statement
    public static final String CREATE_PROJECT_TABLE = "CREATE TABLE " + TABLE_PROJECT + " ( " +
            KEY_PROJECT_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_ID + " INTEGER, " +
            KEY_NAME + " TEXT, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_IMAGE_URL + " TEXT)";

    public ProjectSQLiteHelper(Context context) {
        super(context);
    }


    // ------------------------ Project table methods ----------------//

    /**
     * Creating a Project
     */
    public void addProject(ProjectModel projectModel) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        if (!checkExistProject(projectModel.getID())) {

            // 2. create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(KEY_ID, projectModel.getID());
            values.put(KEY_NAME, projectModel.getName());
            values.put(KEY_DESCRIPTION, projectModel.getDescription());
            values.put(KEY_IMAGE_URL, projectModel.getImage1());

            // 3. insert
            db.insert(TABLE_PROJECT, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            // 4. close
            db.close();
        }
    }

    /**
     * check exist project
     */
    public boolean checkExistProject(int projectId) {

        // 1. build the query
        String query = "SELECT count(*)" +
                " FROM " + TABLE_PROJECT +
                " WHERE " + KEY_ID + " = " + projectId +
                " GROUP BY " + KEY_ID;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build questionnaireModel and add it to list
        int id;
        if (cursor.moveToFirst())
            if (Integer.parseInt(cursor.getString(0)) > 0)
                return true;
        return false;
    }

    /**
     * getting all projects
     */
    public List<ProjectModel> getAllProject() {
        List<ProjectModel> projectModels = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PROJECT;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build project and add it to list
        ProjectModel projectModel = null;
        if (cursor.moveToFirst()) {
            do {
                projectModel = new ProjectModel(Parcel.obtain());
                projectModel.setID(Integer.parseInt(cursor.getString(1)));
                projectModel.setName(cursor.getString(2));
                projectModel.setDescription(cursor.getString(3));
                projectModel.setImage1(cursor.getString(4));

                projectModels.add(projectModel);
            } while (cursor.moveToNext());
        }

        return projectModels;
    }


    /**
     * Updating a project
     */
    public int updateProject(ProjectModel projectModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, projectModel.getID());
        values.put(KEY_NAME, projectModel.getName());
        values.put(KEY_DESCRIPTION, projectModel.getDescription());
        values.put(KEY_IMAGE_URL, projectModel.getImage1());


        // 3. updating row
        int i = db.update(TABLE_PROJECT, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(projectModel.getID())}); //selection args

        // 4. close
        db.close();

        return i;

    }

    /**
     * Deleting a project
     */
    public void deleteProject(ProjectModel projectModel) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_PROJECT,
                KEY_ID + " = ?",
                new String[]{String.valueOf(projectModel.getID())});

        // 3. close
        db.close();
    }
}
