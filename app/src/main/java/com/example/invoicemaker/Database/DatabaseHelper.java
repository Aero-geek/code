package com.example.invoicemaker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.invoicemaker.Database.Contract.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "invoiceAndEstimate.exdb";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static void copyFile(FileInputStream fileInputStream, FileOutputStream fileOutputStream) throws IOException {


        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = fileInputStream.getChannel();
            destination = fileOutputStream.getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }


    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE BUSINESS_INFORMATION (_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,REGISTRATION_NUMBER TEXT,VAT TEXT,ADDRESS_LINE_1 TEXT,ADDRESS_LINE_2 TEXT,ADDRESS_LINE_3 TEXT,PHONE TEXT,MOBILE TEXT,FAX TEXT,EMAIL TEXT,WEBSITE TEXT,LOGO_URL TEXT,PAYPAL_ADDRESS TEXT,CHEQUES_INFORMATION TEXT,BANK_INFORMATION TEXT,OTHER_PAYMENT_INFORMATION TEXT,TAXES INTEGER,FULLY_PAID INTEGER,INVOICE_NOTES TEXT,ESTIMATE_NOTES TEXT,QTY_RATE INTEGER ) ");
        sQLiteDatabase.execSQL("CREATE TABLE CLIENTS (_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,EMAIL TEXT,MOBILE TEXT,PHONE TEXT,FAX TEXT,ADDRESS_CONTACT TEXT,ADDRESS_LINE_1 TEXT,ADDRESS_LINE_2 TEXT,ADDRESS_LINE_3 TEXT,SHIPPING_ADDRESS_NAME TEXT,SHIPPING_ADDRESS_LINE_1 TEXT,SHIPPING_ADDRESS_LINE_2 TEXT,SHIPPING_ADDRESS_LINE_3 TEXT ) ");
        sQLiteDatabase.execSQL("CREATE TABLE CATALOG (_id INTEGER PRIMARY KEY AUTOINCREMENT,CLIENTS_ID INTEGER DEFAULT 0,TYPE INTEGER,DISCOUNT_TYPE INTEGER,TAX_TYPE INTEGER,TAX_LABEL TEXT,TAX_RATE REAL,NAME TEXT,CREATED_AT TEXT,TERMS INTEGER,DUE_DATE TEXT,PO_NUMBER TEXT,DISCOUNT REAL,PAID REAL,TOTAL_AMOUNT REAL,PAID_STATUS INTEGER,ESTIMATE_STATUS INTEGER,SIGNED_DATE TEXT,SIGNED_URL TEXT,NOTES TEXT ) ");
        sQLiteDatabase.execSQL("CREATE TABLE CATALOG_IMAGES (_id INTEGER PRIMARY KEY AUTOINCREMENT,CATALOG_ID INTEGER,IMAGE_URL TEXT,DESCRIPTION TEXT,ADDITIONAL_DETAILS TEXT, FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ");
        sQLiteDatabase.execSQL("CREATE TABLE ITEMS (_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DESCRIPTION TEXT,UNIT_COST REAL,TAXABLE INTEGER )");
        sQLiteDatabase.execSQL("CREATE TABLE ITEMS_ASSOCIATED (_id INTEGER PRIMARY KEY AUTOINCREMENT,ITEM_NAME TEXT,DESCRIPTION TEXT,UNIT_COST REAL,TAXABLE INTEGER,CATALOG_ID INTEGER,QTY REAL DEFAULT 1,TAX_RATE REAL,DISCOUNT REAL, FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ");
        sQLiteDatabase.execSQL("CREATE TABLE SHIPPING (_id INTEGER PRIMARY KEY AUTOINCREMENT,CATALOG_ID INTEGER,AMOUNT REAL,SHIPPING_DATE TEXT,SHIP_VIA TEXT,TRACKING TEXT,FOB TEXT, FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ");
        sQLiteDatabase.execSQL("CREATE TABLE PAYMENTS (_id INTEGER PRIMARY KEY AUTOINCREMENT,CATALOG_ID INTEGER,AMOUNT TEXT,DATE TEXT,METHOD TEXT,NOTES TEXT, FOREIGN KEY ( CATALOG_ID )  REFERENCES CATALOG ( _id ) ON DELETE CASCADE) ");
        sQLiteDatabase.execSQL("CREATE TABLE SETTINGS (_id INTEGER PRIMARY KEY AUTOINCREMENT,CURRENCY INTEGER DEFAULT 102 ,DATE_FORMAT INTEGER DEFAULT 0  ) ");
        populateSettingsTable(sQLiteDatabase);
    }

    public void populateSettingsTable(SQLiteDatabase sQLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Settings.CURRENCY, Integer.valueOf(102));
        contentValues.put(Settings.DATE_FORMAT, Integer.valueOf(0));
        try {
            sQLiteDatabase.insertOrThrow(Settings.TABLE_NAME, null, contentValues);
        } catch (SQLException unused) {
        }
    }

    public boolean copyDbOperation(String str, String str2) throws IOException {
        close();
        File file = new File(str);
        File file2 = new File(str2);
        if (!file.exists()) {
            return false;
        }
        copyFile(new FileInputStream(file), new FileOutputStream(file2));
        getWritableDatabase().close();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }
}
