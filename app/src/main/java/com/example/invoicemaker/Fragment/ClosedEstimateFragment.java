package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicemaker.Adapter.InvoiceAdapter;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.ModelChangeListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ClosedEstimateFragment extends Fragment implements ModelChangeListener {
    private String TAG = "ClosedEstimateFragment";
    private ArrayList<CatalogDTO> catalogDTOS;
    private ArrayList<CatalogDTO> estimates;
    private InvoiceAdapter invoiceAdapter;
    private TextView invoice_message;
    private Activity mActivity;
    private RecyclerView main_invoice_rv;
    private View view;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        if (this.view == null) {
            this.view = layoutInflater.inflate(R.layout.main_fragment_layout, viewGroup, false);
            DataProcessor.getInstance().addChangeListener(this);
            this.catalogDTOS = new ArrayList();
            initLayout();
        }
        return this.view;
    }

    public void onResume() {
        super.onResume();
        updateEstimatesFromDatabase();
        loadData();
    }

    private void updateEstimatesFromDatabase() {
        this.estimates = LoadDatabase.getInstance().getAllEstimates();
        int i = 0;
        while (i < this.estimates.size()) {
            if (((CatalogDTO) this.estimates.get(i)).getEstimateStatus() != 2) {
                this.estimates.remove(i);
                i--;
            }
            i++;
        }
    }

    private void loadData() {
        this.catalogDTOS.clear();
        this.catalogDTOS.addAll(this.estimates);
        if (this.catalogDTOS.size() == 0) {
            this.invoice_message.setVisibility(0);
        } else {
            this.invoice_message.setVisibility(8);
        }
        if (this.invoiceAdapter != null) {
            this.invoiceAdapter.notifyDataSetChanged();
        }
    }

    private void initLayout() {
        this.main_invoice_rv = (RecyclerView) this.view.findViewById(R.id.main_invoice_rv);
        this.main_invoice_rv.setLayoutManager(new LinearLayoutManager(this.mActivity, 1, false));
        this.main_invoice_rv.setHasFixedSize(true);
        this.invoiceAdapter = new InvoiceAdapter(this.mActivity, this.catalogDTOS);
        this.main_invoice_rv.setAdapter(this.invoiceAdapter);
        this.invoice_message = (TextView) this.view.findViewById(R.id.invoice_message);
        this.invoice_message.setText(this.mActivity.getResources().getString(R.string.closed_estimate_message));
    }

    public void onDestroy() {
        super.onDestroy();
        DataProcessor.getInstance().removeChangeListener(this);
    }

    public void onReceiveModelChange(String str, int i) {
        Gson gson = new Gson();
        if (i == 2001) {
            loadData();
            this.invoiceAdapter.filter(str, this.catalogDTOS);
        }
    }
}
