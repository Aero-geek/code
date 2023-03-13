package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.InvoiceDetailsActivity;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class InvoiceAdapter extends Adapter<InvoiceAdapter.MainInvoiceHolder> {
    private String TAG = "InvoiceAdapter";
    private String currency_sign;
    private ArrayList<CatalogDTO> invoiceDTOS;
    private Activity mActivity;

    public InvoiceAdapter(Activity activity, ArrayList<CatalogDTO> arrayList) {
        this.mActivity = activity;
        this.invoiceDTOS = arrayList;

        this.currency_sign = MyConstants.formatCurrency(activity, SettingsDTO.getSettingsDTO().getCurrencyFormat());
    }

    public MainInvoiceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MainInvoiceHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_invoice_layout, viewGroup, false));
    }

    public void onBindViewHolder(MainInvoiceHolder mainInvoiceHolder, int i) {
        if (this.invoiceDTOS != null) {
            TextView access$100;
            StringBuilder stringBuilder;
            final CatalogDTO catalogDTO = (CatalogDTO) this.invoiceDTOS.get(i);
            mainInvoiceHolder.invoice_name.setText(catalogDTO.getCatalogName());
            if (catalogDTO.getPaidStatus() == 2) {
                access$100 = mainInvoiceHolder.invoice_amount;
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.currency_sign);
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount())));
                access$100.setText(stringBuilder.toString());
            } else {
                access$100 = mainInvoiceHolder.invoice_amount;
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.currency_sign);
                stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(catalogDTO.getTotalAmount() - catalogDTO.getPaidAmount())));
                access$100.setText(stringBuilder.toString());
            }
            if (catalogDTO.getClientDTO().getId() == 0) {
                mainInvoiceHolder.client_name.setText("No client");
            } else {
                mainInvoiceHolder.client_name.setText(catalogDTO.getClientDTO().getClientName());
            }
            StringBuilder stringBuilder2;
            if (catalogDTO.getPaidStatus() == 2) {
                access$100 = mainInvoiceHolder.due_date;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Paid ");
                stringBuilder2.append(MyConstants.formatDate(this.mActivity, Long.parseLong(catalogDTO.getCreationDate()), SettingsDTO.getSettingsDTO().getDateFormat()));
                access$100.setText(stringBuilder2.toString());
            } else if (catalogDTO.getDueDate() != null && catalogDTO.getDueDate().length() > 0) {
                long parseLong = (Long.parseLong(catalogDTO.getDueDate()) - Calendar.getInstance().getTimeInMillis()) / 86400000;
                if (parseLong >= 0) {
                    access$100 = mainInvoiceHolder.due_date;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Due ");
                    stringBuilder.append(MyConstants.formatDate(this.mActivity, Long.parseLong(catalogDTO.getDueDate()), SettingsDTO.getSettingsDTO().getDateFormat()));
                    access$100.setText(stringBuilder.toString());
                    mainInvoiceHolder.due_date.setTextColor(this.mActivity.getResources().getColor(R.color.white));
                } else if (parseLong < 0) {
                    access$100 = mainInvoiceHolder.due_date;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Due ");
                    stringBuilder2.append(MyConstants.formatDate(this.mActivity, Long.parseLong(catalogDTO.getDueDate()), SettingsDTO.getSettingsDTO().getDateFormat()));
                    access$100.setText(stringBuilder2.toString());
                    mainInvoiceHolder.due_date.setTextColor(this.mActivity.getResources().getColor(R.color.delete_color_bg));
                }
            } else if (catalogDTO.getTerms() == 1) {
                mainInvoiceHolder.due_date.setText("Due on receipt");
                mainInvoiceHolder.due_date.setTextColor(this.mActivity.getResources().getColor(R.color.white));
            } else if (catalogDTO.getTerms() == 0) {
                mainInvoiceHolder.due_date.setText("");
            }

            mainInvoiceHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    InvoiceDetailsActivity.start(view.getContext(), catalogDTO);
                }
            });
        }
    }

    public int getItemCount() {
        return this.invoiceDTOS.size();
    }

    public void removeItem(int i) {
        this.invoiceDTOS.remove(i);
        notifyItemRemoved(i);
    }

    public void restoreItem(CatalogDTO catalogDTO, int i) {
        this.invoiceDTOS.add(i, catalogDTO);
        notifyItemInserted(i);
    }

    public boolean filter(CharSequence charSequence, ArrayList<CatalogDTO> arrayList) {
        ArrayList arrayList2 = (ArrayList) arrayList.clone();
        this.invoiceDTOS.clear();
        if (charSequence.equals("")) {
            this.invoiceDTOS.addAll(arrayList2);
            notifyDataSetChanged();
            return false;
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            CatalogDTO catalogDTO = (CatalogDTO) it.next();
            if (!(catalogDTO.getClientDTO() == null || catalogDTO.getClientDTO().getClientName() == null)) {
                if (!catalogDTO.getClientDTO().getClientName().equals("")) {
                    if (catalogDTO.getClientDTO().getClientName().toLowerCase(Locale.getDefault()).contains(charSequence.toString().toLowerCase())) {
                        this.invoiceDTOS.add(catalogDTO);
                    }
                }
            }
        }
        notifyDataSetChanged();
        if (this.invoiceDTOS.size() == 0) {
            return false;
        }
        return true;
    }

    public static class MainInvoiceHolder extends ViewHolder {
        private TextView client_name;
        private TextView due_date;
        private TextView invoice_amount;
        private TextView invoice_name;

        public MainInvoiceHolder(View view) {
            super(view);
            this.client_name = (TextView) view.findViewById(R.id.client_name);
            this.invoice_amount = (TextView) view.findViewById(R.id.invoice_amount);
            this.invoice_name = (TextView) view.findViewById(R.id.invoice_name);
            this.due_date = (TextView) view.findViewById(R.id.due_date);
        }
    }
}
