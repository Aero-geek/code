package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class BusinessContactActivity extends AppCompatActivity {
    private BusinessDTO businessDTO;
    private EditText email_address;
    private EditText fax_no;
    private EditText mobile_no;
    private EditText phone_no;
    private Toolbar toolbar;
    private EditText website;

    public static void start(Context context, BusinessDTO businessDTO) {
        Intent intent = new Intent(context, BusinessContactActivity.class);
        intent.putExtra(MyConstants.BUSINESS_DTO, businessDTO);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_business_contact);
        if (AppCore.isNetworkAvailable(BusinessContactActivity.this)) {
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
        Banner(bnnr, BusinessContactActivity.this);
    }

    private void getIntentData() {
        this.businessDTO = (BusinessDTO) getIntent().getSerializableExtra(MyConstants.BUSINESS_DTO);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Business Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.phone_no = (EditText) findViewById(R.id.phone_no);
        this.mobile_no = (EditText) findViewById(R.id.mobile_no);
        this.fax_no = (EditText) findViewById(R.id.fax_no);
        this.email_address = (EditText) findViewById(R.id.email_address);
        this.website = (EditText) findViewById(R.id.website);
    }

    private void updatePaymentOption() {
        this.phone_no.setText(this.businessDTO.getPhoneNo());
        this.mobile_no.setText(this.businessDTO.getMobileNo());
        this.fax_no.setText(this.businessDTO.getFax());
        this.email_address.setText(this.businessDTO.getEmail());
        this.website.setText(this.businessDTO.getWebsite());
    }

    public void onBackPressed() {
        super.onBackPressed();
        saveItem();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveItem() {
        if (this.businessDTO == null) {
            this.businessDTO = new BusinessDTO();
        }
        this.businessDTO.setPhoneNo(this.phone_no.getText().toString());
        this.businessDTO.setMobileNo(this.mobile_no.getText().toString());
        this.businessDTO.setFax(this.fax_no.getText().toString());
        this.businessDTO.setEmail(this.email_address.getText().toString());
        this.businessDTO.setWebsite(this.website.getText().toString());
        if (this.businessDTO.getId() > 0) {
            LoadDatabase.getInstance().updateBusinessInformation(this.businessDTO);
        } else {
            LoadDatabase.getInstance().saveBusinessInformation(this.businessDTO);
        }
    }
}
