package com.example.todo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */

public class ToDoContentProvider extends ContentProvider {
    private static final String LOG = "ToDoContentProvider";
    private static final String SCHEME = "content://";
    private static final String AUTHORITY = "de.host.mobsys.todo";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper databaseHelper;

    //Contract Klasse für todo table
    public static final class Todos implements BaseColumns {
        public static final String PATH = "todos";

        public final Uri CONTENT_URI =
                Uri.parse(SCHEME + AUTHORITY + "/" + PATH);
        //Todo Spalten
        public static final String TITLE = DatabaseHelper.TITLE_FIELD_NAME_TODO;
        public static final String DESCRIPTION = DatabaseHelper.DESCR_FIELD_NAME_TODO;
        public static final String DUEDATE = DatabaseHelper.DATE_FIELD_NAME_TODO;

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.de.host.mobsys.todo";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.de.host.mobsys.todo";
    }


    //Contract Klasse für Prioritaet Tabelle
    public static final class Priorities implements BaseColumns {
        public static final String PATH = "priorities";

        public final Uri CONTENT_URI =
                Uri.parse(SCHEME + AUTHORITY + "/" + PATH);

        public static final String TITLE = DatabaseHelper.FIELD_NAME_PRIORITY;

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.de.host.mobsys.priorities";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.de.host.mobsys.priorities";
    }


    private static final int TODOS = 100;
    private static final int TODO_ID = 101;
    private static final int PRIORITAETEN = 200;
    private static final int PRIORITY_ID = 201;


    static {
        sUriMatcher.addURI(AUTHORITY, Todos.PATH, TODOS);
        sUriMatcher.addURI(AUTHORITY, Todos.PATH + "/#", TODO_ID);
        sUriMatcher.addURI(AUTHORITY, Priorities.PATH, PRIORITAETEN);
        sUriMatcher.addURI(AUTHORITY, Priorities.PATH + "/#", PRIORITY_ID);
    }

