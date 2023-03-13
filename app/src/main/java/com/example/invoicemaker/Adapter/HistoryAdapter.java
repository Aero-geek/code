package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.R;

import java.util.ArrayList;

public class HistoryAdapter extends Adapter<HistoryAdapter.ItemHolder> {
    private ArrayList<String> itemDTOS;
    private Activity mActivity;

    public HistoryAdapter(Activity activity, ArrayList<String> arrayList) {
        this.mActivity = activity;
        this.itemDTOS = arrayList;
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_history_layout, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if (this.itemDTOS != null) {
            itemHolder.creation_date.setText((String) this.itemDTOS.get(i));
        }
    }

    public int getItemCount() {
        return this.itemDTOS == null ? 0 : this.itemDTOS.size();
    }

    public static class ItemHolder extends ViewHolder {
        private TextView creation_date;

        public ItemHolder(View view) {
            super(view);
            this.creation_date = (TextView) view.findViewById(R.id.creation_date);
        }
    }
}
