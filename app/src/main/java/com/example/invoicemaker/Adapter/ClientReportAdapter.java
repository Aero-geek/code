package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Dto.ClientReportDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class ClientReportAdapter extends BaseAdapter {
    Boolean flag = false;
    private String TAG = "ClientReportAdapter";
    private ArrayList<ClientReportDTO> clientReportDTOS;
    private Activity mActivity;

    public ClientReportAdapter(Activity activity, ArrayList<ClientReportDTO> arrayList) {
        this.mActivity = activity;
        this.clientReportDTOS = arrayList;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getCount() {
        return this.clientReportDTOS.size();
    }

    public Object getItem(int i) {
        return this.clientReportDTOS.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(this.mActivity).inflate(R.layout.report_item_layout, null);

            if (((ClientReportDTO) this.clientReportDTOS.get(i)).getId() == -999) {
                ClientReportHolder clientReportHolder = new ClientReportHolder(view);
            }
            ClientReportHolder clientReportHolder = new ClientReportHolder(view);

            if (i > 0) {
                view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                clientReportHolder.client_name.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                clientReportHolder.clients_number.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                clientReportHolder.invocie_number.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                clientReportHolder.paid_amount.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
            } else {
                view.setBackground(mActivity.getResources().getDrawable(R.drawable.selected_tab));
                clientReportHolder.client_name.setTextColor(mActivity.getResources().getColor(R.color.white));
                clientReportHolder.clients_number.setTextColor(mActivity.getResources().getColor(R.color.white));
                clientReportHolder.invocie_number.setTextColor(mActivity.getResources().getColor(R.color.white));
                clientReportHolder.paid_amount.setTextColor(mActivity.getResources().getColor(R.color.white));
            }

            Log.e("client_name", "" + ((ClientReportDTO) this.clientReportDTOS.get(i)).getName());
            Log.e("invocie_number", "" + String.valueOf(((ClientReportDTO) this.clientReportDTOS.get(i)).getInvoices()));

            clientReportHolder.client_name.setText(((ClientReportDTO) this.clientReportDTOS.get(i)).getName());
            clientReportHolder.invocie_number.setText(String.valueOf(((ClientReportDTO) this.clientReportDTOS.get(i)).getInvoices()));
            TextView access$200 = clientReportHolder.paid_amount;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MyConstants.formatCurrency(this.mActivity, SettingsDTO.getSettingsDTO().getCurrencyFormat()));
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(((ClientReportDTO) this.clientReportDTOS.get(i)).getPaidAmount())));
            access$200.setText(stringBuilder.toString());
            view.setTag(clientReportHolder);
        }
        return view;
    }

    public static class ClientReportHolder extends ViewHolder {
        private TextView client_name;
        private TextView clients_number;
        private TextView invocie_number;
        private TextView paid_amount;

        public ClientReportHolder(View view) {
            super(view);
            this.client_name = (TextView) view.findViewById(R.id.month_name);
            this.clients_number = (TextView) view.findViewById(R.id.clients_number);
            this.invocie_number = (TextView) view.findViewById(R.id.invoice_number);
            this.paid_amount = (TextView) view.findViewById(R.id.paid_amount);
            this.clients_number.setVisibility(4);
        }
    }
}
