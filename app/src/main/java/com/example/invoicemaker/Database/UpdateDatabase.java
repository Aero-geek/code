package com.example.invoicemaker.Database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class UpdateDatabase extends DatabaseOpenClose {
    private static UpdateDatabase mInstance;
    private SQLiteDatabase myDb;

    private UpdateDatabase() {
    }

    public static synchronized UpdateDatabase getInstance() {
        UpdateDatabase updateDatabase;
        synchronized (UpdateDatabase.class) {
            if (mInstance == null) {
                mInstance = new UpdateDatabase();
            }
            updateDatabase = mInstance;
        }
        return updateDatabase;
    }

    public void updateData() {
        openDb();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", "SYED IKHTIAR AHMED");
        contentValues.put("EMAIL", "IKHTIAR0240@GMAIL.COM");
        contentValues.put("MOBILE", "01716056720");
        contentValues.put("PHONE", "N/A");
        contentValues.put("FAX", "N/A");
        contentValues.put(Contract.Clients.ADDRESS_CONTACT, "N/A");
        contentValues.put("ADDRESS_LINE_1", "N/A");
        contentValues.put("ADDRESS_LINE_2", "N/A");
        contentValues.put("ADDRESS_LINE_3", "N/A");
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_NAME, "N/A");
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_1, "N/A");
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_2, "N/A");
        contentValues.put(Contract.Clients.SHIPPING_ADDRESS_LINE_3, "N/A");
        try {
            this.myDb.insertOrThrow(Contract.Clients.TABLE_NAME, null, contentValues);
        } catch (SQLException e) {
            Log.d("Database", e.getMessage());
        }
        closeDB();
    }

    public void openDb() {
        if (this.myDb == null) {
            this.myDb = DatabaseManager.getInstance().openDatabase();
        }
    }

    public void closeDB() {
        if (this.myDb != null) {
            DatabaseManager.getInstance().closeDatabase();
            this.myDb = null;
        }
    }
}
