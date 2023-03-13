package com.example.invoicemaker.utils;

import android.graphics.Canvas;

import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.invoicemaker.Adapter.ItemAdapter.ItemHolder;

public class RecyclerItemTouchHelper extends SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int i, int i2, RecyclerItemTouchHelperListener recyclerItemTouchHelperListener) {
        super(i, i2);
        this.listener = recyclerItemTouchHelperListener;
    }

    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
        return true;
    }

    public void onSelectedChanged(ViewHolder viewHolder, int i) {
        if (viewHolder != null) {
            Callback.getDefaultUIUtil().onSelected(((ItemHolder) viewHolder).view_foreground);
        }
    }

    public void onChildDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
        Callback.getDefaultUIUtil().onDrawOver(canvas, recyclerView, ((ItemHolder) viewHolder).view_foreground, f, f2, i, z);
    }

    public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
        Callback.getDefaultUIUtil().clearView(((ItemHolder) viewHolder).view_foreground);
    }

    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
        Callback.getDefaultUIUtil().onDraw(canvas, recyclerView, ((ItemHolder) viewHolder).view_foreground, f, f2, i, z);
    }

    public void onSwiped(ViewHolder viewHolder, int i) {
        this.listener.onSwiped(viewHolder, i, viewHolder.getAdapterPosition());
    }

    public int convertToAbsoluteDirection(int i, int i2) {
        return super.convertToAbsoluteDirection(i, i2);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(ViewHolder viewHolder, int i, int i2);
    }
}
