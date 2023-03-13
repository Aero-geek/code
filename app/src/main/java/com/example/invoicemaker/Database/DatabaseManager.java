package com.example.invoicemaker.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private int mOpenCounter;

    public static synchronized void initializeInstance(SQLiteOpenHelper sQLiteOpenHelper) {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = new DatabaseManager();
                mDatabaseHelper = sQLiteOpenHelper;

            }
        }
    }

    public static synchronized DatabaseManager getInstance() {
        DatabaseManager databaseManager;
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(DatabaseManager.class.getSimpleName());
                stringBuilder.append(" is not initialized, call initializeInstance(..) method first.");
                throw new IllegalStateException(stringBuilder.toString());
            }
            databaseManager = instance;
        }
        return databaseManager;
    }

    public synchronized SQLiteDatabase openDatabase() {
        this.mOpenCounter++;
        if (this.mOpenCounter == 1) {
            this.mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return this.mDatabase;
    }

    public synchronized void closeDatabase() {
        this.mOpenCounter--;
        if (this.mOpenCounter == 0) {
            this.mDatabase.close();
        }
    }
}
