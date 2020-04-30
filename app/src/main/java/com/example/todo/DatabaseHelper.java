package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabseHelper";
    public static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    //--------------------------TOD_O table -------------------------//
    public static final String TABLE_NAME_TODO = "todo";
    public static final String ID_FIELD_NAME_TODO = "id";
    public static final String ID_FIELD_TYPE_TODO = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String TITLE_FIELD_NAME_TODO = "title";
    public static final String TITLE_FIELD_TYPE_TODO = "TEXT";
    public static final String DESCR_FIELD_NAME_TODO = "description";
    public static final String DESCR_FIELD_TYPE_TODO = "TEXT";
    public static final String DATE_FIELD_NAME_TODO = "date";
    public static final String DATE_FIELD_TYPE_TODO = "TEXT";
    public static final String PRIORITY_ID_FIELD_NAME_TODO = "priority_id";
    public static final String PRIORITY_ID_FIELD_TYPE_TODO = "INTEGER";

    //--------------------------TODO_CATEGORY table -------------------------//
    public static final String TABLE_NAME_TODO_CATEGORY = "todo_category";
    public static final String TODO_ID_FIELD_NAME_TODO_CATEGORY = "todo_id";
    public static final String TODO_ID_FIELD_TYPE_TODO_CATEGORY = "INTEGER";
    public static final String CATEGORY_ID_FIELD_NAME_TODO_CATEGORY = "category_id";
    public static final String CATEGORY_ID_FIELD_TYPE_TODO_CATEGORY = "INTEGER";


    //--------------------------CATEGORY table -------------------------//
    public static final String TABLE_NAME_CATEGORY = "category";
    public static final String ID_FIELD_NAME_CATEGORY = "id";
    public static final String ID_FIELD_TYPE_CATEGORY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String CATEGORY_FIELD_NAME_CATEGORY = "name";
    public static final String CATEGORY_FIELD_TYPE_CATEGORY = "TEXT";

    //--------------------------PRIORITY table -------------------------//
    public static final String TABLE_NAME_PRIORITY = "priority";
    public static final String ID_FIELD_NAME_PRIORITY = "id";
    public static final String ID_FIELD_TYPE_PRIORITY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String FIELD_NAME_PRIORITY = "name";
    public static final String FIELD_TYPE_PRIORITY = "TEXT";

    private static final String TABLE_CREATE_TODO = "CREATE TABLE " + TABLE_NAME_TODO + "("
            + ID_FIELD_NAME_TODO + " " + ID_FIELD_TYPE_TODO + ", "
            + DATE_FIELD_NAME_TODO + " " + DATE_FIELD_TYPE_TODO + ", "
            + TITLE_FIELD_NAME_TODO + " " + TITLE_FIELD_TYPE_TODO + ", "
            + DESCR_FIELD_NAME_TODO + " " + DESCR_FIELD_TYPE_TODO + ", "
            + PRIORITY_ID_FIELD_NAME_TODO + " " + PRIORITY_ID_FIELD_TYPE_TODO + ")";

    private static final String TABLE_CREATE_CATEGORY = "CREATE TABLE " + TABLE_NAME_CATEGORY + "("
            + ID_FIELD_NAME_CATEGORY + " " + ID_FIELD_TYPE_CATEGORY + ", "
            + CATEGORY_FIELD_NAME_CATEGORY + " " + CATEGORY_FIELD_TYPE_CATEGORY + ")";

    private static final String TABLE_CREATE_TODO_CATEGORY = "CREATE TABLE " + TABLE_NAME_TODO_CATEGORY + "("
            + TODO_ID_FIELD_NAME_TODO_CATEGORY + " " + TODO_ID_FIELD_TYPE_TODO_CATEGORY + ", "
            + CATEGORY_ID_FIELD_NAME_TODO_CATEGORY + " " + CATEGORY_ID_FIELD_TYPE_TODO_CATEGORY + ")";

    private static final String TABLE_CREATE_PRIORITY = "CREATE TABLE " + TABLE_NAME_PRIORITY + "("
            + ID_FIELD_NAME_PRIORITY + " " + ID_FIELD_TYPE_PRIORITY + ", "
            + FIELD_NAME_PRIORITY + " " + FIELD_TYPE_PRIORITY + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TODO);
        db.execSQL(TABLE_CREATE_CATEGORY);
        db.execSQL(TABLE_CREATE_PRIORITY);
        db.execSQL(TABLE_CREATE_TODO_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PRIORITY);
        onCreate(db);
    }

    public boolean addData(String _date, String _title, String _description, int _prioID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_FIELD_NAME_TODO, _date);
        contentValues.put(TITLE_FIELD_NAME_TODO, _title);
        contentValues.put(DESCR_FIELD_NAME_TODO, _description);
        contentValues.put(PRIORITY_ID_FIELD_NAME_TODO, _prioID);
        long result = db.insert(TABLE_NAME_TODO, null, contentValues);
        return result != -1;
    }

    public Cursor getTodo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getToDo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + id + "'";
        return db.rawQuery(query, null);
    }



    /**
     * Selects id from the entry with specific title
     *
     * @param title - title string
     * @return - database entries
     */
    public Cursor getToDoID(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_TODO + " FROM " + TABLE_NAME_TODO + " WHERE " + TITLE_FIELD_NAME_TODO + " = '" + title + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Updates todo in the database.
     *
     * @param newTitle - new title string
     * @param id       - todo id
     */
    public void updateToDo(String newTitle, String newDescription, String newDatetime, int newPriority, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_TODO + " SET " + TITLE_FIELD_NAME_TODO + " = '" + newTitle + "', " + DESCR_FIELD_NAME_TODO + " = '" + newDescription + "', " + DATE_FIELD_NAME_TODO + " = '" + newDatetime + "', " + PRIORITY_ID_FIELD_NAME_TODO + " = '" + newPriority + "' WHERE " + ID_FIELD_NAME_TODO + " = '" + id + "'";
        Log.d(TAG, "updateToDo: query: " + query);
        Log.d(TAG, "updateToDo: Updating entry with id: " + id + " to " + newTitle);
        db.execSQL(query);
    }

    /**
     * Deletes todo with specific id from database.
     *
     * @param id - todo id
     */
    public void deleteToDo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TODO + " WHERE " + ID_FIELD_NAME_TODO + " = '" + id + "'";
        Log.d(TAG, "deleteToDo: query: " + query);
        Log.d(TAG, "deleteToDo: Deleting entry with id: " + id + " from database");
        db.execSQL(query);
    }


    // ------------------------ "priority" table methods ----------------//

    /**
     * Adds new priority entry to the database.
     *
     * @param title - title string
     * @return - the row ID of the newly inserted row, or -1 if an error occurred
     */
    public boolean addPriority(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME_PRIORITY, title);

        long result = db.insert(TABLE_NAME_PRIORITY, null, contentValues);

        return result != -1;
    }

    /**
     * Updates priority entry with specific id in the database.
     *
     * @param newTitle - new title string
     * @param id       - priority id
     * @param oldTitle - old title string
     */
    public void updatePriority(String newTitle, int id, String oldTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_PRIORITY + " SET " + FIELD_NAME_PRIORITY + " = '" + newTitle + "' WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + id + "' AND " + FIELD_NAME_PRIORITY + " = '" + oldTitle + "'";
        Log.d(TAG, "updatePriority: query: " + query);
        Log.d(TAG, "updatePriority: Updating " + oldTitle + " to " + newTitle);
        db.execSQL(query);
    }

    /**
     * Deletes priority entry with specific id from the database.
     *
     * @param id    - priority id
     * @param title - title string
     */
    public void deletePriority(int id, String title) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_PRIORITY + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + id + "' AND " + FIELD_NAME_PRIORITY + " = '" + title + "'";
        Log.d(TAG, "deletePriority: query: " + query);
        Log.d(TAG, "deletePriority: Deleting " + title + " from database");
        db.execSQL(query);
    }

    /**
     * Selects all priority database entries.
     *
     * @return - priority database entries
     */
    public Cursor getPriority() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_PRIORITY;
        return db.rawQuery(query, null);
    }

    public Cursor getPriorityID(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_PRIORITY + " FROM " + TABLE_NAME_PRIORITY + " WHERE " + FIELD_NAME_PRIORITY + " = '" + title + "'";

        String selectQuery = "SELECT * FROM " + TABLE_NAME_PRIORITY + " WHERE " + ID_FIELD_TYPE_PRIORITY + " = '" + title + "'";
        Log.d("oppp", selectQuery);
        return db.rawQuery(query, null);

    }

    /**
     * Selects priority database entry with specific id.
     *
     * @param id - priority id
     * @return - priority database entry
     */
    public Cursor getPriority(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_PRIORITY + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + id + "'";
        return db.rawQuery(query, null);
    }


    // ------------------------ "category" table methods ----------------//

    /**
     * @param title - title string
     * @return
     */
    public boolean addCategory(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_FIELD_NAME_CATEGORY, title);

        long result = db.insert(TABLE_NAME_CATEGORY, null, contentValues);

        return result != -1;
    }

    /**
     * @param newTitle - new title string
     * @param id       - category id
     * @param oldTitle - old title string
     */
    public void updateCategory(String newTitle, int id, String oldTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_CATEGORY + " SET " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + newTitle + "' WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + id + "' AND " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + oldTitle + "'";
        Log.d(TAG, "updatePriority: query: " + query);
        Log.d(TAG, "updatePriority: Updating " + oldTitle + " to " + newTitle);
        db.execSQL(query);
    }

    /**
     * Deletes category with specific id from database.
     *
     * @param id    - category id
     * @param title - title string
     */
    public void deleteCategory(int id, String title) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_CATEGORY + " WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + id + "' AND " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + title + "'";
        Log.d(TAG, "deletePriority: query: " + query);
        Log.d(TAG, "deletePriority: Deleting " + title + " from database");
        db.execSQL(query);
    }

    /**
     * Selects all category entries from database.
     *
     * @return - selected database entries
     */
    public Cursor getCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_CATEGORY;
        return db.rawQuery(query, null);
    }

    /**
     * Selects category entry with specific id from database.
     *
     * @param id - category id
     * @return - selected category
     */
    public Cursor getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_CATEGORY + " WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + id + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Selects category id from the entry with specific title from database.
     *
     * @param title - title string
     * @return - selected category id
     */
    public Cursor getCategoryID(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_PRIORITY + " FROM " + TABLE_NAME_CATEGORY + " WHERE " + FIELD_NAME_PRIORITY + " = '" + title + "'";
        return db.rawQuery(query, null);
    }


    // ------------------------ "ToDoCategory" table methods ----------------//

    /**
     * Adds a new entry into the database.
     *
     * @param todoID     - id of the todo entry
     * @param categoryID - id of the category entry
     * @return - the row ID of the newly inserted row, or -1 if an error occurred
     */
    public boolean addTodoCategory(int todoID, int categoryID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_ID_FIELD_NAME_TODO_CATEGORY, todoID);
        contentValues.put(CATEGORY_ID_FIELD_NAME_TODO_CATEGORY, categoryID);

        long result = db.insert(TABLE_NAME_TODO_CATEGORY, null, contentValues);

        return result != -1;
    }


    /**
     * Selects entries with specific todo id.
     *
     * @param todoID - id of the todo entry
     * @return - selected entries
     */
    public Cursor getToDoCategory(int todoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO_CATEGORY + " WHERE " + TODO_ID_FIELD_NAME_TODO_CATEGORY + " = '" + todoID + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Removes entries with specific todo id.
     *
     * @param todoID - id of the todo entry
     */
    public void deleteToDoCategories(int todoID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TODO_CATEGORY + " WHERE " + TODO_ID_FIELD_NAME_TODO_CATEGORY + " = '" + todoID + "'";
        Log.d(TAG, "deleteToDoCategories: query: " + query);
        Log.d(TAG, "deleteToDoCategories: Removing category connection to the todo " + todoID + " from database");
        db.execSQL(query);
    }



}
