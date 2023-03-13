package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicemaker.Adapter.ItemAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class SearchItemActivity extends AppCompatActivity {
    private long catalogId;
    private ItemAdapter itemAdapter;
    private ArrayList<ItemDTO> itemDTOS;
    private RecyclerView my_items_rv;
    private Toolbar toolbar;

    public static void start(Context context, long j) {
        Intent intent = new Intent(context, SearchItemActivity.class);
        // intent.setFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_search_item);
        if (AppCore.isNetworkAvailable(SearchItemActivity.this)) {
            addBanner();
        }
        this.itemDTOS = new ArrayList();
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, SearchItemActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
    }

    public void onResume() {
        super.onResume();
        this.itemDTOS.clear();
        this.itemDTOS.addAll(LoadDatabase.getInstance().getMyItems());
        this.itemAdapter.notifyDataSetChanged();
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Select Item");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.my_items_rv = (RecyclerView) findViewById(R.id.my_items_rv);
        this.my_items_rv.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.my_items_rv.setHasFixedSize(true);
        this.itemAdapter = new ItemAdapter(this, this.itemDTOS, this.catalogId, true);
        this.my_items_rv.setAdapter(this.itemAdapter);
        findViewById(R.id.add_client).setVisibility(View.GONE);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
