package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Dto.MonthlyReportDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class ReportAdapter extends BaseAdapter {
    private static String[] calendarMonths = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String TAG = "ReportAdapter";
    private Activity mActivity;
    private ArrayList<MonthlyReportDTO> monthlyReportDTOS;


    public ReportAdapter(Activity activity, ArrayList<MonthlyReportDTO> arrayList) {
        this.mActivity = activity;
        this.monthlyReportDTOS = arrayList;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getCount() {
        return this.monthlyReportDTOS.size();
    }

    public Object getItem(int i) {
        return this.monthlyReportDTOS.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(this.mActivity).inflate(R.layout.report_item_layout, null);
            MonthlyReportHolder monthlyReportHolder = new MonthlyReportHolder(view);
            if (((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getYearOrMonth() < 12) {
                view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                monthlyReportHolder.month_name.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                monthlyReportHolder.clients_number.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                monthlyReportHolder.invocie_number.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                monthlyReportHolder.paid_amount.setTextColor(mActivity.getResources().getColor(R.color.newAppColor));
                monthlyReportHolder.month_name.setText(calendarMonths[((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getYearOrMonth()]);
                monthlyReportHolder.clients_number.setText(String.valueOf(((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getTotalClients()));
            } else {
                view.setBackground(mActivity.getResources().getDrawable(R.drawable.selected_tab));
                monthlyReportHolder.month_name.setTextColor(mActivity.getResources().getColor(R.color.white));
                monthlyReportHolder.clients_number.setTextColor(mActivity.getResources().getColor(R.color.white));
                monthlyReportHolder.invocie_number.setTextColor(mActivity.getResources().getColor(R.color.white));
                monthlyReportHolder.paid_amount.setTextColor(mActivity.getResources().getColor(R.color.white));
                //view.setBackgroundColor(this.mActivity.getResources().getColor(R.color.transparent_grey));
                monthlyReportHolder.month_name.setText(String.valueOf(((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getYearOrMonth()));
                monthlyReportHolder.clients_number.setText(String.valueOf(((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getTotalClientsPerYear()));
            }
            monthlyReportHolder.invocie_number.setText(String.valueOf(((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getTotalInvoices()));
            TextView access$300 = monthlyReportHolder.paid_amount;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MyConstants.formatCurrency(this.mActivity, SettingsDTO.getSettingsDTO().getCurrencyFormat()));
            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(((MonthlyReportDTO) this.monthlyReportDTOS.get(i)).getTotalPaidAmount())));
            access$300.setText(stringBuilder.toString());
            view.setTag(monthlyReportHolder);
        }
        return view;
    }

    public static class MonthlyReportHolder extends ViewHolder {
        LinearLayout reportsLinLay;
        private TextView clients_number;
        private TextView invocie_number;
        private TextView month_name;
        private TextView paid_amount;

        public MonthlyReportHolder(View view) {
            super(view);
            this.month_name = (TextView) view.findViewById(R.id.month_name);
            this.clients_number = (TextView) view.findViewById(R.id.clients_number);
            this.invocie_number = (TextView) view.findViewById(R.id.invoice_number);
            this.paid_amount = (TextView) view.findViewById(R.id.paid_amount);
            reportsLinLay = view.findViewById(R.id.reportsLinLay);
        }
    }
}
