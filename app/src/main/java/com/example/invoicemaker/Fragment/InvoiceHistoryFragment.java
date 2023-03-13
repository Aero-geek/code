package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invoicemaker.Activity.AddPhotoActivity;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.MyConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceHistoryFragment extends Fragment implements OnClickListener {
    private TextView creation_date;
    private String date;
    private Activity mActivity;
    private SettingsDTO settingsDTO;
    private View view;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.invoice_history_layout, viewGroup, false);
            getIntentData();
            initLayout();
        }
        return this.view;
    }

    private void getIntentData() {
        this.date = getArguments().getString(MyConstants.CATALOG_DTO);
        this.settingsDTO = SettingsDTO.getSettingsDTO();
    }

    private void initLayout() {
        this.creation_date = (TextView) this.view.findViewById(R.id.creation_date);
        if (this.date != null) {
            long parseLong = Long.parseLong(this.date);
            Date date = new Date(parseLong);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
            TextView textView = this.creation_date;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MyConstants.formatDate(this.mActivity, parseLong, this.settingsDTO.getDateFormat()));
            stringBuilder.append(" ");
            stringBuilder.append(simpleDateFormat.format(date));
            textView.setText(stringBuilder.toString());
            return;
        }
        this.view.findViewById(R.id.history_layout).setVisibility(View.GONE);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_photo_layout) {
            startActivity(new Intent(this.mActivity, AddPhotoActivity.class));
        }
    }
}
