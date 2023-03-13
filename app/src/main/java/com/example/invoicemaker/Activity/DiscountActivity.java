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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.DiscountDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class DiscountActivity extends AppCompatActivity {
    public String TAG = "DiscountActivity";
    private long catalogId;
    private double discountAmount;
    private int discountType;
    private EditText discount_amount;
    private LinearLayout discount_layout;
    private TextView discount_text;
    private Spinner spinner;
    private double subtotalAmount;
    private Toolbar toolbar;

    public static void start(Context context, long j, int i, double d, double d2) {
        Intent intent = new Intent(context, DiscountActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.DISCOUNT_TYPE, i);
        intent.putExtra(MyConstants.DISCOUNT_AMOUNT_TOTAL, d);
        intent.putExtra(MyConstants.DISCOUNT_AMOUNT_SUBTOTAL, d2);
        context.startActivity(intent);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.discountType = getIntent().getIntExtra(MyConstants.DISCOUNT_TYPE, 0);
        this.discountAmount = getIntent().getDoubleExtra(MyConstants.DISCOUNT_AMOUNT_TOTAL, 0.0d);
        this.subtotalAmount = getIntent().getDoubleExtra(MyConstants.DISCOUNT_AMOUNT_SUBTOTAL, 0.0d);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_discount);
        if (AppCore.isNetworkAvailable(DiscountActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, DiscountActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Discount");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.discount_layout = (LinearLayout) findViewById(R.id.discount_layout);
        this.discount_amount = (EditText) findViewById(R.id.discount_amount);
        EditText editText;
        StringBuilder stringBuilder;
        if (this.discountType == 2) {
            editText = this.discount_amount;
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf((this.discountAmount * 100.0d) / this.subtotalAmount)));
            editText.setText(stringBuilder.toString());
        } else {
            editText = this.discount_amount;
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.discountAmount)));
            editText.setText(stringBuilder.toString());
        }
        this.discount_text = (TextView) findViewById(R.id.discount_text);
        switch (this.discountType) {
            case 2:
                this.discount_text.setText("Discount (%)");
                this.discount_layout.setVisibility(0);
                break;
            case 3:
                this.discount_text.setText("Amount");
                this.discount_layout.setVisibility(0);
                break;
            default:
                this.discount_layout.setVisibility(8);
                break;
        }
        this.spinner = (Spinner) findViewById(R.id.spinner);
        this.spinner.setSelection(this.discountType);
        this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                DiscountActivity.this.discountType = i;
                switch (DiscountActivity.this.discountType) {
                    case 2:
                        DiscountActivity.this.discount_text.setText("Discount (%)");
                        DiscountActivity.this.discount_layout.setVisibility(View.VISIBLE);
                        return;
                    case 3:
                        DiscountActivity.this.discount_text.setText("Amount");
                        DiscountActivity.this.discount_layout.setVisibility(0);
                        return;
                    default:
                        DiscountActivity.this.discount_layout.setVisibility(8);
                        return;
                }
            }
        });
    }

    private void updateOnBackPressed() {
        if (this.discountType == 3 || this.discountType == 2) {
            try {
                this.discountAmount = Double.valueOf(this.discount_amount.getText().toString()).doubleValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.discountAmount = 0.0d;
        }
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setDiscountType(this.discountType);
        discountDTO.setDiscountAmount(this.discountAmount);
        discountDTO.setCatalogId(this.catalogId);
        LoadDatabase.getInstance().updateInvoiceDiscount(discountDTO);
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
