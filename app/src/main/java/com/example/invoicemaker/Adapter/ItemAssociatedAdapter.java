package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.AddItemActivity;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class ItemAssociatedAdapter extends Adapter<ItemAssociatedAdapter.ItemHolder> {
    public String TAG = "ItemAssociatedAdapter";
    private String currency_sign;
    private int discountType;
    private ArrayList<ItemAssociatedDTO> dtos;
    private Activity mActivity;
    private int taxType;

    public ItemAssociatedAdapter(Activity activity, ArrayList<ItemAssociatedDTO> arrayList, int i, int i2) {
        this.mActivity = activity;
        this.dtos = arrayList;
        this.discountType = i;
        this.taxType = i2;
        this.currency_sign = MyConstants.formatCurrency(activity, SettingsDTO.getSettingsDTO().getCurrencyFormat());
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if (this.dtos != null) {
            final ItemAssociatedDTO itemAssociatedDTO = (ItemAssociatedDTO) this.dtos.get(i);
            itemHolder.item_name.setText(itemAssociatedDTO.getItemName());
            TextView access$100 = itemHolder.unit_cost_quantity;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(itemAssociatedDTO.getQuantity());
            stringBuilder.append(" x ");
            stringBuilder.append(this.currency_sign);
            //stringBuilder.append(itemAssociatedDTO.getUnitCost());

            String d = String.valueOf(itemAssociatedDTO.getUnitCost());

            if (d.contains(".")) {

                int dotPos = String.valueOf(d).lastIndexOf(".");
                String subStr = String.valueOf(d).substring(dotPos);
                if (subStr.length() <= 2) {
                    stringBuilder.append(itemAssociatedDTO.getUnitCost() + "0");
                } else {
                    stringBuilder.append(itemAssociatedDTO.getUnitCost());
                }
            } else {
                stringBuilder.append(itemAssociatedDTO.getUnitCost());

            }

            access$100.setText(stringBuilder.toString());
            double unitCost = Double.valueOf(itemAssociatedDTO.getUnitCost()) * itemAssociatedDTO.getQuantity();
            //itemAssociatedDTO.getUnitCost() * itemAssociatedDTO.getQuantity();
            if (this.discountType == 1) {
                unitCost -= (itemAssociatedDTO.getDiscount() * unitCost) * 0.01d;
            }
            TextView access$200 = itemHolder.total_cost;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(this.currency_sign);
            // stringBuilder2.append(MyConstants.formatDecimal(unitCost));
            String d1 = String.valueOf(MyConstants.formatDecimal(unitCost));
            if (d1.contains(".")) {
                int dotPos1 = String.valueOf(d1).lastIndexOf(".");
                String subStr1 = String.valueOf(d1).substring(dotPos1);
                if (subStr1.length() <= 2) {
                    stringBuilder2.append(MyConstants.formatDecimal(unitCost) + "0");
                } else {
                    stringBuilder2.append(MyConstants.formatDecimal(unitCost));
                }
            } else {
                stringBuilder2.append(MyConstants.formatDecimal(unitCost));

            }

            access$200.setText(stringBuilder2.toString());
            itemHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AddItemActivity.start(ItemAssociatedAdapter.this.mActivity, itemAssociatedDTO, itemAssociatedDTO.getCatalogId(), ItemAssociatedAdapter.this.discountType, ItemAssociatedAdapter.this.taxType);
                }
            });
        }
    }

    public int getItemCount() {
        return this.dtos == null ? 0 : this.dtos.size();
    }

    public void removeItem(int i) {
        this.dtos.remove(i);
        notifyItemRemoved(i);
    }

    public void restoreItem(ItemAssociatedDTO itemAssociatedDTO, int i) {
        this.dtos.add(i, itemAssociatedDTO);
        notifyItemInserted(i);
    }

    public void updateDiscount(int i) {
        this.discountType = i;
    }

    public void updateTax(int i) {
        this.taxType = i;
    }

    public static class ItemHolder extends ViewHolder {
        private TextView item_name;
        private TextView total_cost;
        private TextView unit_cost_quantity;

        public ItemHolder(View view) {
            super(view);
            this.item_name = (TextView) view.findViewById(R.id.item_name);
            this.unit_cost_quantity = (TextView) view.findViewById(R.id.unit_cost_quantity);
            this.total_cost = (TextView) view.findViewById(R.id.total_cost);
        }
    }
}