    public ToDoContentProvider() {
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * Ruft Methoden zum auswaehlen auf.
     *
     * @param uri           Angeforderte Uri.
     * @param projection    Liste der zurueckzugebenden Spalten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @param sortOrder     Zeilen ordnen die als SQL ORDER BY Klausel formatiert sind.
     * @return Cursor mit ausgewaehltem Element der Datenbank.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Gibt hinterlegten Integer für URI Muster zurück
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode) {
            case TODOS:
                return queryAllTodos(projection, selection,
                        selectionArgs, sortOrder);
            case TODO_ID://bestimmtes TODO
                long todoId = ContentUris.parseId(uri);
                return queryTodo(todoId, projection, selection,
                        selectionArgs, sortOrder);
            case PRIORITAETEN:
                return queryAllPriorities(projection, selection,
                        selectionArgs, sortOrder);
            case PRIORITY_ID:
                long priorityId = ContentUris.parseId(uri);
                return queryPriority(priorityId, projection, selection,
                        selectionArgs, sortOrder);
            default:
                return null;
        }
    }

    /**
     * Getter fuer Type.
     *
     * @param uri Angeforderte Uri.
     * @return Inhalt vom jeweiligen Typ.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //URI Muster auswerten
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode) {
            case TODOS:
                return Todos.CONTENT_TYPE;
            case TODO_ID:
                return Todos.CONTENT_ITEM_TYPE;
            case PRIORITAETEN:
                return Priorities.CONTENT_TYPE;
            case PRIORITY_ID:
                return Priorities.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    /**
     * Ruft Methoden zum Hinzufuegen auf.
     *
     * @param uri    Angeforderte Uri.
     * @param values Werte die hinzugefuegt werden sollen.
     * @return ID der hinzugefuegten Reihe oder -1 bei Fehler.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //URI Muster auswerten
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode) {
            case TODOS: {
                //Einfügen und ID zurückgeben
                long id = insertTodo(values);
                //URi erstellen und zurückgeben
                return ContentUris.appendId(uri.buildUpon(), id).build();
            }
            case PRIORITAETEN: {
                long id = insertPriority(values);
                return ContentUris.appendId(uri.buildUpon(), id).build();
            }
            default: {
                return null;
            }
        }
    }

    /**
     * Ruft die Methoden zum Loeschen auf.
     *
     * @param uri           Angeforderte Uri.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //URI Muster auswerten
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode) {
            case TODOS: {
                //deleteTodo löscht den Datensatz in der DB
                int affected = deleteAllTodos(selection, selectionArgs);
                return affected;
            }
            case TODO_ID: {
                long todoId = ContentUris.parseId(uri);
                int affected = deleteTodo(todoId, selection, selectionArgs);
                return affected;
            }
            case PRIORITAETEN: {
                int affected = deleteAllPriorities(selection, selectionArgs);
                return affected;
            }
            case PRIORITY_ID: {
                long priorityId = ContentUris.parseId(uri);
                int affected = deletePriority(priorityId, selection, selectionArgs);
                return affected;
            }
            default:
                return 0;
        }
    }

    /**
     * Ruft die Methoden zum Aktualisieren auf.
     *
     * @param uri           Angeforderte Uri.
     * @param values        Zuordnung von Spaltennamen zu neuen Spaltenwerten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return Anzahl der Reihen.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //URI Muster auswerten
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode) {
            case TODOS: {
                int affected = updateTodo(values, selection, selectionArgs);
                return affected;
            }
            case PRIORITAETEN: {
                int affected = updatePriority(values, selection, selectionArgs);
                return affected;
            }
            default:
                return 0;
        }
    }

    /**
     * Waehlt alle Todo_ Elemente aus der Datenbank aus.
     *
     * @param projection    Liste der zurueckzugebenden Spalten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @param sortOrder     Zeilen ordnen die als SQL ORDER BY Klausel formatiert sind.
     * @return Cursor mit allen Todo_ Eintraegen aus der Datenbank.
     */
    private Cursor queryAllTodos(String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            //Aufgrund der 1:1 Abbildung direkte Übernahme in DB Abfrage
            db = this.databaseHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_NAME_TODO, projection, selection,
                    selectionArgs, null, null, sortOrder);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return cursor;
    }

    /**
     * Waehlt ein Todo_ Element mit einer bestimmten ID.
     *
     * @param id            Todo_ ID.
     * @param projection    Liste der zurueckzugebenden Spalten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @param sortOrder     Zeilen ordnen die als SQL ORDER BY Klausel formatiert sind.
     * @return Cursor mit ausgewaehltem Todo_
     */
    private Cursor queryTodo(long id, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        ;
        try {
            //Selektierung erzeugen
            String selString = DatabaseHelper.ID_FIELD_NAME_TODO + "=" + id;
            if (!selection.isEmpty()) {
                selString += " AND " + selection;
            }
            //Wie zuvor
            cursor = db.query(DatabaseHelper.TABLE_NAME_TODO, projection, selString,
                    selectionArgs, null, null, sortOrder);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return cursor;
    }

    /**
     * Waehlt alle Prioritaeten aus der Datenbank aus.
     *
     * @param projection    Liste der zurueckzugebenden Spalten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @param sortOrder     Zeilen ordnen die als SQL ORDER BY Klausel formatiert sind.
     * @return Cursor mit allen Prioritaeten in der Datenbank.
     */
    private Cursor queryAllPriorities(String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_NAME_PRIORITY, projection, selection,
                    selectionArgs, null, null, sortOrder);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return cursor;
    }

    /**
     * Waehlt eine Prioritaet aus der Datenbank mit einer bestimmten ID.
     *
     * @param id            Prioritaet ID.
     * @param projection    Liste der zurueckzugebenden Spalten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @param sortOrder     Zeilen ordnen die als SQL ORDER BY Klausel formatiert sind.
     * @return Cursor mit ausgewaehlter Prioritaet.
     */
    private Cursor queryPriority(long id, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try {
            String selString = DatabaseHelper.ID_FIELD_NAME_TODO + "=" + id;
            if (selection != null) {
                if (!selection.isEmpty()) {
                    selString += " AND " + selection;
                }
            }
            cursor = db.query(DatabaseHelper.TABLE_NAME_PRIORITY, projection, selString,
                    selectionArgs, null, null, sortOrder);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return cursor;
    }

    /**
     * Legt ein neues Todo_ in der Datenbank ab.
     *
     * @param values Todo_ das hinzugefuegt werden soll.
     * @return ID der Reihe oder -1 bei Fehler.
     */
    private long insertTodo(ContentValues values) {
        long inserted = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            inserted = db.insert(DatabaseHelper.TABLE_NAME_TODO, null, values);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return inserted;
    }

    /**
     * Legt eine neue Prioritaet in der Datenbank an.
     *
     * @param values Prioritaet die hinzugefuegt werden soll.
     * @return ID der Reihe oder -1 bei Fehler.
     */
    private long insertPriority(ContentValues values) {
        long inserted = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            inserted = db.insert(DatabaseHelper.TABLE_NAME_PRIORITY, null, values);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return inserted;
    }

    /**
     * Loescht alle Todo_ Eintraege aus der Datenbank.
     *
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    private int deleteAllTodos(String selection, String[] selectionArgs) {
        int deleted = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            deleted = db.delete(DatabaseHelper.TABLE_NAME_TODO, selection, selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return deleted;
    }

    /**
     * Loescht ein Todo_ Eintrag mit einer bestimmten ID.
     *
     * @param id            Todo_ ID.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    private int deleteTodo(long id, String selection, String[] selectionArgs) {
        int deleted = 0;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            String selString = DatabaseHelper.ID_FIELD_NAME_TODO + "=" + id;
            if (selection != null) {
                if (!selection.isEmpty()) {
                    selString += " AND " + selection;
                }
            }
            deleted = db.delete(DatabaseHelper.TABLE_NAME_TODO, selString,
                    selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return deleted;
    }

    /**
     * Loescht alle Prioritaet Eintraege in der Datenbank.
     *
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    private int deleteAllPriorities(String selection, String[] selectionArgs) {
        int deleted = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            deleted = db.delete(DatabaseHelper.TABLE_NAME_PRIORITY, selection, selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return deleted;
    }

    /**
     * Loescht Prioritaet Eintrag in der Datenbank.
     *
     * @param id            Prioritaet ID.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    private int deletePriority(long id, String selection, String[] selectionArgs) {
        int deleted = 0;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            String selString = DatabaseHelper.ID_FIELD_NAME_TODO + "=" + id;
            if (selection != null) {
                if (!selection.isEmpty()) {
                    selString += " AND " + selection;
                }
            }
            deleted = db.delete(DatabaseHelper.TABLE_NAME_TODO, selString,
                    selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return deleted;
    }

    /**
     * Aktualisiert ein Todo_ Eintrag in der Datenbank.
     *
     * @param values        Zuordnung von Spaltennamen zu neuen Spaltenwerten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return
     */
    private int updateTodo(ContentValues values, String selection, String[] selectionArgs) {
        int updated = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            updated = db.update(DatabaseHelper.TABLE_NAME_TODO, values, selection, selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return updated;
    }

    /**
     * Aktualisiert Prioritaet Eintrag in Datenbank.
     *
     * @param values        Zuordnung von Spaltennamen zu neuen Spaltenwerten.
     * @param selection     WHERE-Klausel bei null alle Zeilen Aktualisiert.
     * @param selectionArgs in die WHERE-Klausel aufgenommen, die durch die Werte von whereArgs ersetzt wird.
     * @return Anzahl der Zeilen.
     */
    private int updatePriority(ContentValues values, String selection, String[] selectionArgs) {
        int updated = -1;
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            updated = db.update(DatabaseHelper.TABLE_NAME_PRIORITY, values, selection, selectionArgs);
        } catch (Exception ex) {
            Log.e(LOG, "error querying database", ex);
        }
        return updated;
    }
}
