package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.AddItemActivity;
import com.example.invoicemaker.Activity.MyItemActivity;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ItemAdapter extends Adapter<ItemAdapter.ItemHolder> {
    private String TAG = "ItemAdapter";
    private long catalogId;
    private String currency_sign;
    private boolean isSearch;
    private ArrayList<ItemDTO> itemDTOS;
    private Activity mActivity;

    public ItemAdapter(Activity activity, ArrayList<ItemDTO> arrayList, long j, boolean z) {
        this.mActivity = activity;
        this.itemDTOS = arrayList;
        this.isSearch = z;
        this.catalogId = j;
        this.currency_sign = MyConstants.formatCurrency(activity, SettingsDTO.getSettingsDTO().getCurrencyFormat());
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if (this.itemDTOS != null) {
            final ItemDTO itemDTO = (ItemDTO) this.itemDTOS.get(i);
            itemHolder.item_name.setText(itemDTO.getItemName());
            TextView access$100 = itemHolder.unit_cost;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.currency_sign);


            stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(itemDTO.getUnitCost())));
            access$100.setText(stringBuilder.toString());
            itemHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ItemAdapter.this.isSearch) {
                        ItemAssociatedDTO itemAssociatedDTO = new ItemAssociatedDTO();
                        itemAssociatedDTO.setItemName(itemDTO.getItemName());

                        itemAssociatedDTO.setUnitCost(String.valueOf(MyConstants.formatDecimal(Double.valueOf(itemDTO.getUnitCost()))));
                        itemAssociatedDTO.setTaxAble(itemDTO.getTexable());
                        itemAssociatedDTO.setDescription(itemDTO.getItemDescription());
                        itemAssociatedDTO.setQuantity(1.0d);
                        AddItemActivity.start(view.getContext(), itemAssociatedDTO, ItemAdapter.this.catalogId, 0, 3);
                        Log.e("itemView", "if");
                        return;
                    }
                    Log.e("itemView", "else");
                    MyItemActivity.start(view.getContext(), itemDTO);
                }
            });
        }
    }

    public int getItemCount() {
        return this.itemDTOS == null ? 0 : this.itemDTOS.size();
    }

    public void filter(CharSequence charSequence, ArrayList<ItemDTO> arrayList) {
        ArrayList arrayList2 = (ArrayList) arrayList.clone();
        this.itemDTOS.clear();
        if (charSequence.equals("")) {
            this.itemDTOS.addAll(arrayList2);
            notifyDataSetChanged();
            return;
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            ItemDTO itemDTO = (ItemDTO) it.next();
            if (itemDTO.getItemName() != null) {
                if (!itemDTO.getItemName().equals("")) {
                    if (itemDTO.getItemName().toLowerCase(Locale.getDefault()).contains(charSequence.toString().toLowerCase())) {
                        this.itemDTOS.add(itemDTO);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(int i) {
        this.itemDTOS.remove(i);
        notifyItemRemoved(i);
    }

    public void restoreItem(ItemDTO itemDTO, int i) {
        this.itemDTOS.add(i, itemDTO);
        notifyItemInserted(i);
    }

    public static class ItemHolder extends ViewHolder {
        public RelativeLayout view_background;
        public RelativeLayout view_foreground;
        private TextView item_name;
        private TextView unit_cost;

        public ItemHolder(View view) {
            super(view);
            this.item_name = (TextView) view.findViewById(R.id.item_name);
            this.unit_cost = (TextView) view.findViewById(R.id.unit_cost);
            this.view_foreground = (RelativeLayout) view.findViewById(R.id.view_foreground);
            this.view_background = (RelativeLayout) view.findViewById(R.id.view_background);
        }
    }
}
