package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.AddClientActivity;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ClientAdapter extends Adapter<ClientAdapter.ItemHolder> {
    private static final String TAG = "ClientAdapter";
    private long catalogId;
    private ArrayList<ClientDTO> clientDTOS;
    private String currency_sign;
    private boolean fromCatalog = false;
    private boolean isInvoice = false;
    private Activity mActivity;


    public ClientAdapter(Activity activity, ArrayList<ClientDTO> arrayList, long j, boolean z) {
        this.mActivity = activity;
        this.clientDTOS = arrayList;
        this.catalogId = j;
        this.currency_sign = MyConstants.formatCurrency(activity, SettingsDTO.getSettingsDTO().getCurrencyFormat());
        this.fromCatalog = z;
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.client_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if (this.clientDTOS != null) {
            final ClientDTO clientDTO = (ClientDTO) this.clientDTOS.get(i);
            itemHolder.client_name.setText(clientDTO.getClientName());
            if (this.fromCatalog) {
                itemHolder.total_amount.setVisibility(4);
                itemHolder.total_amount_estimate.setVisibility(4);
            } else {
                TextView access$100;
                StringBuilder stringBuilder;
                if (clientDTO.getTotalInvoice() > 1) {
                    access$100 = itemHolder.total_amount;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.currency_sign);
                    stringBuilder.append(clientDTO.getTotalAmount());
                    stringBuilder.append(", ");
                    stringBuilder.append(clientDTO.getTotalInvoice());
                    stringBuilder.append(" Invoices");
                    access$100.setText(stringBuilder.toString());
                } else {
                    access$100 = itemHolder.total_amount;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.currency_sign);
                    stringBuilder.append(clientDTO.getTotalAmount());
                    stringBuilder.append(", ");
                    stringBuilder.append(clientDTO.getTotalInvoice());
                    stringBuilder.append(" Invoice");
                    access$100.setText(stringBuilder.toString());
                }
                StringBuilder stringBuilder2;
                if (clientDTO.getTotalEstimate() > 1) {
                    access$100 = itemHolder.total_amount_estimate;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(this.currency_sign);
                    stringBuilder2.append(clientDTO.getTotalAmountEstimate());
                    stringBuilder2.append(", ");
                    stringBuilder2.append(clientDTO.getTotalEstimate());
                    stringBuilder2.append(" Estimates");
                    access$100.setText(stringBuilder2.toString());
                } else {
                    access$100 = itemHolder.total_amount_estimate;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(this.currency_sign);
                    stringBuilder2.append(clientDTO.getTotalAmountEstimate());
                    stringBuilder2.append(", ");
                    stringBuilder2.append(clientDTO.getTotalEstimate());
                    stringBuilder2.append(" Estimate");
                    access$100.setText(stringBuilder2.toString());
                }
            }
            itemHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ClientAdapter.this.catalogId > 0) {
                        clientDTO.setCatalogId(ClientAdapter.this.catalogId);
                        ClientAdapter.this.isInvoice = true;
                    } else {
                        ClientAdapter.this.isInvoice = false;
                    }
                    if (ClientAdapter.this.fromCatalog) {
                        if (ClientAdapter.this.catalogId == 0) {
                            clientDTO.setCatalogId(MyConstants.createNewInvoice());
                        }
                        LoadDatabase.getInstance().addInvoiceClient(clientDTO);
                        ClientAdapter.this.mActivity.finish();
                        return;
                    }
                    AddClientActivity.start(view.getContext(), clientDTO, clientDTO.getCatalogId(), ClientAdapter.this.isInvoice);
                }
            });
        }
    }

    public int getItemCount() {
        return this.clientDTOS == null ? 0 : this.clientDTOS.size();
    }

    public void filter(String str, ArrayList<ClientDTO> arrayList) {
        ArrayList arrayList2 = (ArrayList) arrayList.clone();
        this.clientDTOS.clear();
        if (str.equals("")) {
            this.clientDTOS.addAll(arrayList2);
            notifyDataSetChanged();
            return;
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            ClientDTO clientDTO = (ClientDTO) it.next();
            if (!(clientDTO == null || clientDTO.getClientName() == null)) {
                if (!clientDTO.getClientName().equals("")) {
                    if (clientDTO.getClientName().toLowerCase(Locale.getDefault()).contains(str.toString().toLowerCase())) {
                        this.clientDTOS.add(clientDTO);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(int i) {
        this.clientDTOS.remove(i);
        notifyItemRemoved(i);
    }

    public void restoreItem(ClientDTO clientDTO, int i) {
        this.clientDTOS.add(i, clientDTO);
        notifyItemInserted(i);
    }

    public static class ItemHolder extends ViewHolder {
        ImageView addClientImg;
        private TextView client_name;
        private TextView total_amount;
        private TextView total_amount_estimate;

        public ItemHolder(View view) {
            super(view);
            this.client_name = (TextView) view.findViewById(R.id.client_name);
            addClientImg = view.findViewById(R.id.addClientImg);
            this.total_amount = (TextView) view.findViewById(R.id.total_amount);
            this.total_amount_estimate = (TextView) view.findViewById(R.id.total_amount_estimate);
        }
    }
}
