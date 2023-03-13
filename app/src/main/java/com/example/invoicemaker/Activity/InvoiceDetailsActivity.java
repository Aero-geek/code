package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.invoicemaker.Adapter.PagerAdapter;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.PaymentDTO;
import com.example.invoicemaker.Fragment.EditInvoiceFragment;
import com.example.invoicemaker.Fragment.InvoiceHistoryFragment;
import com.example.invoicemaker.MainActivity;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.ModelChangeListener;
import com.example.invoicemaker.utils.MyConstants;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.itextpdf.xmp.options.PropertyOptions;

import java.util.Calendar;

public class InvoiceDetailsActivity extends AppCompatActivity implements OnClickListener, ModelChangeListener {
    public String TAG = "InvoiceDetailsActivity";
    private TextView activity_name;
    private String callerActivity;
    private CatalogDTO catalogDTO;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;

    public static void start(Context context, CatalogDTO catalogDTO) {
        Intent intent = new Intent(context, InvoiceDetailsActivity.class);
        intent.putExtra(MyConstants.CALLER_ACTIVITY, context.getClass().getSimpleName());
        intent.putExtra(MyConstants.CATALOG_DTO, catalogDTO);
        intent.setFlags(PropertyOptions.DELETE_EXISTING);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_invoice_details);
        DataProcessor.getInstance().addChangeListener(this);
        if (AppCore.isNetworkAvailable(InvoiceDetailsActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        setUpTabLayout(bundle);
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, InvoiceDetailsActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.catalogDTO = (CatalogDTO) getIntent().getSerializableExtra(MyConstants.CATALOG_DTO);
        this.callerActivity = getIntent().getStringExtra(MyConstants.CALLER_ACTIVITY);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invoice_details_menu, menu);
        if (MyConstants.CATALOG_TYPE == 1) {
            menu.findItem(R.id.mark_paid).setVisible(false);
            menu.findItem(R.id.make_invoice).setVisible(true);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_invoice:
                if (checkValidity()) {
                    try {
                        LoadDatabase.getInstance().deleteInvoice(this.catalogDTO == null ? 0 : this.catalogDTO.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                return true;
            case R.id.duplicate:
                if (checkValidity()) {
                    createDuplicateCatalog();
                }
                return true;
            case R.id.make_invoice:
                if (checkValidity()) {
                    UpdateEstimateData();
                }
                return true;
            case R.id.mark_paid:
                if (checkValidity()) {
                    UpdateInvoiceData();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void createDuplicateCatalog() {
        MyConstants.DUPLICATE_ENTRY_FOR = this.catalogDTO.getType();
        if (MyConstants.CATALOG_TYPE == 1) {
            this.catalogDTO.setEstimateStatus(1);
        }
        CatalogDTO catalogDTO = null;
        try {
            catalogDTO = this.catalogDTO.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (MyConstants.CATALOG_TYPE == 1) {
            MyConstants.invoiceCount = LoadDatabase.getInstance().getAllEstimates().size() + 1;
        } else {
            MyConstants.invoiceCount = LoadDatabase.getInstance().getAllInvoices().size() + 1;
        }
        catalogDTO.setCatalogName(MyConstants.getInvoiceName());
        try {
            catalogDTO.setId(LoadDatabase.getInstance().createDuplicate(catalogDTO, getApplicationContext()));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        finish();
        start(this, catalogDTO);
    }

    private boolean checkValidity() {
        if (this.catalogDTO != null) {
            return true;
        }
        Toast.makeText(this, "Invoice/Estimate has not created yet!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void UpdateInvoiceData() {
        double totalAmount = this.catalogDTO.getTotalAmount() - this.catalogDTO.getPaidAmount();
        if (totalAmount != 0.0d && this.catalogDTO.getId() != 0) {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setCatalogId(this.catalogDTO.getId());
            paymentDTO.setPaidAmount(totalAmount);
            paymentDTO.setPaymentDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            paymentDTO.setPaymentMethod("Others");
            LoadDatabase.getInstance().addPayment(paymentDTO);
        }
    }

    private void UpdateEstimateData() {
        this.catalogDTO.setEstimateStatus(2);
        LoadDatabase.getInstance().updateInvoice(this.catalogDTO);
        MyConstants.createDuplicateEntry = true;
        try {
            copyEstimateToInvoice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyConstants.createDuplicateEntry = false;
    }

    private void copyEstimateToInvoice() {
        CatalogDTO clone;
        MyConstants.DUPLICATE_ENTRY_FOR = this.catalogDTO.getType();
        try {
            clone = this.catalogDTO.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            clone = null;
        }
        MyConstants.CATALOG_TYPE = 0;
        MyConstants.invoiceCount = LoadDatabase.getInstance().getAllInvoices().size() + 1;
        clone.setCatalogName(MyConstants.getInvoiceName());
        try {
            clone.setId(LoadDatabase.getInstance().createDuplicate(clone, getApplicationContext()));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        finish();
        start(this, clone);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.activity_name = (TextView) findViewById(R.id.activity_name);
        if (MyConstants.CATALOG_TYPE == 1) {
            this.activity_name.setText("Estimate");
        } else {
            this.activity_name.setText("Invoice");
        }
        findViewById(R.id.send_text).setOnClickListener(this);
        findViewById(R.id.send_email).setOnClickListener(this);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("tabsCount", this.pagerAdapter.getCount());
        bundle.putStringArray("titles", (String[]) this.pagerAdapter.getTitles().toArray(new String[0]));
    }

    private void setUpTabLayout(Bundle bundle) {
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putSerializable(MyConstants.CATALOG_DTO, this.catalogDTO);
            Fragment editInvoiceFragment = new EditInvoiceFragment();
            editInvoiceFragment.setArguments(bundle);
            this.pagerAdapter.addFragment(editInvoiceFragment, getResources().getString(R.string.edit_invoice_text), null);
            bundle = new Bundle();
            bundle.putString(MyConstants.CATALOG_DTO, this.catalogDTO == null ? null : this.catalogDTO.getCreationDate());
            editInvoiceFragment = new InvoiceHistoryFragment();
            editInvoiceFragment.setArguments(bundle);
            this.pagerAdapter.addFragment(editInvoiceFragment, getResources().getString(R.string.invoice_history_text), null);
        } else {
            Integer valueOf = Integer.valueOf(bundle.getInt("tabsCount"));
            String[] stringArray = bundle.getStringArray("titles");
            for (int i = 0; i < valueOf.intValue(); i++) {
                this.pagerAdapter.addFragment(getFragment(i, bundle), stringArray[i], null);
            }
        }
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setOffscreenPageLimit(3);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
            }
        });
        this.tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        this.tabLayout.setTabGravity(0);
        this.tabLayout.setupWithViewPager(this.viewPager);
        int betweenSpace = 10;
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.leftMargin = betweenSpace;
            params.rightMargin = betweenSpace;

        }
    }

    private Fragment getFragment(int i, Bundle bundle) {
        return bundle == null ? this.pagerAdapter.getItem(i) : getSupportFragmentManager().findFragmentByTag(getFragmentTag(i));
    }

    private String getFragmentTag(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android:switcher:2131296670:");
        stringBuilder.append(i);
        return stringBuilder.toString();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.add_photo_layout) {
            startActivity(new Intent(this, AddPhotoActivity.class));
        }
    }

    private void updateOnBackPressed() {
        if (this.callerActivity != null && this.callerActivity.equals(getClass().getSimpleName())) {
            MyConstants.CATALOG_TYPE = MyConstants.DUPLICATE_ENTRY_FOR;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("from_app", true);
            intent.setFlags(268468224);
            startActivity(intent);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        updateOnBackPressed();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onReceiveModelChange(String str, int i) {
        if (!MyConstants.createDuplicateEntry) {
            Gson gson = new Gson();
            switch (i) {
                case 2001:
                case MyConstants.ACTION_INVOICE_UPDATED /*2002*/:
                    CatalogDTO catalogDTO = (CatalogDTO) gson.fromJson(str, CatalogDTO.class);
                    if (this.catalogDTO == null) {
                        this.catalogDTO = new CatalogDTO();
                    }
                    this.catalogDTO.setCatalogName(catalogDTO.getCatalogName());
                    this.catalogDTO.setCreationDate(catalogDTO.getCreationDate());
                    this.catalogDTO.setClientDTO(catalogDTO.getClientDTO());
                    this.catalogDTO.setDueDate(catalogDTO.getDueDate());
                    this.catalogDTO.setDiscount(catalogDTO.getDiscount());
                    this.catalogDTO.setDiscountType(catalogDTO.getDiscountType());
                    this.catalogDTO.setDiscountAmount(catalogDTO.getDiscountAmount());
                    this.catalogDTO.setEstimateStatus(catalogDTO.getEstimateStatus());
                    this.catalogDTO.setId(catalogDTO.getId());
                    this.catalogDTO.setInvoiceShippingDTO(catalogDTO.getInvoiceShippingDTO());
                    this.catalogDTO.setNotes(catalogDTO.getNotes());
                    this.catalogDTO.setPaidAmount(catalogDTO.getPaidAmount());
                    this.catalogDTO.setPaidStatus(catalogDTO.getPaidStatus());
                    this.catalogDTO.setPoNumber(catalogDTO.getPoNumber());
                    this.catalogDTO.setSubTotalAmount(catalogDTO.getSubTotalAmount());
                    this.catalogDTO.setSignedUrl(catalogDTO.getSignedUrl());
                    this.catalogDTO.setSignedDate(catalogDTO.getSignedDate());
                    this.catalogDTO.setTaxType(catalogDTO.getTaxType());
                    this.catalogDTO.setTaxLabel(catalogDTO.getTaxLabel());
                    this.catalogDTO.setTaxRate(catalogDTO.getTaxRate());
                    this.catalogDTO.setTaxAmount(catalogDTO.getTaxAmount());
                    this.catalogDTO.setTotalAmount(catalogDTO.getTotalAmount());
                    this.catalogDTO.setType(catalogDTO.getType());
                    this.catalogDTO.setTerms(catalogDTO.getTerms());
                    break;
                default:
                    this.catalogDTO = LoadDatabase.getInstance().getSingleCatalog(this.catalogDTO.getId());
                    break;
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        DataProcessor.getInstance().removeChangeListener(this);
    }
}
