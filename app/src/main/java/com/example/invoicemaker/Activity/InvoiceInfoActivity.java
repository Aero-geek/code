package com.example.invoicemaker.Activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class InvoiceInfoActivity extends AppCompatActivity implements OnClickListener {
    static Calendar calendar = Calendar.getInstance();
    static int[] termsArrayNumeric = new int[]{0, 0, 1, 2, 3, 4, 5, 6, 7, 10, 14, 30, 45, 60, 90, 180, 365, 0};
    private static long creationDateTimestamp = 0;
    private static EditText creation_date = null;
    private static int dateFormat = 0;
    private static long dueDateTimestamp = 0;
    private static EditText due_date = null;
    private static String due_text = null;
    private static int position = 1;
    private static EditText terms;
    String[] termsArray = new String[]{"None", "Due on receipt", "Next day", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "7 Days", "10 Days", "14 Days", "30 Days", "45 Days", "60 Days", "90 Days", "180 Days", "365 Days", "Custom"};
    private String TAG = "InvoiceInfoActivity";
    private CatalogDTO catalogDTO;
    private TextInputLayout due_date_layout;
    private EditText invoice_name;
    private EditText po_number;
    private SettingsDTO settingsDTO;
    private Toolbar toolbar;

    public static void start(Context context, CatalogDTO catalogDTO) {
        Intent intent = new Intent(context, InvoiceInfoActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO);
        context.startActivity(intent);
    }

    public void onClick(View view) {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_invoice_info);
        if (AppCore.isNetworkAvailable(InvoiceInfoActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, InvoiceInfoActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogDTO = (CatalogDTO) getIntent().getSerializableExtra(MyConstants.CATALOG_DTO);
        this.settingsDTO = SettingsDTO.getSettingsDTO();
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Invoice Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.invoice_name = (EditText) findViewById(R.id.invoice_name);
        this.po_number = (EditText) findViewById(R.id.po_number);
        creation_date = (EditText) findViewById(R.id.creation_date);
        creation_date.setInputType(0);
        creation_date.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                InvoiceInfoActivity.this.showDatePickerDialog();
            }
        });
        creation_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    InvoiceInfoActivity.this.showDatePickerDialog();
                }
            }
        });
        due_date = (EditText) findViewById(R.id.due_date);
        this.due_date_layout = (TextInputLayout) findViewById(R.id.due_date_layout);
        due_date.setInputType(0);
        due_date.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                InvoiceInfoActivity.this.showDatePickerDialog();
            }
        });
        due_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    InvoiceInfoActivity.this.showDatePickerDialog();
                }
            }
        });
        terms = (EditText) findViewById(R.id.terms);
        terms.setInputType(0);
        terms.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                InvoiceInfoActivity.this.showTermsDialog();
            }
        });
        terms.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    InvoiceInfoActivity.this.showTermsDialog();
                }
            }
        });
        if (MyConstants.CATALOG_TYPE == 1) {
            getSupportActionBar().setTitle((CharSequence) "Estimate Info");
            terms.setVisibility(View.GONE);
            this.po_number.setVisibility(View.GONE);
            due_date.setVisibility(View.GONE);
        }
        if (this.catalogDTO.getId() > 0) {
            this.invoice_name.setText(this.catalogDTO.getCatalogName());
            creationDateTimestamp = Long.parseLong(this.catalogDTO.getCreationDate());
            creation_date.setText(MyConstants.formatDate(this, creationDateTimestamp, this.settingsDTO.getDateFormat()));
            position = this.catalogDTO.getTerms();
            if (position == 0 || position == 1) {
                this.due_date_layout.setVisibility(View.GONE);
            } else {
                dueDateTimestamp = Long.parseLong(this.catalogDTO.getDueDate());
                due_date.setText(MyConstants.formatDate(this, dueDateTimestamp, this.settingsDTO.getDateFormat()));
            }
            dateFormat = this.settingsDTO.getDateFormat();
            terms.setText(this.termsArray[position]);
            this.po_number.setText(this.catalogDTO.getPoNumber());
            return;
        }
        addInvoiceInfo();
    }

    private void addInvoiceInfo() {
        this.invoice_name.setText(MyConstants.getInvoiceName());
        creationDateTimestamp = Calendar.getInstance().getTimeInMillis();
        creation_date.setText(MyConstants.formatDate(this, creationDateTimestamp, this.settingsDTO.getDateFormat()));
        terms.setText(getResources().getString(R.string.due_on_receipt));
        due_date.setText("");
        this.po_number.setText("");
        position = 1;
    }

    public void showTermsDialog() {
        Builder builder = new Builder(this);
        builder.setSingleChoiceItems(this.termsArray, position, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                InvoiceInfoActivity.position = i;
                InvoiceInfoActivity.terms.setText(InvoiceInfoActivity.this.termsArray[InvoiceInfoActivity.position]);
                InvoiceInfoActivity.this.updateDueDate();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void updateDueDate() {
        if (position == 0 || position == 1) {
            this.due_date_layout.setVisibility(View.GONE);
            due_date.setText("");
            return;
        }
        this.due_date_layout.setVisibility(View.VISIBLE);
        if (position == 17) {
            dueDateTimestamp = creationDateTimestamp;
            due_date.setText(creation_date.getText().toString());
            return;
        }
        try {
            calendar.setTimeInMillis(creationDateTimestamp + (((long) (((termsArrayNumeric[position] * 24) * 60) * 60)) * 1000));
            dueDateTimestamp = calendar.getTimeInMillis();
            due_text = MyConstants.formatDate(getApplicationContext(), dueDateTimestamp, dateFormat);
            due_date.setText(due_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog() {
        new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
    }

    private void updateOnBackPressed() {
        String obj = this.invoice_name.getText().toString().trim();
        this.invoice_name.requestFocus();
        if (obj.trim().equalsIgnoreCase("")) {
            this.invoice_name.setError("Cannot be empty");
        } else if (Character.isDigit(trim(obj).charAt(obj.length() - 1))) {
            this.catalogDTO.setCatalogName(this.invoice_name.getText().toString().trim());
            this.catalogDTO.setCreationDate(String.valueOf(creationDateTimestamp));
            this.catalogDTO.setTerms(position);
            if (dueDateTimestamp != 0) {
                this.catalogDTO.setDueDate(String.valueOf(dueDateTimestamp));
            } else {
                this.catalogDTO.setDueDate("");
            }
            this.catalogDTO.setPoNumber(this.po_number.getText().toString().trim());
            if (this.catalogDTO.getId() == 0) {
                this.catalogDTO.setPaidStatus(1);
                this.catalogDTO.setDiscountType(0);
                this.catalogDTO.setTaxType(3);
                LoadDatabase.getInstance().saveInvoice(this.catalogDTO);
                return;
            }
            LoadDatabase.getInstance().updateInvoice(this.catalogDTO);
            super.onBackPressed();
        } else {
            this.invoice_name.setError("Invoice should be letters followed by digits");
        }
    }

    public String trim(String str) {
        int len = str.length();
        int st = 0;

        char[] val = str.toCharArray();

        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return str.substring(st, len);
    }

    public void onBackPressed() {
        updateOnBackPressed();
    }

    public boolean onSupportNavigateUp() {
        updateOnBackPressed();
        return true;
    }

    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
        private String TAG = "DatePicker";

        public Dialog onCreateDialog(Bundle bundle) {
            Calendar instance = Calendar.getInstance();
            if (InvoiceInfoActivity.creation_date.hasFocus()) {
                instance.setTimeInMillis(InvoiceInfoActivity.creationDateTimestamp);
            } else if (InvoiceInfoActivity.due_date.hasFocus()) {
                instance.setTimeInMillis(InvoiceInfoActivity.dueDateTimestamp);
            }
            return new DatePickerDialog(getActivity(), this, instance.get(Calendar.YEAR), instance.get(2), instance.get(5));
        }

        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar instance = Calendar.getInstance();
            instance.set(i, i2, i3);
            CharSequence formatDate = MyConstants.formatDate(getContext(), instance.getTimeInMillis(), InvoiceInfoActivity.dateFormat);
            if (InvoiceInfoActivity.creation_date.hasFocus()) {
                InvoiceInfoActivity.creation_date.setText(formatDate);
                InvoiceInfoActivity.creationDateTimestamp = instance.getTimeInMillis();
                if (InvoiceInfoActivity.position != 17) {
                    try {
                        InvoiceInfoActivity.calendar.setTimeInMillis(InvoiceInfoActivity.creationDateTimestamp + (((long) (((InvoiceInfoActivity.termsArrayNumeric[InvoiceInfoActivity.position] * 24) * 60) * 60)) * 1000));
                        InvoiceInfoActivity.dueDateTimestamp = InvoiceInfoActivity.calendar.getTimeInMillis();
                        InvoiceInfoActivity.due_text = MyConstants.formatDate(getContext(), InvoiceInfoActivity.dueDateTimestamp, InvoiceInfoActivity.dateFormat);
                        InvoiceInfoActivity.due_date.setText(InvoiceInfoActivity.due_text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (InvoiceInfoActivity.creationDateTimestamp > InvoiceInfoActivity.dueDateTimestamp) {
                    InvoiceInfoActivity.due_date.setText(formatDate);
                    InvoiceInfoActivity.dueDateTimestamp = InvoiceInfoActivity.creationDateTimestamp;
                }
            } else if (InvoiceInfoActivity.due_date.hasFocus()) {
                if (instance.getTimeInMillis() < InvoiceInfoActivity.creationDateTimestamp) {
                    showWarningDialog();
                    return;
                }
                InvoiceInfoActivity.due_date.setText(formatDate);
                InvoiceInfoActivity.dueDateTimestamp = instance.getTimeInMillis();
                InvoiceInfoActivity.terms.setText("Custom");
                InvoiceInfoActivity.position = 17;
            }
        }

        private void showWarningDialog() {
            Builder builder = new Builder(getActivity());
            builder.setMessage((CharSequence) "Due date must be after invoice date");
            builder.setPositiveButton((CharSequence) "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }
}
