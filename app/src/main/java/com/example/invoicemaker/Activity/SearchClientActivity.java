package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicemaker.Adapter.ClientAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.util.ArrayList;

public class SearchClientActivity extends AppCompatActivity implements OnClickListener {
    private long catalogId;
    private ClientAdapter clientAdapter;
    private ArrayList<ClientDTO> clientDTOS;
    private RecyclerView clients_rv;
    private boolean fromCatalog;
    private Toolbar toolbar;

    public static void start(Context context, long j, boolean z) {
        Intent intent = new Intent(context, SearchClientActivity.class);
        // intent.setFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.FROM_CATALOG, z);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_search_item);
        if (AppCore.isNetworkAvailable(SearchClientActivity.this)) {
            addBanner();
        }
        this.clientDTOS = new ArrayList();
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, SearchClientActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.fromCatalog = getIntent().getBooleanExtra(MyConstants.FROM_CATALOG, false);
    }

    public void onResume() {
        super.onResume();
        this.clientDTOS.clear();
        this.clientDTOS.addAll(LoadDatabase.getInstance().getClientList());
        this.clientAdapter.notifyDataSetChanged();
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Clients");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.clients_rv = (RecyclerView) findViewById(R.id.my_items_rv);
        this.clients_rv.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.clients_rv.setHasFixedSize(true);
        this.clientAdapter = new ClientAdapter(this, this.clientDTOS, this.catalogId, this.fromCatalog);
        this.clients_rv.setAdapter(this.clientAdapter);
        findViewById(R.id.add_client).setOnClickListener(this);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_client) {
            finish();
            AddClientActivity.start(this, new ClientDTO(), this.catalogId, true);
        }
    }
}
