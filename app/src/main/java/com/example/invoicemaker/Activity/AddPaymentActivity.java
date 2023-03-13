package com.example.invoicemaker.Activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.PaymentDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.util.Calendar;

public class AddPaymentActivity extends AppCompatActivity implements OnClickListener {
    public static long paymentTimestamp;
    private static EditText payment_date;
    private static EditText payment_method;
    public String TAG = PaymentActivity.class.getName();
    private TextView cancel_payment;
    private long catalogId;
    private TextView delete_payment;
    private int operationType;
    private double paidAmount;
    private TextView paid_amount;
    private PaymentDTO paymentDTO;
    private long paymentId = 0;
    private EditText payment_notes;
    private TextView save_payment;
    private Toolbar toolbar;

    public static void start(Context context, long j, double d, int i) {
        Intent intent = new Intent(context, AddPaymentActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.PAID_AMOUNT, d);
        intent.putExtra(MyConstants.OPERATION_TYPE, i);
        context.startActivity(intent);
    }

    public static void start(Context context, long j, int i) {
        Intent intent = new Intent(context, AddPaymentActivity.class);
        intent.putExtra(MyConstants.PAYMENT_DTO, j);
        intent.putExtra(MyConstants.OPERATION_TYPE, i);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_add_payment);
        if (AppCore.isNetworkAvailable(AddPaymentActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        if (this.operationType == 1) {
            updateData();
        }
        updateLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, AddPaymentActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.paymentId = getIntent().getLongExtra(MyConstants.PAYMENT_DTO, 0);
        this.paidAmount = getIntent().getDoubleExtra(MyConstants.PAID_AMOUNT, 0.0d);
        this.operationType = getIntent().getIntExtra(MyConstants.OPERATION_TYPE, 0);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.paymentDTO = new PaymentDTO();
        paymentTimestamp = Calendar.getInstance().getTimeInMillis();
        this.save_payment = (TextView) findViewById(R.id.save_payment);
        this.cancel_payment = (TextView) findViewById(R.id.cancel_payment);
        this.delete_payment = (TextView) findViewById(R.id.delete_payment);
        this.paid_amount = (TextView) findViewById(R.id.paid_amount);
        payment_date = (EditText) findViewById(R.id.payment_date);
        payment_method = (EditText) findViewById(R.id.payment_method);
        this.payment_notes = (EditText) findViewById(R.id.payment_notes);
        payment_date.setInputType(0);
        payment_date.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AddPaymentActivity.this.showDatePickerDialog();
            }
        });
        payment_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    AddPaymentActivity.this.showDatePickerDialog();
                }
            }
        });
        this.cancel_payment.setOnClickListener(this);
        this.save_payment.setOnClickListener(this);
        this.delete_payment.setOnClickListener(this);
    }

    public void showDatePickerDialog() {
        new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
    }

    private void updateData() {
        this.paymentDTO = LoadDatabase.getInstance().getSinglePayment(this.paymentId);
        this.catalogId = this.paymentDTO.getCatalogId();
        paymentTimestamp = Long.parseLong(this.paymentDTO.getPaymentDate());
    }

    private void updateLayout() {
        if (this.operationType == 0) {
            getSupportActionBar().setTitle((CharSequence) "Add Payment");
            this.delete_payment.setVisibility(View.GONE);
            this.cancel_payment.setVisibility(0);
        } else if (this.operationType == 1) {
            getSupportActionBar().setTitle((CharSequence) "Edit Payment");
            this.cancel_payment.setVisibility(8);
            this.delete_payment.setVisibility(0);
        }
        TextView textView = this.paid_amount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.paidAmount)));
        textView.setText(stringBuilder.toString());
        payment_date.setText(MyConstants.formatDate(this, Calendar.getInstance().getTimeInMillis(), SettingsDTO.getSettingsDTO().getDateFormat()));
        payment_method.setText("Other");
        if (this.paymentDTO != null && this.paymentDTO.getId() > 0) {
            textView = this.paid_amount;
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.paymentDTO.getPaidAmount())));
            textView.setText(stringBuilder.toString());
            payment_date.setText(MyConstants.formatDate(this, Long.parseLong(this.paymentDTO.getPaymentDate()), SettingsDTO.getSettingsDTO().getDateFormat()));
            payment_method.setText(this.paymentDTO.getPaymentMethod());
            this.payment_notes.setText(this.paymentDTO.getPaymentNotes());
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel_payment) {
            finish();
        } else if (id == R.id.delete_payment) {
            deletePayment();
            finish();
        } else if (id == R.id.save_payment) {
            savePayment();
            finish();
        }
    }

    private void deletePayment() {
        if (this.paymentDTO != null && this.paymentDTO.getId() > 0) {
            LoadDatabase.getInstance().deletePayment(this.paymentDTO);
        }
    }

    private void savePayment() {
        try {
            this.paidAmount = Double.valueOf(this.paid_amount.getText().toString()).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaidAmount(this.paidAmount);
        paymentDTO.setCatalogId(this.catalogId);
        paymentDTO.setPaymentDate(String.valueOf(paymentTimestamp));
        paymentDTO.setPaymentMethod(payment_method.getText().toString().trim());
        paymentDTO.setPaymentNotes(this.payment_notes.getText().toString().trim());
        if (this.operationType == 0) {
            LoadDatabase.getInstance().addPayment(paymentDTO);
        } else if (this.operationType == 1) {
            paymentDTO.setId(this.paymentDTO.getId());
            LoadDatabase.getInstance().updatePayment(paymentDTO);
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
        public Dialog onCreateDialog(Bundle bundle) {
            Calendar instance = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, instance.get(1), instance.get(2), instance.get(5));
        }

        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar instance = Calendar.getInstance();
            instance.set(i, i2, i3);
            AddPaymentActivity.paymentTimestamp = instance.getTimeInMillis();
            if (AddPaymentActivity.payment_date.hasFocus()) {
                AddPaymentActivity.payment_date.setText(MyConstants.formatDate(getActivity(), AddPaymentActivity.paymentTimestamp, SettingsDTO.getSettingsDTO().getDateFormat()));
            }
        }
    }
}
