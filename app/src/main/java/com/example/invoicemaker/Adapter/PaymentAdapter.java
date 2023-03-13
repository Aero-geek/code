package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.AddPaymentActivity;
import com.example.invoicemaker.Dto.PaymentDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class PaymentAdapter extends BaseAdapter {
    private String TAG = "PaymentAdapter";
    private Activity mActivity;
    private ArrayList<PaymentDTO> paymentDTOS;

    public PaymentAdapter(Activity activity, ArrayList<PaymentDTO> arrayList) {
        this.mActivity = activity;
        this.paymentDTOS = arrayList;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getCount() {
        return this.paymentDTOS.size();
    }

    public Object getItem(int i) {
        return this.paymentDTOS.get(i);
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view != null) {
            return view;
        }
        view = LayoutInflater.from(this.mActivity).inflate(R.layout.main_payment_layout, null);
        MainPaymentHolder mainPaymentHolder = new MainPaymentHolder(view);
        mainPaymentHolder.payment_date.setText(MyConstants.formatDate(this.mActivity, Long.parseLong(((PaymentDTO) this.paymentDTOS.get(i)).getPaymentDate()), SettingsDTO.getSettingsDTO().getDateFormat()));
        mainPaymentHolder.payment_amount.setText(String.valueOf(MyConstants.formatDecimal(Double.valueOf(((PaymentDTO) this.paymentDTOS.get(i)).getPaidAmount()))));
        view.setTag(mainPaymentHolder);
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AddPaymentActivity.start(view.getContext(), ((PaymentDTO) PaymentAdapter.this.paymentDTOS.get(i)).getId(), 1);
            }
        });
        return view;
    }

    public static class MainPaymentHolder extends ViewHolder {
        private TextView payment_amount;
        private TextView payment_date;

        public MainPaymentHolder(View view) {
            super(view);
            this.payment_date = (TextView) view.findViewById(R.id.payment_date);
            this.payment_amount = (TextView) view.findViewById(R.id.payment_amount);
        }
    }
}
