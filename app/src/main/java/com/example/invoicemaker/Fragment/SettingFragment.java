package com.example.invoicemaker.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.invoicemaker.Activity.AddLogoActivity;
import com.example.invoicemaker.Activity.BusinessDetailsActivity;
import com.example.invoicemaker.Activity.PaymentOptionActivity;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.DatabaseHelper;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.MainActivity;
import com.example.invoicemaker.R;
import com.example.invoicemaker.utils.DataProcessor;
import com.example.invoicemaker.utils.FilePickerActivity;
import com.example.invoicemaker.utils.LockScreen;
import com.example.invoicemaker.utils.ModelChangeListener;
import com.example.invoicemaker.utils.MyConstants;
import com.example.invoicemaker.utils.SessionManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SettingFragment extends Fragment implements OnClickListener, ModelChangeListener {

    public static final int REQUEST_CODE_RESTORE = 14;
    public static final int REQUEST_FILE_PICKER = 80;
    public static final String TAG = "SettingFragment";
    private final int CLOUD = 2;
    private final int DEVICE = 1;
    String DB_FILEPATH;
    TextView change_pin;
    TextView currency_format_value;
    TextView date_format_value;
    String filename;
    RelativeLayout pin_lock_layout;
    Switch pin_lock_switch;
    private BusinessDTO businessDTO;
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
            this.view = layoutInflater.inflate(R.layout.settings_fragment_layout, viewGroup, false);
            DataProcessor.getInstance().addChangeListener(this);
            initLayout();
        }
        return this.view;
    }

    public void onResume() {
        super.onResume();
        loadData();
        updateLayout();
    }

    private void loadData() {
        this.businessDTO = LoadDatabase.getInstance().getBusinessInformation();
        this.settingsDTO = SettingsDTO.getSettingsDTO();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.my_item_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void initLayout() {
        this.currency_format_value = (TextView) this.view.findViewById(R.id.currency_format_value);
        this.date_format_value = (TextView) this.view.findViewById(R.id.date_format_value);
        this.view.findViewById(R.id.select_logo).setOnClickListener(this);
        this.view.findViewById(R.id.business_details).setOnClickListener(this);
        this.view.findViewById(R.id.payment_options).setOnClickListener(this);
        this.view.findViewById(R.id.currency_format_layout).setOnClickListener(this);
        this.view.findViewById(R.id.date_format_layout).setOnClickListener(this);
        this.view.findViewById(R.id.backup).setOnClickListener(this);
        this.view.findViewById(R.id.restore).setOnClickListener(this);
        this.view.findViewById(R.id.export).setOnClickListener(this);
        this.view.findViewById(R.id.reset).setOnClickListener(this);
        managePinLayout();
    }

    private void managePinLayout() {
        this.pin_lock_layout = (RelativeLayout) this.view.findViewById(R.id.pin_lock_layout);
        this.pin_lock_switch = (Switch) this.view.findViewById(R.id.pin_lock_switch);
        this.change_pin = (TextView) this.view.findViewById(R.id.change_pin);
        this.pin_lock_layout.setOnClickListener(this);
        this.change_pin.setOnClickListener(this);
        if (SessionManager.getInstance(this.mActivity.getApplicationContext()).getPasscode() == -1) {
            this.pin_lock_switch.setChecked(false);
            this.change_pin.setTextColor(getResources().getColor(17170432));
            return;
        }
        this.pin_lock_switch.setChecked(true);
        this.change_pin.setTextColor(getResources().getColor(R.color.newAppColor));
    }

    private void updateLayout() {
        this.currency_format_value.setText(getResources().getStringArray(R.array.currency_symbols)[this.settingsDTO.getCurrencyFormat()]);
        this.date_format_value.setText(MyConstants.formatDate(this.mActivity, Calendar.getInstance().getTimeInMillis(), this.settingsDTO.getDateFormat()));
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.backup:
                backup();
                return;
            case R.id.business_details:
                BusinessDetailsActivity.start(view.getContext(), this.businessDTO);
                return;
            case R.id.change_pin:
                if (this.pin_lock_switch.isChecked()) {
                    intent = new Intent(this.mActivity, LockScreen.class);
                    intent.putExtra("ScreenType", 1);
                    startActivity(intent);
                    return;
                }
                Toast.makeText(this.mActivity, getResources().getString(R.string.change_pin_message), 0).show();
                return;
            case R.id.currency_format_layout:
                showCurrencyDialog();
                return;
            case R.id.date_format_layout:
                showDateDialog();
                return;
            case R.id.export:
                export();
                return;
            case R.id.payment_options:
                PaymentOptionActivity.start(view.getContext(), this.businessDTO, 0);
                return;
            case R.id.pin_lock_layout:
                if (this.pin_lock_switch.isChecked()) {
                    this.pin_lock_switch.setChecked(false);
                    SessionManager.getInstance(this.mActivity.getApplicationContext()).setPasscode(-1);
                    this.change_pin.setTextColor(getResources().getColor(17170432));
                    return;
                }
                intent = new Intent(this.mActivity, LockScreen.class);
                intent.putExtra("ScreenType", 1);
                startActivity(intent);
                return;
            case R.id.reset:
                reset();
                return;
            case R.id.restore:
                restore();
                return;
            case R.id.select_logo:
                AddLogoActivity.start(view.getContext(), this.businessDTO.getLogo(), this.businessDTO.getId());
                return;
            default:
                return;
        }
    }

    private void export() {
        Builder builder = new Builder(this.mActivity);
        CharSequence[] charSequenceArr = new String[]{"Device", "Cloud"};
        builder.setTitle((CharSequence) "Export");
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingFragment.this.exportCSV(i);
            }
        });
        builder.create();
        builder.show();
    }

    private void exportCSV(int i) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    Toast.makeText(this.mActivity, "You have denied this permission.", 1).show();
                    return;
                }
                ActivityCompat.requestPermissions(this.mActivity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
            } else if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                exportCSVOperation(1);
            }
        } else if (i == 1) {
            exportCSVOperation(2);
        }
    }

    private void reset() {
        Builder builder = new Builder(this.mActivity);
        builder.setTitle((CharSequence) "RESET");
        builder.setMessage((CharSequence) "Do you want to erase all the data!");
        builder.setPositiveButton((CharSequence) "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog show = ProgressDialog.show(SettingFragment.this.mActivity, SettingFragment.this.mActivity.getString(R.string.app_name), "Proccessing...", true);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        LoadDatabase.getInstance().truncateDB(SettingFragment.this.mActivity.getApplicationContext());
                        ((AppCore) SettingFragment.this.mActivity.getApplication()).init();
                        Intent intent = new Intent(SettingFragment.this.mActivity, MainActivity.class);
                        intent.putExtra("from_app", true);
                        intent.addFlags(67141632);
                        SettingFragment.this.startActivity(intent);
                        Toast.makeText(SettingFragment.this.mActivity.getApplicationContext(), "ALL DATA HAS BEEN RESET", 0).show();
                        show.dismiss();
                    }
                }, 1000);
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create();
        builder.show();
    }

    private void restore() {
        Builder builder = new Builder(this.mActivity);
        CharSequence[] charSequenceArr = new String[]{"Device", "Cloud"};
        builder.setTitle((CharSequence) "Restore");
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingFragment.this.importDb(i);
            }
        });
        builder.create();
        builder.show();
    }

    private void importDb(int i) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    Toast.makeText(this.mActivity, "You have denied this permission.", 1).show();
                    return;
                }
                ActivityCompat.requestPermissions(this.mActivity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 102);
            } else if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                this.mActivity.startActivityForResult(new Intent(this.mActivity, FilePickerActivity.class), 80);
            }
        } else if (i == 1) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("application/x-sqlite3");
            this.mActivity.startActivityForResult(Intent.createChooser(intent, "RESTORE"), 14);
        }
    }

    private void backup() {
        Builder builder = new Builder(this.mActivity);
        CharSequence[] charSequenceArr = new String[]{"Device", "Cloud"};
        builder.setTitle((CharSequence) "Backup");
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingFragment.this.exportDb(i);
            }
        });
        builder.create();
        builder.show();
    }

    private void exportDb(int i) {
        if (i == 0) {
            if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    Toast.makeText(this.mActivity, "You have denied this permission.", 1).show();
                    return;
                }
                ActivityCompat.requestPermissions(this.mActivity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
            } else if (ContextCompat.checkSelfPermission(this.mActivity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                exportOperation(1);
            }
        } else if (i == 1) {
            exportOperation(2);
        }
    }

    public void exportOperation(final int i) {
        File externalCacheDir;
        if (i == 2) {
            externalCacheDir = this.mActivity.getExternalCacheDir();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            ContextWrapper contextWrapper = new ContextWrapper(getContext());
            String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
            stringBuilder.append(parentpath);
            stringBuilder.append("/Invoice/backup");
            externalCacheDir = new File(stringBuilder.toString());
            if (!externalCacheDir.exists()) {
                externalCacheDir.mkdir();
            }
        }
        String format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        StringBuilder stringBuilder2;
        if (i == 2) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(externalCacheDir.toString());
            stringBuilder2.append("/");
            stringBuilder2.append(format);
            stringBuilder2.append(".bak");
            this.filename = stringBuilder2.toString();
        } else {
            try {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(externalCacheDir.toString());
                stringBuilder2.append("/");
                stringBuilder2.append(format);
                stringBuilder2.append(".bak");
                this.filename = stringBuilder2.toString();
                new FileWriter(this.filename).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Dialog show = ProgressDialog.show(this.mActivity, this.mActivity.getString(R.string.app_name), "Proccessing...", true);
        new Thread() {
            public void run() {
                try {
                    SettingFragment.this.DB_FILEPATH = SettingFragment.this.mActivity.getDatabasePath(DatabaseHelper.DATABASE_NAME).toString();
                    if (new DatabaseHelper(SettingFragment.this.mActivity).copyDbOperation(SettingFragment.this.DB_FILEPATH, SettingFragment.this.filename)) {
                        SettingFragment.this.mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (i == 1) {
                                    Toast.makeText(SettingFragment.this.mActivity, "Backup is created to Invoice Folder", Toast.LENGTH_SHORT).show();
                                } else if (i == 2) {
                                    Intent intent = new Intent("android.intent.action.SEND");
                                    intent.setType("application/x-sqlite3");
                                    Context access$100 = SettingFragment.this.mActivity;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(SettingFragment.this.mActivity.getApplicationContext().getPackageName());
                                    stringBuilder.append(".my.package.name.provider");
                                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(access$100, stringBuilder.toString(), new File(SettingFragment.this.filename)));
                                    intent.putExtra("android.intent.extra.SUBJECT", "Invoice And Estimate");
                                    intent.setFlags(1);
                                    SettingFragment.this.mActivity.startActivity(Intent.createChooser(intent, "BACKUP"));
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        timerDelayRemoveDialog(1000, show);
    }

    private void exportCSVOperation(final int i) {
        File externalCacheDir;
        if (i == 2) {
            externalCacheDir = this.mActivity.getExternalCacheDir();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            ContextWrapper contextWrapper = new ContextWrapper(getContext());
            String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
            stringBuilder.append(parentpath);
            stringBuilder.append("/Invoice/Export");
            externalCacheDir = new File(stringBuilder.toString());
            if (!externalCacheDir.exists()) {
                externalCacheDir.mkdir();
            }
        }
        String format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        StringBuilder stringBuilder2;
        if (i == 2) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(externalCacheDir.toString());
            stringBuilder2.append("/");
            stringBuilder2.append(format);
            stringBuilder2.append(".csv");
            this.filename = stringBuilder2.toString();
        } else {
            try {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(externalCacheDir.toString());
                stringBuilder2.append("/");
                stringBuilder2.append(format);
                stringBuilder2.append(".csv");
                this.filename = stringBuilder2.toString();
                new FileWriter(this.filename).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Dialog show = ProgressDialog.show(this.mActivity, this.mActivity.getString(R.string.app_name), "Proccessing...", true);
        new Thread() {
            public void run() {
                try {
                    SettingFragment.this.createCSVFile(SettingFragment.this.filename);
                    SettingFragment.this.mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (i == 1) {
                                Toast.makeText(SettingFragment.this.mActivity, "CSV file is created to Export Folder", 0).show();
                            } else if (i == 2) {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("message/rfc822");
                                intent.putExtra("android.intent.extra.SUBJECT", "Invoice And Estimate Maker");
                                Context access$100 = SettingFragment.this.mActivity;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(SettingFragment.this.mActivity.getPackageName());
                                stringBuilder.append(".my.package.name.provider");
                                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(access$100, stringBuilder.toString(), new File(SettingFragment.this.filename)));
                                intent.setFlags(1);
                                SettingFragment.this.startActivity(Intent.createChooser(intent, "Select Application."));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        timerDelayRemoveDialog(1000, show);
    }

    private void createCSVFile(final String str) {
        new Thread() {
            public void run() {
                try {
                    FileWriter fileWriter = new FileWriter(str);
                    ArrayList allInvoices = LoadDatabase.getInstance().getAllInvoices();
                    fileWriter.append("Invoice");
                    fileWriter.append(",");
                    fileWriter.append("Date");
                    fileWriter.append(",");
                    fileWriter.append("Due");
                    fileWriter.append(",");
                    fileWriter.append("Client");
                    fileWriter.append(",");
                    fileWriter.append("Client Email");
                    fileWriter.append(",");
                    fileWriter.append("Tax");
                    fileWriter.append(",");
                    fileWriter.append("Discount");
                    fileWriter.append(",");
                    fileWriter.append("Shipping");
                    fileWriter.append(",");
                    fileWriter.append("Subtotal");
                    fileWriter.append(",");
                    fileWriter.append("Total");
                    fileWriter.append(",");
                    fileWriter.append("Paid");
                    fileWriter.append(",");
                    fileWriter.append("Balance due");
                    fileWriter.append(",");
                    fileWriter.append("PO #");
                    fileWriter.append(",");
                    fileWriter.append("10");
                    for (int i = 0; i < allInvoices.size(); i++) {
                        CatalogDTO calculateInvoiceBalance = LoadDatabase.getInstance().calculateInvoiceBalance(((CatalogDTO) allInvoices.get(i)).getId());
                        fileWriter.append(calculateInvoiceBalance.getCatalogName());
                        fileWriter.append(",");
                        fileWriter.append(MyConstants.sdf.format(new Date(Long.parseLong(calculateInvoiceBalance.getCreationDate()))));
                        fileWriter.append(",");
                        if (!(calculateInvoiceBalance.getDueDate() == null || calculateInvoiceBalance.getDueDate().equals(""))) {
                            fileWriter.append(MyConstants.sdf.format(new Date(Long.parseLong(calculateInvoiceBalance.getDueDate()))));
                        }
                        fileWriter.append(",");
                        if (calculateInvoiceBalance.getClientDTO().getClientName() != null) {
                            fileWriter.append(calculateInvoiceBalance.getClientDTO().getClientName());
                        }
                        fileWriter.append(",");
                        if (calculateInvoiceBalance.getClientDTO().getEmailAddress() != null) {
                            fileWriter.append(calculateInvoiceBalance.getClientDTO().getEmailAddress());
                        }
                        fileWriter.append(",");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getTaxAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getDiscountAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getInvoiceShippingDTO().getAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getSubTotalAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getTotalAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getPaidAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MyConstants.formatDecimal(Double.valueOf(calculateInvoiceBalance.getTotalAmount() - calculateInvoiceBalance.getPaidAmount())));
                        stringBuilder.append("");
                        fileWriter.append(stringBuilder.toString());
                        fileWriter.append(",");
                        if (calculateInvoiceBalance.getPoNumber() != null) {
                            fileWriter.append(calculateInvoiceBalance.getPoNumber());
                        }
                        fileWriter.append(",");
                        fileWriter.append("10");
                    }
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void timerDelayRemoveDialog(long j, final Dialog dialog) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, j);
    }

    void showCurrencyDialog() {
        Builder builder = new Builder(this.mActivity);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.currency_format_titles), this.settingsDTO.getCurrencyFormat(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingFragment.this.settingsDTO.setCurrencyFormat(i);
                LoadDatabase.getInstance().updateSettings(SettingFragment.this.settingsDTO);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void showDateDialog() {
        Builder builder = new Builder(this.mActivity);
        int length = getResources().getStringArray(R.array.date_format_titles).length;
        CharSequence[] charSequenceArr = new String[length];
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < length; i++) {
            charSequenceArr[i] = MyConstants.formatDate(this.mActivity, timeInMillis, i);
        }
        builder.setSingleChoiceItems(charSequenceArr, this.settingsDTO.getDateFormat(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingFragment.this.settingsDTO.setDateFormat(i);
                LoadDatabase.getInstance().updateSettings(SettingFragment.this.settingsDTO);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void onReceiveModelChange(String str, int i) {
        Gson gson = new Gson();
        if (i == 321) {
            this.settingsDTO = (SettingsDTO) gson.fromJson(str, SettingsDTO.class);
            updateLayout();
        } else if (i == 666) {
            managePinLayout();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        DataProcessor.getInstance().removeChangeListener(this);
    }
}
