package com.example.invoicemaker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    private static final String PREFER_NAME = "InvoiceAndEstimate";
    private static SessionManager instance;
    public final String KEY_PASSCODE = "AppPasscode";
    int PRIVATE_MODE = 0;
    Context _context;
    Editor editor;
    SharedPreferences pref;

    private SessionManager(Context context) {
        this._context = context;
        this.pref = this._context.getSharedPreferences(PREFER_NAME, this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public int getPasscode() {
        return this.pref.getInt("AppPasscode", -1);
    }

    public void setPasscode(int i) {
        this.editor.putInt("AppPasscode", i);
        this.editor.commit();
    }
}
