package com.example.invoicemaker.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;


public class SegmentedRadioGroup extends RadioGroup {
    public SegmentedRadioGroup(Context context) {
        super(context);
    }

    public SegmentedRadioGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        changeButtonsImages();
    }

    private void changeButtonsImages() {
        int childCount = super.getChildCount();
        if (childCount > 1) {
            super.getChildAt(0);
            int i = 1;
            while (i < childCount - 1) {
                super.getChildAt(i);
                i++;
            }
            super.getChildAt(i);
            return;
        }
        super.getChildAt(0);
    }
}
