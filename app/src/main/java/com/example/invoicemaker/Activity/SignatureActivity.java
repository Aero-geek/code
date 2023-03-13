package com.example.invoicemaker.Activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.Dto.SignedDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;
import com.kyanogen.signatureview.SignatureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

public class SignatureActivity extends AppCompatActivity implements OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static SettingsDTO settingsDTO = null;
    private static String signedDate = "";
    private static TextView signed_date;
    public String TAG = SignatureActivity.class.getName();
    SignatureView signatureView;
    private long catalogId;
    private String signatureUrl = "";

    public static void start(Context context, long j, String str, String str2) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
        intent.putExtra(MyConstants.SIGNED_DATE, str);
        intent.putExtra(MyConstants.SIGNED_URL, str2);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_signature);
        if (AppCore.isNetworkAvailable(SignatureActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (this.catalogId > 0) {
            updateLayout();
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, SignatureActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else {
            i = iArr[0];
        }
    }

    private void getIntentData() {
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
        signedDate = getIntent().getStringExtra(MyConstants.SIGNED_DATE);
        this.signatureUrl = getIntent().getStringExtra(MyConstants.SIGNED_URL);
        settingsDTO = SettingsDTO.getSettingsDTO();
    }

    private void initLayout() {
        this.signatureView = (SignatureView) findViewById(R.id.signature_view);
        signed_date = (TextView) findViewById(R.id.signed_date);

        if (TextUtils.isEmpty(signedDate)) {
            signedDate = String.valueOf(Calendar.getInstance().getTimeInMillis());
        }
        signed_date.setText(MyConstants.formatDate(this, Long.parseLong(signedDate), settingsDTO.getDateFormat()));
        findViewById(R.id.save_sign).setOnClickListener(this);
        findViewById(R.id.clear_sign).setOnClickListener(this);
        findViewById(R.id.delete_sign).setOnClickListener(this);
        findViewById(R.id.cancel_sign).setOnClickListener(this);
        signed_date.setOnClickListener(this);

    }

    private void updateLayout() {
        if (!TextUtils.isEmpty(this.signatureUrl)) {
            try {
                this.signatureView.setBitmap(BitmapFactory.decodeFile(this.signatureUrl).copy(Config.ARGB_8888, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_sign:
                finish();
                break;
            case R.id.clear_sign:
                clearSign();
                return;
            case R.id.delete_sign:
                deleteSign();
                finish();
                return;
            case R.id.save_sign:
                saveSign();
                finish();
                return;
            case R.id.signed_date:
                break;
            case R.id.signed_Lay:

                break;
            default:
                return;
        }
        updateDate();
    }

    private void updateDate() {
        new DatePickerFragment().show(getSupportFragmentManager(), "signatureDatePicker");
    }

    private void saveSign() {
        if (this.catalogId == 0) {
            this.catalogId = MyConstants.createNewInvoice();
        }
        try {
            Bitmap signatureBitmap = this.signatureView.getSignatureBitmap();
            if (this.signatureUrl == null || this.signatureUrl.length() == 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MyConstants.getRootDirectory(getApplicationContext()));
                stringBuilder.append(File.separator);
                stringBuilder.append("signature");
                String stringBuilder2 = stringBuilder.toString();
                File file = new File(stringBuilder2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                timestamp.setTime(Long.parseLong(signedDate));
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append(File.separator);
                stringBuilder3.append("Signature_");
                stringBuilder3.append(timestamp);
                stringBuilder3.append(".jpg");
                this.signatureUrl = stringBuilder3.toString();
            }
            OutputStream fileOutputStream = new FileOutputStream(new File(this.signatureUrl));
            signatureBitmap.compress(CompressFormat.PNG, 40, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SignedDTO signedDTO = new SignedDTO();
        signedDTO.setCatalogId(this.catalogId);
        signedDTO.setSignedDate(signedDate);
        signedDTO.setSignedUrl(this.signatureUrl);
        LoadDatabase.getInstance().updateInvoiceSignature(signedDTO);
        return;
    }

    private void clearSign() {
        this.signatureView.clearCanvas();
    }

    private void deleteSign() {
        if (!TextUtils.isEmpty(this.signatureUrl)) {
            File file = new File(this.signatureUrl);
            if (file.exists()) {
                file.delete();
            }
            SignedDTO signedDTO = new SignedDTO();
            signedDTO.setCatalogId(this.catalogId);
            LoadDatabase.getInstance().updateInvoiceSignature(signedDTO);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
        public Dialog onCreateDialog(Bundle bundle) {
            Calendar instance = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, instance.get(1), instance.get(Calendar.MONTH), instance.get(5));
        }

        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar instance = Calendar.getInstance();
            instance.set(i, i2, i3);
            SignatureActivity.signedDate = String.valueOf(instance.getTimeInMillis());
            SignatureActivity.signed_date.setText(MyConstants.formatDate(getActivity(), Long.valueOf(SignatureActivity.signedDate).longValue(), SignatureActivity.settingsDTO.getDateFormat()));
        }
    }
}
