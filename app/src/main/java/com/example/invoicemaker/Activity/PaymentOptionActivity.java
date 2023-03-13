package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class PaymentOptionActivity extends AppCompatActivity {
    private EditText bank_details;
    private BusinessDTO businessDTO;
    private EditText business_name;
    private EditText other_details;
    private EditText paypal_address;
    private Toolbar toolbar;

    public static void start(Context context, BusinessDTO businessDTO, long j) {
        Intent intent = new Intent(context, PaymentOptionActivity.class);
        intent.putExtra(MyConstants.BUSINESS_DTO, businessDTO);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_payment_option);

        if (AppCore.isNetworkAvailable(PaymentOptionActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        if (this.businessDTO != null) {
            updatePaymentOption();
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, PaymentOptionActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.businessDTO = (BusinessDTO) getIntent().getSerializableExtra(MyConstants.BUSINESS_DTO);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Payment options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.paypal_address = (EditText) findViewById(R.id.paypal_address);
        this.business_name = (EditText) findViewById(R.id.business_name);
        this.bank_details = (EditText) findViewById(R.id.bank_details);
        this.other_details = (EditText) findViewById(R.id.other_details);
    }

    private void updatePaymentOption() {
        this.paypal_address.setText(this.businessDTO.getPaypalAddress());
        this.business_name.setText(this.businessDTO.getCheckInformation());
        this.bank_details.setText(this.businessDTO.getBankInformation());
        this.other_details.setText(this.businessDTO.getOtherPaymentInformation());
    }

    private boolean updateOnBackPressed() {
        CharSequence trim = this.paypal_address.getText().toString().trim();
        if (trim.equals("") || Patterns.EMAIL_ADDRESS.matcher(trim).matches()) {
            saveItem();
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.paypal_error_message));
        builder.setPositiveButton((CharSequence) "OK", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
        return false;
    }

    public void onBackPressed() {
        if (updateOnBackPressed()) {
            super.onBackPressed();
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveItem() {
        if (this.businessDTO == null) {
            this.businessDTO = new BusinessDTO();
        }
        this.businessDTO.setPaypalAddress(this.paypal_address.getText().toString());
        this.businessDTO.setCheckInformation(this.business_name.getText().toString());
        this.businessDTO.setBankInformation(this.bank_details.getText().toString());
        this.businessDTO.setOtherPaymentInformation(this.other_details.getText().toString());
        if (this.businessDTO.getId() > 0) {
            LoadDatabase.getInstance().updateBusinessInformation(this.businessDTO);
        } else {
            LoadDatabase.getInstance().saveBusinessInformation(this.businessDTO);
        }
    }
}
