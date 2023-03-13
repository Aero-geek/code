package com.example.invoicemaker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ItemDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class MyItemActivity extends AppCompatActivity implements OnClickListener {
    public static String ITEM_DTO = "item_dto";
    private String TAG = "MyItemActivity";
    private ImageView delete_item;
    private ItemDTO itemDTO = null;
    private EditText item_additional_details;
    private EditText item_name;
    private Switch item_taxable;
    private EditText item_unit_cost;
    private Toolbar toolbar;
    private double unitCost = 0.0d;

    public static void start(Context context, ItemDTO itemDTO) {
        Intent intent = new Intent(context, MyItemActivity.class);
        intent.putExtra(ITEM_DTO, itemDTO);
        context.startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_my_item);
        if (AppCore.isNetworkAvailable(MyItemActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, MyItemActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.itemDTO = (ItemDTO) getIntent().getSerializableExtra(ITEM_DTO);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        boolean z = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.delete_item = (ImageView) findViewById(R.id.delete_item);
        this.delete_item.setOnClickListener(this);
        if (this.itemDTO != null) {
            getSupportActionBar().setTitle((CharSequence) "Edit Item");
            this.delete_item.setVisibility(View.VISIBLE);
        } else {
            getSupportActionBar().setTitle((CharSequence) "New Item");
        }
        this.item_name = (EditText) findViewById(R.id.item_name);
        this.item_unit_cost = (EditText) findViewById(R.id.item_unit_cost);
        this.item_additional_details = (EditText) findViewById(R.id.item_additional_details);
        this.item_taxable = (Switch) findViewById(R.id.item_taxable);
        if (this.itemDTO != null) {
            this.item_name.setText(this.itemDTO.getItemName());
            EditText editText = this.item_unit_cost;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(this.itemDTO.getUnitCost());
            editText.setText(stringBuilder.toString());
            this.item_additional_details.setText(this.itemDTO.getItemDescription());
            Switch switchR = this.item_taxable;
            if (this.itemDTO.getTexable() != 1) {
                z = false;
            }
            switchR.setChecked(z);
        }
        findViewById(R.id.cancel_item).setOnClickListener(this);
        findViewById(R.id.save_item).setOnClickListener(this);
    }

    private void messageForDiscardChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.discard_changes_message));
        builder.setPositiveButton((CharSequence) "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MyItemActivity.this.finish();
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
        hideKeyboard(MyItemActivity.this);
        messageForDiscardChanges();
    }

    public boolean onSupportNavigateUp() {
        messageForDiscardChanges();
        return true;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel_item) {
            finish();
        } else if (id == R.id.delete_item) {
            LoadDatabase.getInstance().deleteMyItem(this.itemDTO.getId());
            finish();
        } else if (id == R.id.save_item) {
            saveItem();
        }
    }

    private void saveItem() {
        String trim = this.item_name.getText().toString().trim();
        if (trim.equalsIgnoreCase("")) {
            this.item_name.setError("Cannot be empty");
            return;
        }
        try {
            this.unitCost = Double.valueOf(this.item_unit_cost.getText().toString().trim()).doubleValue();
        } catch (Exception unused) {
            this.unitCost = 0.0d;
        }
        if (this.itemDTO == null) {
            this.itemDTO = new ItemDTO();
        }
        this.itemDTO.setItemName(trim);


        this.itemDTO.setUnitCost(String.valueOf(MyConstants.formatDecimal(Double.valueOf(unitCost))));
        this.itemDTO.setItemDescription(this.item_additional_details.getText().toString().trim());
        this.itemDTO.setTexable(item_taxable.isChecked() ? 1 : 0);
        if (this.itemDTO.getId() > 0) {
            LoadDatabase.getInstance().updateMyItem(this.itemDTO);
        } else {
            LoadDatabase.getInstance().saveMyItem(this.itemDTO);
        }
        finish();
    }
}


