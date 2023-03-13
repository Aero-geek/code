package com.example.invoicemaker.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ClientDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

public class AddClientActivity extends AppCompatActivity implements OnClickListener {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static int SELECT_CONTACT = 987;
    public String TAG = "AddClientActivity";
    private TextView btn_delete;
    private TextView btn_import;
    private TextView btn_report;
    private TextView btn_save;
    private ClientDTO clientDTO;
    private EditText client_name;
    //  ImageView addClientImg;
    private EditText contact_address;
    private EditText email_address;
    private EditText fax_no;
    private long invoiceId;
    private boolean isInvoice;
    private EditText mobile_no;
    private EditText phone_no;
    private ImageView search_item;
    private EditText shipping_address_line1;
    private EditText shipping_address_name;
    private Toolbar toolbar;

    public static void start(Context context, ClientDTO clientDTO, long j, boolean z) {
        Intent intent = new Intent(context, AddClientActivity.class);
        intent.setFlags(1 << 26);
        intent.putExtra(MyConstants.CLIENT_DTO, clientDTO);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.IS_INVOICE, z);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_add_client);


        if (AppCore.isNetworkAvailable(AddClientActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, AddClientActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }


    private void getIntentData() {
        this.clientDTO = (ClientDTO) getIntent().getSerializableExtra(MyConstants.CLIENT_DTO);
        this.invoiceId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        this.isInvoice = getIntent().getBooleanExtra(MyConstants.IS_INVOICE, false);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (this.clientDTO.getId() == 0) {
            getSupportActionBar().setTitle((CharSequence) "New Client");
        } else {
            getSupportActionBar().setTitle((CharSequence) "Edit Client");
        }
        this.search_item = (ImageView) findViewById(R.id.search_item);
        this.search_item.setOnClickListener(this);
        this.client_name = (EditText) findViewById(R.id.client_name);
        //  this.addClientImg = (ImageView) findViewById(R.id.addClientImg);
        this.email_address = (EditText) findViewById(R.id.email_address);
        this.mobile_no = (EditText) findViewById(R.id.mobile_no);
        this.phone_no = (EditText) findViewById(R.id.phone_no);
        this.fax_no = (EditText) findViewById(R.id.fax_no);
        this.contact_address = (EditText) findViewById(R.id.contact_address);
        this.shipping_address_name = (EditText) findViewById(R.id.shipping_address_name);
        this.shipping_address_line1 = (EditText) findViewById(R.id.shipping_address_line1);
        if (this.clientDTO.getId() > 0) {
            updateClientInfo();
        } else {
            this.search_item.setVisibility(View.VISIBLE);
        }
        this.btn_import = (TextView) findViewById(R.id.import_from_contact);
        this.btn_delete = (TextView) findViewById(R.id.delete_client);
        this.btn_report = (TextView) findViewById(R.id.client_report);
        this.btn_save = (TextView) findViewById(R.id.save_client);
        this.btn_save.setOnClickListener(this);
        this.btn_delete.setOnClickListener(this);
        this.btn_import.setOnClickListener(this);
        this.btn_report.setOnClickListener(this);
        if (this.clientDTO.getId() != 0) {
            this.btn_report.setVisibility(View.VISIBLE);
            this.btn_delete.setVisibility(View.VISIBLE);
            this.btn_import.setVisibility(View.GONE);
        }
    }

    private void updateClientInfo() {
        this.client_name.setText(this.clientDTO.getClientName());
        this.email_address.setText(this.clientDTO.getEmailAddress());
        this.mobile_no.setText(this.clientDTO.getMobileNo());
        this.phone_no.setText(this.clientDTO.getPhoneNo());
        this.fax_no.setText(this.clientDTO.getFaxNo());
        this.contact_address.setText(this.clientDTO.getContactAdress());
        this.shipping_address_name.setText(this.clientDTO.getShippingAddress());
        this.shipping_address_line1.setText(this.clientDTO.getShippingLine1());
    }

    private void checkParmission() {
        if (VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {
            updateFromContact();
            return;
        }
        requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 100);
    }

    private void updateFromContact() {
        startActivityForResult(new Intent("android.intent.action.PICK", Contacts.CONTENT_URI), SELECT_CONTACT);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 100) {
            return;
        }
        if (iArr[0] == 0) {
            updateFromContact();
        } else {
            Toast.makeText(this, "Until you grant the permission, we cannot access contact information", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveClient() {
        //  addClientImg.setVisibility(View.GONE);
        String obj = this.client_name.getText().toString();
        this.client_name.requestFocus();
        if (obj.trim().equalsIgnoreCase("")) {
            this.client_name.setError("Cannot be empty");
            return;
        }
        this.clientDTO.setClientName(this.client_name.getText().toString().trim());
        this.clientDTO.setEmailAddress(this.email_address.getText().toString().trim());
        this.clientDTO.setMobileNo(this.mobile_no.getText().toString().trim());
        this.clientDTO.setPhoneNo(this.phone_no.getText().toString().trim());
        this.clientDTO.setFaxNo(this.fax_no.getText().toString().trim());
        this.clientDTO.setContactAdress(this.contact_address.getText().toString().trim());
        this.clientDTO.setShippingAddress(this.shipping_address_name.getText().toString().trim());
        this.clientDTO.setShippingLine1(this.shipping_address_line1.getText().toString().trim());
        if (this.invoiceId == 0 && this.isInvoice) {
            this.invoiceId = MyConstants.createNewInvoice();
        }
        this.clientDTO.setCatalogId(this.invoiceId);
        if (this.clientDTO.getId() <= 0) {
            this.clientDTO.setId(LoadDatabase.getInstance().saveClient(this.clientDTO));
            LoadDatabase.getInstance().addInvoiceClient(this.clientDTO);
        } else if (LoadDatabase.getInstance().updateClient(this.clientDTO) > 0) {
            LoadDatabase.getInstance().addInvoiceClient(this.clientDTO);
        }
        finish();
    }

    private void messageForDiscardChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.discard_changes_message));
        builder.setPositiveButton((CharSequence) "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddClientActivity.this.finish();
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.client_report:
                try {
                    ClientStatementPreviewActivity clientStatementPreviewActivity = new ClientStatementPreviewActivity();
                    ClientStatementPreviewActivity.start(this, this.clientDTO);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.delete_client:
                LoadDatabase.getInstance().deleteInvoiceClient(this.invoiceId, 0);
                LoadDatabase.getInstance().deleteInvoiceShipping(this.invoiceId);
                LoadDatabase.getInstance().deleteClient(this.clientDTO.getId());
                finish();
                return;
            case R.id.import_from_contact:
                checkParmission();
                return;
            case R.id.save_client:
                saveClient();
                return;
            case R.id.search_item:
                SearchClientActivity.start(view.getContext(), this.invoiceId, false);
                return;
            default:
                return;
        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == SELECT_CONTACT && i2 == -1) {
            try {
                Cursor query = getContentResolver().query(intent.getData(), null, null, null, null);
                if (query.moveToFirst()) {
                    CharSequence string;
                    String string2 = query.getString(query.getColumnIndex("_id"));
                    // addClientImg.setVisibility(View.GONE);
                    this.client_name.setText(query.getString(query.getColumnIndex("display_name")));
                    Cursor query2 = getContentResolver().query(Phone.CONTENT_URI, null, "contact_id = ?", new String[]{string2}, null);
                    while (query2.moveToNext()) {
                        string = query2.getString(query2.getColumnIndex("data1"));
                        int i3 = query2.getInt(query2.getColumnIndex("data2"));
                        if (i3 != 2) {
                            if (i3 != 12) {
                                if (i3 != 17) {
                                    switch (i3) {
                                        case 4:
                                        case 5:
                                            if (!this.fax_no.getText().toString().equals("")) {
                                                break;
                                            }
                                            this.fax_no.setText(string);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else if (this.phone_no.getText().toString().equals("")) {
                                this.phone_no.setText(string);
                            }
                        }
                        if (this.mobile_no.getText().toString().equals("")) {
                            this.mobile_no.setText(string);
                        }
                    }
                    query2.close();
                    Cursor query3 = getContentResolver().query(Email.CONTENT_URI, null, "contact_id = ?", new String[]{string2}, null);
                    if (query3.moveToFirst()) {
                        this.email_address.setText(query3.getString(query3.getColumnIndex("data1")));
                    }
                    query3.close();
                    Uri uri = StructuredPostal.CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("contact_id=");
                    stringBuilder.append(string2);
                    Cursor query4 = contentResolver.query(uri, null, stringBuilder.toString(), null, null);
                    while (query4.moveToNext()) {
                        String string3 = query4.getString(query4.getColumnIndex("data4"));
                        String string4 = query4.getString(query4.getColumnIndex("data7"));
                        String string5 = query4.getString(query4.getColumnIndex("data10"));
                        string = "";
                        if (string3 != null) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(string);
                            stringBuilder2.append(string3);
                            stringBuilder2.append(" ");
                            string = stringBuilder2.toString();
                        }
                        if (string4 != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(string);
                            stringBuilder.append(string4);
                            stringBuilder.append(" ");
                            string = stringBuilder.toString();
                        }
                        if (string5 != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(string);
                            stringBuilder.append(string5);
                            stringBuilder.append(" ");
                            string = stringBuilder.toString();
                        }
                        this.contact_address.setText(string);
                    }
                    query4.close();
                }
                query.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
