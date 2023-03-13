package com.example.invoicemaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Activity.AddPhotoActivity;
import com.example.invoicemaker.Dto.ImageDTO;
import com.example.invoicemaker.R;

import java.util.ArrayList;

public class ItemImageAdapter extends Adapter<ItemImageAdapter.ItemHolder> {
    private ArrayList<ImageDTO> imageDTOS;
    private Activity mActivity;

    public ItemImageAdapter(Activity activity, ArrayList<ImageDTO> arrayList) {
        this.mActivity = activity;
        this.imageDTOS = arrayList;
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image_layout, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if (this.imageDTOS != null) {
            final ImageDTO imageDTO = (ImageDTO) this.imageDTOS.get(i);
            itemHolder.image_url.setText(imageDTO.getDescription());
            itemHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AddPhotoActivity.start(view.getContext(), imageDTO, imageDTO.getCatalogId());
                }
            });
        }
    }

    public int getItemCount() {
        return this.imageDTOS == null ? 0 : this.imageDTOS.size();
    }

    public static class ItemHolder extends ViewHolder {
        private TextView image_url;

        public ItemHolder(View view) {
            super(view);
            this.image_url = (TextView) view.findViewById(R.id.image_url);
        }
    }
}
