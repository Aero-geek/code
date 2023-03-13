package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.TaxDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class TaxActivity extends AppCompatActivity {
    public String TAG = "TaxActivity";
    private long catalogId;
    private Spinner spinner;
    private String taxLabel = "";
    private double taxRate;
    private int taxType;
    private EditText tax_label;
    private LinearLayout tax_label_layout;
    private EditText tax_rate;
    private LinearLayout tax_rate_layout;
    private Toolbar toolbar;

    public static void start(Context context, long j, int i, String str, double d) {
        Intent intent = new Intent(context, TaxActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.TAX_TYPE, i);
        intent.putExtra(MyConstants.TAX_LABEL, str);
        intent.putExtra(MyConstants.TAX_RATE, d);
        context.startActivity(intent);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.taxType = getIntent().getIntExtra(MyConstants.TAX_TYPE, 0);
        this.taxRate = getIntent().getDoubleExtra(MyConstants.TAX_RATE, 0.0d);
        this.taxLabel = getIntent().getStringExtra(MyConstants.TAX_LABEL);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_tax);
        if (AppCore.isNetworkAvailable(TaxActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, TaxActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Taxes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.tax_label_layout = (LinearLayout) findViewById(R.id.tax_label_layout);
        this.tax_rate_layout = (LinearLayout) findViewById(R.id.tax_rate_layout);
        this.tax_label = (EditText) findViewById(R.id.tax_label);
        this.tax_rate = (EditText) findViewById(R.id.tax_rate);
        this.tax_label.setText(this.taxLabel);
        EditText editText = this.tax_rate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.taxRate)));
        editText.setText(stringBuilder.toString());
        checkTaxType();
        this.spinner = (Spinner) findViewById(R.id.spinner);
        this.spinner.setSelection(this.taxType);
        this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                TaxActivity.this.taxType = i;
                switch (TaxActivity.this.taxType) {
                    case 0:
                    case 1:
                        TaxActivity.this.tax_rate_layout.setVisibility(View.VISIBLE);
                        return;
                    default:
                        TaxActivity.this.tax_rate_layout.setVisibility(8);
                        return;
                }
            }
        });
    }

    private boolean checkTaxType() {
        switch (this.taxType) {
            case 0:
            case 1:
                this.tax_rate_layout.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }
    }

    private void updateOnBackPressed() {
        if (checkTaxType()) {
            try {
                this.taxRate = Double.valueOf(this.tax_rate.getText().toString()).doubleValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.taxRate = 0.0d;
        }
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        TaxDTO taxDTO = new TaxDTO();
        taxDTO.setCatalogId(this.catalogId);
        taxDTO.setTaxType(this.taxType);
        taxDTO.setTaxLabel(this.tax_label.getText().toString().trim());
        taxDTO.setTaxRate(this.taxRate);
        LoadDatabase.getInstance().updateInvoiceTax(taxDTO);
    }

    public void onBackPressed() {
        super.onBackPressed();
        updateOnBackPressed();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
