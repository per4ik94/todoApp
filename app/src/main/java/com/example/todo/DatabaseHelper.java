package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabseHelper";
    public static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    //--------------------------TODO_TABELLE-------------------------//
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

    //--------------------------TODO_KATEGORIE TABELLE-------------------------//
    public static final String TABLE_NAME_TODO_CATEGORY = "todo_category";
    public static final String TODO_ID_FIELD_NAME_TODO_CATEGORY = "todo_id";
    public static final String TODO_ID_FIELD_TYPE_TODO_CATEGORY = "INTEGER";
    public static final String CATEGORY_ID_FIELD_NAME_TODO_CATEGORY = "category_id";
    public static final String CATEGORY_ID_FIELD_TYPE_TODO_CATEGORY = "INTEGER";


    //--------------------------KATEGORIE TABELLE -------------------------//
    public static final String TABLE_NAME_CATEGORY = "category";
    public static final String ID_FIELD_NAME_CATEGORY = "id";
    public static final String ID_FIELD_TYPE_CATEGORY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String CATEGORY_FIELD_NAME_CATEGORY = "name";
    public static final String CATEGORY_FIELD_TYPE_CATEGORY = "TEXT";

    //--------------------------PRIORITAET TABELLE -------------------------//
    public static final String TABLE_NAME_PRIORITY = "priority";
    public static final String ID_FIELD_NAME_PRIORITY = "id";
    public static final String ID_FIELD_TYPE_PRIORITY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String FIELD_NAME_PRIORITY = "name";
    public static final String FIELD_TYPE_PRIORITY = "TEXT";

    //-----------------------------TABELLE ERSTELLEN -----------------------//
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

    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TODO);
        db.execSQL(TABLE_CREATE_CATEGORY);
        db.execSQL(TABLE_CREATE_PRIORITY);
        db.execSQL(TABLE_CREATE_TODO_CATEGORY);
    }

    /**
     * Alte Tabellen Loeschen und neue erstellen.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PRIORITY);
        onCreate(db);
    }

    /**
     * Ablegen der Todo_ Daten in eine Tabelle.
     *
     * @param _titel        Todo_ Titel.
     * @param _beschreibung Todo_ Beschreibung.
     * @param _datum        Todo_ Datum.
     * @param _prioID       Todo_ Prioritaet ID.
     * @return
     */
    public boolean addData(String _titel, String _beschreibung, String _datum, int _prioID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_FIELD_NAME_TODO, _datum);
        contentValues.put(TITLE_FIELD_NAME_TODO, _titel);
        contentValues.put(DESCR_FIELD_NAME_TODO, _beschreibung);
        contentValues.put(PRIORITY_ID_FIELD_NAME_TODO, _prioID);
        long result = db.insert(TABLE_NAME_TODO, null, contentValues);
        return result != -1;
    }

    /**
     * Waehlt alle Todo_ Elemente aus.
     *
     * @return Datenbank Eintraege.
     */
    public Cursor getTodo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    /**
     * Waehlt ein bestimmtes Todo_ aus.
     *
     * @param id von Todo_ Element.
     * @return Datenbank Eintrag.
     */
    public Cursor getToDo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + id + "'";
        return db.rawQuery(query, null);
    }


    /**
     * Waehlt die ID eines bestimmten Todo_.
     *
     * @param _titel Todo_ Titel.
     * @return Datenbank Eintrag.
     */
    public Cursor getToDoID(String _titel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_TODO + " FROM " + TABLE_NAME_TODO + " WHERE " + TITLE_FIELD_NAME_TODO + " = '" + _titel + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Aktualisiert die Daten einer bestimmten Todo_.
     *
     * @param _neuerTitel       Neuer Titel.
     * @param _neueBeschreibung Neue Beschreibung.
     * @param _neuesDatum       Neues Datum.
     * @param _neuePrio         Neue Prioritaet.
     * @param _todoID           Todo_ID die Aktualisiert wird.
     */
    public void updateToDo(String _neuerTitel, String _neueBeschreibung, String _neuesDatum, int _neuePrio, int _todoID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_TODO + " SET " + TITLE_FIELD_NAME_TODO + " = '" + _neuerTitel + "', " + DESCR_FIELD_NAME_TODO + " = '" + _neueBeschreibung + "', " + DATE_FIELD_NAME_TODO + " = '" + _neuesDatum + "', " + PRIORITY_ID_FIELD_NAME_TODO + " = '" + _neuePrio + "' WHERE " + ID_FIELD_NAME_TODO + " = '" + _todoID + "'";
        Log.d(TAG, "updateToDo: " + _todoID + " ID wird umbenannt zu " + _neuerTitel);
        db.execSQL(query);
    }

    /**
     * Loescht ein bestimmtes Todo_.
     *
     * @param id Todo_ ID.
     */
    public void deleteToDo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TODO + " WHERE " + ID_FIELD_NAME_TODO + " = '" + id + "'";
        Log.d(TAG, "deleteToDo: TODO ID: " + id + " gelöscht");
        db.execSQL(query);
    }


    /**
     * Legt Prioritaet mit dem eingegebenem Namen in der Datenbank ab.
     *
     * @param _prio Prioritaet Name.
     * @return ID der neuen Reihe oder -1 bei fehler.
     */
    public boolean addPrioritaet(String _prio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME_PRIORITY, _prio);
        long result = db.insert(TABLE_NAME_PRIORITY, null, contentValues);
        return result != -1;
    }

    /**
     * Aktualisiert den Prioritaet Namen einer bestimmten ID.
     *
     * @param _neuePrioritaetTitel Neuer Prioritaet Name.
     * @param _prioID              Prioritaet ID.
     * @param _oldPrioritaetTitel  Alter Prioritaet Name.
     */
    public void updatePrioritaet(String _neuePrioritaetTitel, int _prioID, String _oldPrioritaetTitel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_PRIORITY + " SET " + FIELD_NAME_PRIORITY + " = '" + _neuePrioritaetTitel + "' WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + _prioID + "' AND " + FIELD_NAME_PRIORITY + " = '" + _oldPrioritaetTitel + "'";
        Log.d(TAG, "updatePrioritaet: von" + _oldPrioritaetTitel + " zu " + _neuePrioritaetTitel);
        db.execSQL(query);
    }

    /**
     * Loescht eine Prioritaet mit einer bestimmten ID.
     *
     * @param _prioID         Prioritaet ID.
     * @param _prioritaetName Prioritaet Name.
     */
    public void deletePrioritaet(int _prioID, String _prioritaetName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_PRIORITY + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + _prioID + "' AND " + FIELD_NAME_PRIORITY + " = '" + _prioritaetName + "'";
        Log.d(TAG, "deletePrioritaet: " + _prioritaetName + " Geloescht");
        db.execSQL(query);
    }

    /**
     * Waehlt alle Prioritaet Eintraege aus.
     *
     * @return Datenbank Eintraege.
     */
    public Cursor getPrioritaet() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_PRIORITY;
        return db.rawQuery(query, null);
    }

    /**
     * Waehlt eine Prioritaet mit einem bestimmten Namen.
     *
     * @param _prioritaetName Prioritaet Name.
     * @return Datenbank Eintrag.
     */
    public Cursor getPrioritaetID(String _prioritaetName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_PRIORITY + " FROM " + TABLE_NAME_PRIORITY + " WHERE " + FIELD_NAME_PRIORITY + " = '" + _prioritaetName + "'";
        //Log.d(TAG, "getPrioritaetID: " + query);
        return db.rawQuery(query, null);
    }

    /**
     * Waehlt eine Prioritaet mit einer bestimmten ID.
     *
     * @param _prioID Prioritaet ID.
     * @return Datenbank Eintrag.
     */
    public Cursor getPrioritaet(int _prioID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_PRIORITY + " WHERE " + ID_FIELD_NAME_PRIORITY + " = '" + _prioID + "'";
        //Log.d(TAG, "getPrioritaet: " + query);
        return db.rawQuery(query, null);
    }

    /**
     * Legt einen Eintrag in der TodoKategorie Tabelle ab.
     *
     * @param _todoID      Todo_ ID
     * @param _kategorieID Kategorie ID.
     * @return neu erzeugte Reihe oder -1 bei Fehler.
     */
    public boolean addTodoKategorie(int _todoID, int _kategorieID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_ID_FIELD_NAME_TODO_CATEGORY, _todoID);
        contentValues.put(CATEGORY_ID_FIELD_NAME_TODO_CATEGORY, _kategorieID);
        long result = db.insert(TABLE_NAME_TODO_CATEGORY, null, contentValues);
        return result != -1;
    }

    /**
     * Waehlt einen Eintrag mit einer bestimmten ID.
     *
     * @param _todoID Todo_ ID.
     * @return Datenbank Eintrag.
     */
    public Cursor getTodoKategorie(int _todoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TODO_CATEGORY + " WHERE " + TODO_ID_FIELD_NAME_TODO_CATEGORY + " = '" + _todoID + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Loescht einen TodoKategorie Eintrag mit einer bestimmten ID.
     *
     * @param todoID Todo_ ID.
     */
    public void deleteTodoKategorie(int todoID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TODO_CATEGORY + " WHERE " + TODO_ID_FIELD_NAME_TODO_CATEGORY + " = '" + todoID + "'";
        Log.d(TAG, "deleteTodoKategorie: Kategorie mit " + todoID + " Geloescht");
        db.execSQL(query);
    }

    /**
     * Legt eine Kategorie in der Datenbank ab.
     *
     * @param _kategorieName Kategorie Name.
     * @return Neue Reihe mit der Kategorie oder -1 bei Fehler.
     */
    public boolean addKategorie(String _kategorieName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_FIELD_NAME_CATEGORY, _kategorieName);
        long result = db.insert(TABLE_NAME_CATEGORY, null, contentValues);
        return result != -1;
    }

    /**
     * Aktualisiert eine bestehende Kategorie.
     *
     * @param _neueKategorie Neuer Kategorie Name.
     * @param _kategorieID   Kategorie ID.
     * @param _alteKategorie Alter Kategorie Name.
     */
    public void updateKategorie(String _neueKategorie, int _kategorieID, String _alteKategorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_CATEGORY + " SET " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + _neueKategorie + "' WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + _kategorieID + "' AND " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + _alteKategorie + "'";
        Log.d(TAG, "updateKategorie: von " + _alteKategorie + " zu " + _neueKategorie);
        db.execSQL(query);
    }

    /**
     * Loescht eine Kategorie mit einer bestimmten ID.
     *
     * @param _kategorieID   Kategorie ID.
     * @param _kategorieName Kategorie Name.
     * @throws SQLiteException
     */
    public void deleteKategorie(int _kategorieID, String _kategorieName) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_CATEGORY + " WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + _kategorieID + "' AND " + CATEGORY_FIELD_NAME_CATEGORY + " = '" + _kategorieName + "'";
        Log.d(TAG, "deleteKategorie: " + _kategorieName + " Geloescht");
        db.execSQL(query);
    }

    /**
     * Waehlt alle Kategorien aus.
     *
     * @return alle Kategorie Eintraege.
     */
    public Cursor getKategorie() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_CATEGORY;
        return db.rawQuery(query, null);
    }

    /**
     * Waehlt eine Kategorie mit einer bestimmten ID.
     *
     * @param _kategorieID Kategorie ID.
     * @return Kategorie Datenbank Eintrag.
     */
    public Cursor getKategorie(int _kategorieID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_CATEGORY + " WHERE " + ID_FIELD_NAME_CATEGORY + " = '" + _kategorieID + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Waehlt eine Kategorie mit einem bestimmten Namen.
     *
     * @param _kategorieName Kategorie Name.
     * @return Kategorie Datenbank Eintrag.
     */
    public Cursor getKategorieID(String _kategorieName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_FIELD_NAME_PRIORITY + " FROM " + TABLE_NAME_CATEGORY + " WHERE " + FIELD_NAME_PRIORITY + " = '" + _kategorieName + "'";
        return db.rawQuery(query, null);
    }
}
