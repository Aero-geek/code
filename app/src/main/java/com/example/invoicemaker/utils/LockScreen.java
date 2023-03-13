package com.example.invoicemaker.utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.invoicemaker.R;


public class LockScreen extends Activity implements OnClickListener, OnKeyListener {
    private String PIN = "";
    private Button cancel_btn;
    private EditText focus_keyboard;
    private Button forget_password;
    private int pass;
    private Button save_btn;
    private int type;
    private MyTextWtcher watcher;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lock_screen);
        init();
    }

    private void init() {
        this.cancel_btn = (Button) findViewById(R.id.Button01);
        this.save_btn = (Button) findViewById(R.id.Button02);
        this.focus_keyboard = (EditText) findViewById(R.id.EditText05);
        this.forget_password = (Button) findViewById(R.id.Button03);
        this.forget_password.setVisibility(4);
        this.save_btn.setVisibility(4);
        this.type = getIntent().getIntExtra("ScreenType", 0);
        if (this.type == 2) {
            this.cancel_btn.setVisibility(4);
            this.pass = getIntent().getIntExtra("AppPasscode", -1);
        }
        this.watcher = new MyTextWtcher(this.focus_keyboard);
        this.focus_keyboard.addTextChangedListener(this.watcher);
        this.focus_keyboard.setOnKeyListener(this);
        this.cancel_btn.setOnClickListener(this);
        this.save_btn.setOnClickListener(this);
    }

    public void PINdisplay() {
        EditText editText = (EditText) findViewById(R.id.EditText01);
        EditText editText2 = (EditText) findViewById(R.id.EditText02);
        EditText editText3 = (EditText) findViewById(R.id.EditText03);
        EditText editText4 = (EditText) findViewById(R.id.EditText04);
        if (this.PIN.length() > 0) {
            editText.setText("*");
        }
        if (this.PIN.length() > 1) {
            editText2.setText("*");
        }
        if (this.PIN.length() > 2) {
            editText3.setText("*");
        }
        if (this.PIN.length() > 3) {
            editText4.setText("*");
        }
        if (this.PIN.length() < 4) {
            editText4.setText("");
        }
        if (this.PIN.length() < 3) {
            editText3.setText("");
        }
        if (this.PIN.length() < 2) {
            editText2.setText("");
        }
        if (this.PIN.length() < 1) {
            editText.setText("");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Button01:
                finish();
                return;
            case R.id.Button02:
                SessionManager.getInstance(getApplicationContext()).setPasscode(Integer.parseInt(this.PIN));
                setResult(-1);
                DataProcessor.getInstance().notifyListeners(null, 666);
                finish();
                return;
            default:
                return;
        }
    }

    public void onResume() {
        super.onResume();
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0) {
            return true;
        }
        if (i == 67) {
            if (this.PIN.length() >= 1 && this.PIN.length() <= 4) {
                this.PIN = this.PIN.substring(0, this.PIN.length() - 1);
                PINdisplay();
                if (this.type == 1) {
                    if (this.PIN.length() == 4) {
                        this.save_btn.setVisibility(0);
                    } else {
                        this.save_btn.setVisibility(4);
                    }
                }
                if (this.type == 2 && this.PIN.length() == 4 && this.pass >= 0 && Integer.parseInt(this.PIN) == this.pass) {
                    finish();
                }
            }
        } else if (i == 66) {
            view = getCurrentFocus();
            if (view != null) {
                ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return false;
    }

    private class MyTextWtcher implements TextWatcher {
        private EditText et;

        private MyTextWtcher(EditText editText) {
            this.et = editText;
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (R.id.EditText05 == this.et.getId()) {
                if (LockScreen.this.PIN.length() == 4) {
                    LockScreen.this.PIN = "";
                    LockScreen.this.PINdisplay();
                } else {
                    LockScreen lockScreen = LockScreen.this;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(LockScreen.this.PIN);
                    stringBuilder.append(LockScreen.this.focus_keyboard.getText().toString());
                    lockScreen.PIN = stringBuilder.toString();
                    LockScreen.this.PINdisplay();
                }
                if (LockScreen.this.type == 1) {
                    if (LockScreen.this.PIN.length() == 4) {
                        LockScreen.this.save_btn.setVisibility(0);
                    } else {
                        LockScreen.this.save_btn.setVisibility(4);
                    }
                }
                if (LockScreen.this.type == 2 && LockScreen.this.PIN.length() == 4 && LockScreen.this.pass >= 0 && Integer.parseInt(LockScreen.this.PIN) == LockScreen.this.pass) {
                    LockScreen.this.finish();
                }
                LockScreen.this.focus_keyboard.removeTextChangedListener(LockScreen.this.watcher);
                LockScreen.this.focus_keyboard.setText("");
                LockScreen.this.focus_keyboard.addTextChangedListener(LockScreen.this.watcher);
            }
        }
    }
}
