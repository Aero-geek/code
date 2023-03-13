package com.example.invoicemaker.Activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.InvoiceShippingDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.util.Calendar;

public class ShippingInfoActivity extends AppCompatActivity implements OnClickListener {
    static Calendar calendar = Calendar.getInstance();
    private static EditText ship_date;
    private String TAG = "InvoiceInfoActivity";
    private long catalogId;
    private EditText fob;
    private EditText ship_via;
    private InvoiceShippingDTO shippingDTO;
    private long shippingId;
    private EditText shipping_amount;
    private Toolbar toolbar;
    private EditText tracking_number;

    public static void start(Context context, InvoiceShippingDTO invoiceShippingDTO, long j) {
        Intent intent = new Intent(context, ShippingInfoActivity.class);
        intent.putExtra(MyConstants.SHIPPING_DTO, invoiceShippingDTO);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        context.startActivity(intent);
    }

    public void onClick(View view) {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_shipping_info);
        if (AppCore.isNetworkAvailable(ShippingInfoActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, ShippingInfoActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.shippingDTO = (InvoiceShippingDTO) getIntent().getSerializableExtra(MyConstants.SHIPPING_DTO);
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Shipping");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.shipping_amount = (EditText) findViewById(R.id.shipping_amount);
        ship_date = (EditText) findViewById(R.id.ship_date);
        this.ship_via = (EditText) findViewById(R.id.ship_via);
        this.tracking_number = (EditText) findViewById(R.id.tracking_number);
        this.fob = (EditText) findViewById(R.id.fob);
        ship_date.setInputType(0);
        ship_date.setInputType(0);
        ship_date.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShippingInfoActivity.this.showDatePickerDialog();
            }
        });
        if (this.shippingDTO.getId() > 0) {
            updateShippingInfo();
        }
    }

    private void updateShippingInfo() {
        EditText editText = this.shipping_amount;
        StringBuilder stringBuilder = new StringBuilder();
        //  stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.shippingDTO.getAmount())));


        String d = String.valueOf(MyConstants.formatDecimal(Double.valueOf(this.shippingDTO.getAmount())));
        if (d.contains(".")) {
            int dotPos = String.valueOf(d).lastIndexOf(".");
            String subStr = String.valueOf(d).substring(dotPos);
            if (subStr.length() <= 2) {
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.shippingDTO.getAmount())) + "0");
            } else {
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.shippingDTO.getAmount())));
            }
        } else {
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.shippingDTO.getAmount())));
        }


        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        ship_date.setText(this.shippingDTO.getShippingDate());
        this.ship_via.setText(this.shippingDTO.getShipVia());
        this.tracking_number.setText(this.shippingDTO.getTracking());
        this.fob.setText(this.shippingDTO.getFob());
    }

    public void showDatePickerDialog() {
        new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
    }

    private void updateOnBackPressed() {
        double doubleValue;
        try {
            doubleValue = Double.valueOf(this.shipping_amount.getText().toString()).doubleValue();
        } catch (Exception unused) {
            doubleValue = 0.0d;
        }
        this.shippingDTO.setAmount(doubleValue);
        this.shippingDTO.setShippingDate(ship_date.getText().toString());
        this.shippingDTO.setShipVia(this.ship_via.getText().toString());
        this.shippingDTO.setTracking(this.tracking_number.getText().toString());
        this.shippingDTO.setFob(this.fob.getText().toString());
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        this.shippingDTO.setCatalogId(this.catalogId);
        if (this.shippingDTO.getId() > 0) {
            LoadDatabase.getInstance().updateInvoiceShipping(this.shippingDTO);
        } else {
            LoadDatabase.getInstance().saveInvoiceShipping(this.shippingDTO);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        updateOnBackPressed();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
        public Dialog onCreateDialog(Bundle bundle) {
            Calendar instance = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, instance.get(1), instance.get(Calendar.MONTH), instance.get(5));
        }

        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            ShippingInfoActivity.ship_date.setText(String.format("%d/%02d/%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2 + 1), Integer.valueOf(i3)}));
        }
    }
}
