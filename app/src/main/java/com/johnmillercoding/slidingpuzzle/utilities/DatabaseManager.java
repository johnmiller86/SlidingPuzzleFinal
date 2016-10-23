package com.johnmillercoding.slidingpuzzle.utilities;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

    private Integer openInstances = 0;
    private static DatabaseManager databaseManager;
    private static SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * Initializes a database instance.
     * @param helper the SQLiteOpenHelper.
     */
    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager();
            sqLiteOpenHelper = helper;
        }
    }

    /**
     * Gets the database manager.
     * @return the database manager.
     */
    public static synchronized DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() + " is not initialized...");
        }
        return databaseManager;
    }

    /**
     * Opens a database instance.
     * @return the instance.
     */
    public synchronized SQLiteDatabase openDatabase() {
        openInstances++;
        if(openInstances == 1) {
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        }
        return sqLiteDatabase;
    }

    /**
     * Closes the database instance.
     */
    public synchronized void closeDatabase() {
        openInstances--;
        if(openInstances == 0) {
            sqLiteDatabase.close();
        }
    }
}