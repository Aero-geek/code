package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.Adapter.PaymentAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.PaymentDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.ModelChangeListener;
import com.example.invoicemaker.utils.MyConstants;
import com.example.invoicemaker.utils.NonScrollListView;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity implements OnClickListener, ModelChangeListener {
    public String TAG = PaymentActivity.class.getName();
    private NonScrollListView all_payments;
    private long catalogId;
    private TextView due_amount;
    private double paidAmount;
    private TextView paid_amount;
    private PaymentAdapter paymentAdapter;
    private ArrayList<PaymentDTO> paymentDTOS;
    private Toolbar toolbar;
    private double totalAmount;
    private TextView total_amount;

    public static void start(Context context, long j, double d, double d2) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.PAID_AMOUNT, d);
        intent.putExtra(MyConstants.TOTAL_AMOUNT, d2);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_payment);
        if (AppCore.isNetworkAvailable(PaymentActivity.this)) {
            addBanner();
        }
        this.paymentDTOS = new ArrayList();
        DataProcessor.getInstance().addChangeListener(this);
        getIntentData();
        initLayout();
        if (this.catalogId > 0) {
            updateLayout();
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, PaymentActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.paidAmount = getIntent().getDoubleExtra(MyConstants.PAID_AMOUNT, 0.0d);
        this.totalAmount = getIntent().getDoubleExtra(MyConstants.TOTAL_AMOUNT, 0.0d);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Payments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.total_amount = (TextView) findViewById(R.id.total_amount);
        this.paid_amount = (TextView) findViewById(R.id.paid_amount);
        this.due_amount = (TextView) findViewById(R.id.due_amount);
        loadPayments();
        this.all_payments = (NonScrollListView) findViewById(R.id.all_payments_list);
        this.paymentAdapter = new PaymentAdapter(this, this.paymentDTOS);
        this.all_payments.setAdapter(this.paymentAdapter);
        findViewById(R.id.add_payment_fab).setOnClickListener(this);
    }

    private void updateLayout() {
        TextView textView = this.total_amount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("$");
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.totalAmount)));
        textView.setText(stringBuilder.toString());
        textView = this.paid_amount;
        stringBuilder = new StringBuilder();
        stringBuilder.append("$");
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.paidAmount)));
        textView.setText(stringBuilder.toString());
        textView = this.due_amount;
        stringBuilder = new StringBuilder();
        stringBuilder.append("$");
        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.totalAmount - this.paidAmount)));
        textView.setText(stringBuilder.toString());
    }

    private void loadPayments() {
        this.paymentDTOS.clear();
        this.paymentDTOS = LoadDatabase.getInstance().getPayments(this.catalogId);
    }

    private void calculateTotalPaid() {
        this.paidAmount = 0.0d;
        for (int i = 0; i < this.paymentDTOS.size(); i++) {
            this.paidAmount += ((PaymentDTO) this.paymentDTOS.get(i)).getPaidAmount();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_payment_fab) {
            AddPaymentActivity.start(view.getContext(), this.catalogId, MyConstants.formatDecimal(Double.valueOf(this.totalAmount - this.paidAmount)), 0);
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onReceiveModelChange(String str, int i) {
        if (i == 104) {
            try {
                loadPayments();
                calculateTotalPaid();
                this.paymentAdapter = new PaymentAdapter(this, this.paymentDTOS);
                this.all_payments.setAdapter(this.paymentAdapter);
                TextView textView = this.paid_amount;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.paidAmount)));
                stringBuilder.append("");
                textView.setText(stringBuilder.toString());
                textView = this.due_amount;
                stringBuilder = new StringBuilder();
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(this.totalAmount - this.paidAmount)));
                stringBuilder.append("");
                textView.setText(stringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
