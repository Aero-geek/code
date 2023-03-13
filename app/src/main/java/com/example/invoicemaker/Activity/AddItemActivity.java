package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ItemAssociatedDTO;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;
import com.google.android.material.textfield.TextInputLayout;

public class AddItemActivity extends AppCompatActivity implements TextWatcher, OnClickListener {
    private static final String TAG = "AddItemActivity";
    double cost = 0.0d;
    String strCost = "0.0";
    double disc = 0.0d;
    double quant = 0.0d;
    double rate = 0.0d;
    int taxable = 1;
    private EditText additional_details;
    private TextView amount;
    private long catalogId;
    private String currency_sign;
    private ImageView delete_item;
    private EditText discount;
    private int discountType;
    private TextInputLayout discount_layout;
    private ItemAssociatedDTO itemAssociatedDTO;
    private EditText item_name;
    private EditText quantity;
    private TextView save_item;
    private ToggleButton save_item_for_future;
    private ImageView search_item;
    private int taxType;
    private EditText tax_rate;
    private TextInputLayout tax_rate_layout;
    private CheckBox taxable_checkbox;
    private LinearLayout taxable_layout;
    private Toolbar toolbar;
    private EditText unit_cost;

    public static void start(Context context, ItemAssociatedDTO itemAssociatedDTO, long j, int i, int i2) {
        Log.e("start", "start");
        Intent intent = new Intent(context, AddItemActivity.class);
        //intent.setFlags(PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE);
        intent.putExtra(MyConstants.ITEM_ASSOCIATED_DTO, itemAssociatedDTO);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.DISCOUNT_TYPE, i);
        intent.putExtra(MyConstants.TAX_TYPE, i2);
        context.startActivity(intent);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_add_item);
        if (AppCore.isNetworkAvailable(AddItemActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, AddItemActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.itemAssociatedDTO = (ItemAssociatedDTO) getIntent().getSerializableExtra(MyConstants.ITEM_ASSOCIATED_DTO);
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.discountType = getIntent().getIntExtra(MyConstants.DISCOUNT_TYPE, 0);
        this.taxType = getIntent().getIntExtra(MyConstants.TAX_TYPE, 0);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.search_item = (ImageView) findViewById(R.id.search_item);
        this.delete_item = (ImageView) findViewById(R.id.delete_item);
        this.save_item = (TextView) findViewById(R.id.save_item);
        this.search_item.setOnClickListener(this);
        this.delete_item.setOnClickListener(this);
        this.save_item.setOnClickListener(this);
        this.item_name = (EditText) findViewById(R.id.item_name);
        this.unit_cost = (EditText) findViewById(R.id.unit_cost);
        this.unit_cost.addTextChangedListener(this);
        this.quantity = (EditText) findViewById(R.id.quantity);
        this.quantity.addTextChangedListener(this);
        this.discount = (EditText) findViewById(R.id.discount);
        this.discount.addTextChangedListener(this);
        this.amount = (TextView) findViewById(R.id.amount);
        this.taxable_checkbox = (CheckBox) findViewById(R.id.taxable_checkbox);
        this.tax_rate = (EditText) findViewById(R.id.tax_rate);
        this.tax_rate.addTextChangedListener(this);
        this.tax_rate_layout = (TextInputLayout) findViewById(R.id.tax_rate_layout);
        this.additional_details = (EditText) findViewById(R.id.additional_details);
        this.save_item_for_future = (ToggleButton) findViewById(R.id.save_item_for_future);
        this.discount_layout = (TextInputLayout) findViewById(R.id.discount_layout);
        this.taxable_layout = (LinearLayout) findViewById(R.id.taxable_layout);
        this.currency_sign = MyConstants.formatCurrency(this, SettingsDTO.getSettingsDTO().getCurrencyFormat());
        TextView textView = this.amount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        stringBuilder.append("0.00");
        textView.setText(stringBuilder.toString());
        this.taxable_checkbox.setChecked(true);
        this.taxable_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    if (AddItemActivity.this.taxType == 2) {
                        AddItemActivity.this.tax_rate_layout.setVisibility(View.VISIBLE);
                    } else {
                        AddItemActivity.this.tax_rate_layout.setVisibility(View.GONE);
                    }
                    AddItemActivity.this.taxable = 1;
                    return;
                }
                AddItemActivity.this.tax_rate_layout.setVisibility(View.GONE);
                AddItemActivity.this.taxable = 0;
            }
        });
        if (this.itemAssociatedDTO == null || this.itemAssociatedDTO.getId() <= 0) {
            this.search_item.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_item));
        } else {
            this.delete_item.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_item));
        }
        if (this.itemAssociatedDTO != null) {
            updateItemAsssociatedInfo();
        }
        if (this.discountType == 1) {
            this.discount_layout.setVisibility(View.VISIBLE);
        } else {
            this.discount_layout.setVisibility(View.GONE);
        }
        if (this.taxType == 3) {
            this.taxable_layout.setVisibility(View.GONE);
        } else {
            this.taxable_layout.setVisibility(View.VISIBLE);
        }
    }

    private void updateItemAsssociatedInfo() {
        this.item_name.setText(this.itemAssociatedDTO.getItemName());
        EditText editText = this.unit_cost;
        StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append(this.itemAssociatedDTO.getUnitCost());
        String d = this.itemAssociatedDTO.getUnitCost();
        int dotPos = String.valueOf(d).lastIndexOf(".");
        String subStr = String.valueOf(d).substring(dotPos);
        if (subStr.length() <= 2) {
            stringBuilder.append(this.itemAssociatedDTO.getUnitCost() + "0");
        } else {
            stringBuilder.append(this.itemAssociatedDTO.getUnitCost());
        }

        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        editText = this.quantity;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getQuantity());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        this.additional_details.setText(this.itemAssociatedDTO.getDescription());
        editText = this.discount;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getDiscount());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        editText = this.tax_rate;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getTaxRate());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        this.taxable = this.itemAssociatedDTO.getTaxAble();
        if (this.taxable == 1) {
            if (this.taxType == 2) {
                this.tax_rate_layout.setVisibility(View.VISIBLE);
            } else {
                this.tax_rate_layout.setVisibility(View.GONE);
            }
            this.taxable_checkbox.setChecked(true);
            return;
        }
        this.tax_rate_layout.setVisibility(View.GONE);
        this.taxable_checkbox.setChecked(false);
    }
    /*{
        this.item_name.setText(this.itemAssociatedDTO.getItemName());
        EditText editText = this.unit_cost;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getUnitCost());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        editText = this.quantity;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getQuantity());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        this.additional_details.setText(this.itemAssociatedDTO.getDescription());
        editText = this.discount;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getDiscount());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        editText = this.tax_rate;
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.itemAssociatedDTO.getTaxRate());
        stringBuilder.append("");
        editText.setText(stringBuilder.toString());
        this.taxable = this.itemAssociatedDTO.getTaxAble();
        if (this.taxable == 1) {
            if (this.taxType == 2) {
                this.tax_rate_layout.setVisibility(0);
            } else {
                this.tax_rate_layout.setVisibility(8);
            }
            this.taxable_checkbox.setChecked(true);
            return;
        }
        this.tax_rate_layout.setVisibility(8);
        this.taxable_checkbox.setChecked(false);
    }*/

    private void updateTotalAmount() {

        try {
            this.cost = Double.valueOf(this.unit_cost.getText().toString()).doubleValue();
            strCost = this.unit_cost.getText().toString();
        } catch (Exception unused) {
            this.cost = 0.00d;
            strCost = "0.00";
        }
        try {
            this.quant = Double.valueOf(this.quantity.getText().toString()).doubleValue();
        } catch (Exception unused2) {
            this.quant = 0.0d;

        }
        try {
            this.rate = Double.valueOf(this.tax_rate.getText().toString()).doubleValue();
        } catch (Exception unused3) {
            this.rate = 0.0d;
        }
        try {
            this.disc = Double.valueOf(this.discount.getText().toString()).doubleValue();
        } catch (Exception unused4) {
            this.disc = 0.0d;
        }
        double d = this.cost * this.quant;
        d -= (this.disc / 100.0d) * d;

        float myf = (float) (Float.parseFloat(strCost) * this.quant);

        int dotPos = String.valueOf(d).lastIndexOf(".");
        String subStr = String.valueOf(d).substring(dotPos);
        TextView textView = this.amount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        if (subStr.length() <= 2) {
            stringBuilder.append(MyConstants.formatDecimal(d) + "0");
        } else {
            stringBuilder.append(MyConstants.formatDecimal(d));
        }
        textView.setText(stringBuilder.toString());
    }
    /*{
        try {
            this.cost = Double.valueOf(this.unit_cost.getText().toString()).doubleValue();
        } catch (Exception unused) {
            this.cost = 0.0d;
        }
        try {
            this.quant = Double.valueOf(this.quantity.getText().toString()).doubleValue();
        } catch (Exception unused2) {
            this.quant = 0.0d;
        }
        try {
            this.rate = Double.valueOf(this.tax_rate.getText().toString()).doubleValue();
        } catch (Exception unused3) {
            this.rate = 0.0d;
        }
        try {
            this.disc = Double.valueOf(this.discount.getText().toString()).doubleValue();
        } catch (Exception unused4) {
            this.disc = 0.0d;
        }
        double d = this.cost * this.quant;
        d -= (this.disc / 100.0d) * d;


        TextView textView = this.amount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.currency_sign);
        stringBuilder.append(MyConstants.formatDecimal(d));
        textView.setText(stringBuilder.toString());
    }*/

    private void saveItem() {
        String obj = this.item_name.getText().toString();
        this.item_name.requestFocus();
        if (obj.trim().equalsIgnoreCase("")) {
            this.item_name.setError("Cannot be empty");
            return;
        }
        if (this.itemAssociatedDTO == null) {
            this.itemAssociatedDTO = new ItemAssociatedDTO();
        }
        updateTotalAmount();
        this.itemAssociatedDTO.setItemName(this.item_name.getText().toString().trim());
        this.itemAssociatedDTO.setDescription(this.additional_details.getText().toString().trim());
        this.itemAssociatedDTO.setUnitCost(strCost);
        this.itemAssociatedDTO.setQuantity(this.quant);
        this.itemAssociatedDTO.setTaxAble(this.taxable);
        this.itemAssociatedDTO.setTaxRate(this.rate);
        this.itemAssociatedDTO.setDiscount(this.disc);
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        this.itemAssociatedDTO.setCatalogId(this.catalogId);
        if (this.itemAssociatedDTO.getId() > 0) {
            LoadDatabase.getInstance().updateInvoiceItem(this.itemAssociatedDTO);
        } else {
            LoadDatabase.getInstance().saveInvoiceItem(this.itemAssociatedDTO);
        }
        if (this.save_item_for_future.isChecked()) {
            LoadDatabase.getInstance().saveMyItem(new ItemDTO(this.itemAssociatedDTO.getItemName(), this.itemAssociatedDTO.getDescription(), this.itemAssociatedDTO.getUnitCost(), this.itemAssociatedDTO.getTaxAble()));
        }
        finish();
    }
    /*{
        String obj = this.item_name.getText().toString();
        this.item_name.requestFocus();
        if (obj.trim().equalsIgnoreCase("")) {
            this.item_name.setError("Cannot be empty");
            return;
        }
        if (this.itemAssociatedDTO == null) {
            this.itemAssociatedDTO = new ItemAssociatedDTO();
        }
        updateTotalAmount();
        this.itemAssociatedDTO.setItemName(this.item_name.getText().toString().trim());
        this.itemAssociatedDTO.setDescription(this.additional_details.getText().toString().trim());
        this.itemAssociatedDTO.setUnitCost(String.valueOf(MyConstants.formatDecimal(Double.valueOf(cost))));
        this.itemAssociatedDTO.setQuantity(this.quant);
        this.itemAssociatedDTO.setTaxAble(this.taxable);
        this.itemAssociatedDTO.setTaxRate(this.rate);
        this.itemAssociatedDTO.setDiscount(this.disc);
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        this.itemAssociatedDTO.setCatalogId(this.catalogId);
        if (this.itemAssociatedDTO.getId() > 0) {
            LoadDatabase.getInstance().updateInvoiceItem(this.itemAssociatedDTO);
        } else {
            LoadDatabase.getInstance().saveInvoiceItem(this.itemAssociatedDTO);
        }
        if (this.save_item_for_future.isChecked()) {
            LoadDatabase.getInstance().saveMyItem(new ItemDTO(this.itemAssociatedDTO.getItemName(), this.itemAssociatedDTO.getDescription(), this.itemAssociatedDTO.getUnitCost(), this.itemAssociatedDTO.getTaxAble()));
        }
        finish();
    }*/

    private void messageForDiscardChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.discard_changes_message));
        builder.setPositiveButton((CharSequence) "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddItemActivity.this.finish();
            }
        });
        builder.setNegativeButton((CharSequence) "NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create();
        builder.show();
    }

    public void onBackPressed() {
        messageForDiscardChanges();
    }

    public boolean onSupportNavigateUp() {
        messageForDiscardChanges();
        return true;
    }

    public void afterTextChanged(Editable editable) {
        updateTotalAmount();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.delete_item) {
            LoadDatabase.getInstance().deleteInvoiceItem(this.itemAssociatedDTO.getId());
            finish();
        } else if (id == R.id.save_item) {
            saveItem();
        } else if (id == R.id.search_item) {
            SearchItemActivity.start(view.getContext(), this.catalogId);
        }
    }
}
