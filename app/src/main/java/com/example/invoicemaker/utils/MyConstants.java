package com.example.invoicemaker.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.CatalogDTO;
import com.example.invoicemaker.Listener.ConfirmListener;
import com.example.invoicemaker.R;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyConstants {
    public static final int ACTION_BUSINESS_INFORMATION_ADDED = 10001;
    public static final int ACTION_BUSINESS_INFORMATION_DELETED = 10003;
    public static final int ACTION_BUSINESS_INFORMATION_UPDATED = 10002;
    public static final int ACTION_INVOICE_ADDED = 2001;
    public static final int ACTION_INVOICE_CLIENT_ADDED = 4001;
    public static final int ACTION_INVOICE_CLIENT_DELETED = 4003;
    public static final int ACTION_INVOICE_CLIENT_UPDATED = 4002;
    public static final int ACTION_INVOICE_DELETED = 2003;
    public static final int ACTION_INVOICE_IMAGE_ADDED = 3004;
    public static final int ACTION_INVOICE_IMAGE_DELETED = 3006;
    public static final int ACTION_INVOICE_IMAGE_UPDATED = 3005;
    public static final int ACTION_INVOICE_ITEM_ADDED = 3001;
    public static final int ACTION_INVOICE_ITEM_DELETED = 3003;
    public static final int ACTION_INVOICE_ITEM_UPDATED = 3002;
    public static final int ACTION_INVOICE_LIST_UPDATE = 2001;
    public static final int ACTION_INVOICE_SHIPPING_ADDED = 5001;
    public static final int ACTION_INVOICE_SHIPPING_DELETED = 5003;
    public static final int ACTION_INVOICE_SHIPPING_UPDATED = 5002;
    public static final int ACTION_INVOICE_UPDATED = 2002;
    public static final int ACTION_PASSCODE_ADDED = 666;
    public static final int ACTION_SETTINGS_UPDATED = 321;
    public static final int ACTION_UPDATE_DISCOUNT_AMOUNT = 102;
    public static final int ACTION_UPDATE_INVOICE_BALANCE = 101;
    public static final int ACTION_UPDATE_INVOICE_SIGNATURE = 105;
    public static final int ACTION_UPDATE_PAID_AMOUNT = 104;
    public static final int ACTION_UPDATE_TAX_AMOUNT = 103;
    public static final int CATALOG_TYPE_ESTIMATE = 1;
    public static final int CATALOG_TYPE_INVOICE = 0;
    public static final int CLOSED_ESTIMATE = 2;
    public static final int DEFAULT_CURRENCY_FORMAT = 102;
    public static final int DEFAULT_DATE_FORMAT = 0;
    public static final int DISCOUNT_TYPE_FLAT_AMOUNT = 3;
    public static final int DISCOUNT_TYPE_NONE = 0;
    public static final int DISCOUNT_TYPE_PERCENTAGE = 2;
    public static final int DISCOUNT_TYPE_PER_ITEM = 1;
    public static final int OPEN_ESTIMATE = 1;
    public static final int OUTSTANDING_INVOICE = 1;
    public static final int PAID_INVOICE = 2;
    public static final int PICK_IMAGE = 103;
    public static final int REQUEST_CAMERA = 102;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 102;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    public static final int TAX_TYPE_DEDUCTED = 1;
    public static final int TAX_TYPE_NONE = 3;
    public static final int TAX_TYPE_ON_THE_TOTAL = 0;
    public static final int TAX_TYPE_PER_ITEM = 2;
    public static final int TYPE_PAYMENT_ADD = 0;
    public static final int TYPE_PAYMENT_EDIT = 1;
    public static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    public static String BUSINESS_DTO = "business_dto";
    public static String CALLER_ACTIVITY = "caller_activity";
    public static String CATALOG_DTO = "catalog_dto";
    public static int CATALOG_TYPE = 0;
    public static String CLIENT_DTO = "client_dto";
    public static String DISCOUNT_AMOUNT = "discount_amount";
    public static String DISCOUNT_AMOUNT_SUBTOTAL = "discount_amount_subtotal";
    public static String DISCOUNT_AMOUNT_TOTAL = "discount_amount_total";
    public static String DISCOUNT_TYPE = "discount_type";
    public static int DUPLICATE_ENTRY_FOR = 1;
    public static String FROM_CATALOG = "FROM_CATALOG";
    public static String IMAGE_DTO = "image_dto";
    public static String IMAGE_URL = "image_url";
    public static String IS_INVOICE = "IS_INVOICE";
    public static String ITEM_ASSOCIATED_DTO = "item_associated_dto";
    public static String OPERATION_TYPE = "operation_type";
    public static String PAID_AMOUNT = "paid_amount";
    public static String PAYMENT_DTO = "payment_dto";
    public static String SHIPPING_DTO = "shipping_dto";
    public static String SIGNED_DATE = "signed_date";
    public static String SIGNED_URL = "signed_url";
    public static String TAG = "MyConstants";
    public static String TAX_LABEL = "tax_label";
    public static String TAX_RATE = "tax_rate";
    public static String TAX_TYPE = "tax_type";
    public static String TOTAL_AMOUNT = "total_amount";
    public static boolean createDuplicateEntry = false;
    public static int invoiceCount = 1;

    public static String formatDate(Context context, long j, int i) {
        return new SimpleDateFormat(context.getResources().getStringArray(R.array.date_format_titles)[i], Locale.ENGLISH).format(Long.valueOf(j));
    }

    public static String formatCurrency(Context context, int i) {
        return context.getResources().getStringArray(R.array.currency_symbols)[i];
    }

    public static String getInvoiceName() {
        StringBuilder stringBuilder;
        if (CATALOG_TYPE == 1) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("ESTIMATE");
            stringBuilder.append(String.format("%d", new Object[]{Integer.valueOf(invoiceCount)}));
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("INVOICE");
        stringBuilder.append(String.format("%d", new Object[]{Integer.valueOf(invoiceCount)}));
        return stringBuilder.toString();
    }

    public static long createNewInvoice() {
        CatalogDTO catalogDTO = new CatalogDTO();
        catalogDTO.setCatalogName(getInvoiceName());
        catalogDTO.setCreationDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        catalogDTO.setTerms(1);
        catalogDTO.setDiscountType(0);
        catalogDTO.setTaxType(3);
        catalogDTO.setPaidStatus(1);
        invoiceCount++;
        return LoadDatabase.getInstance().saveInvoice(catalogDTO);
    }

    public static String getRootDirectory(Context context) {
        Exception e;
        String str = "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            ContextWrapper contextWrapper = new ContextWrapper(context);
            String parentpath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
            stringBuilder.append(parentpath);
            stringBuilder.append(File.separator);
            stringBuilder.append("invoice");
            String stringBuilder2 = stringBuilder.toString();
            try {
                File file = new File(stringBuilder2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return stringBuilder2;
            } catch (Exception e2) {
                String str2 = stringBuilder2;
                e = e2;
                str = str2;
                try {
                    e.printStackTrace();
                } catch (Throwable unused) {
                    return str;
                }
            } catch (Throwable unused2) {
                str = stringBuilder2;
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
        return str;
    }

    public static void loadImage(Context context, String str, ImageView imageView) {


        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context).load(str).thumbnail(0.5f).transition(new DrawableTransitionOptions().crossFade()).apply(requestOptions).into(imageView);
    }

    public static void loadImage(Context context, Uri uri, ImageView imageView) {

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context).load(uri).thumbnail(0.5f).transition(new DrawableTransitionOptions().crossFade()).apply(requestOptions).into(imageView);
    }

    public static void showConfirmDialog(Context context, String str, String str2, final ConfirmListener confirmListener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.confirm_dialog_layout);
        ((TextView) dialog.findViewById(R.id.title)).setText(str);
        ((TextView) dialog.findViewById(R.id.message)).setText(str2);
        dialog.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                confirmListener.ok();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                confirmListener.cancel();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static String getFileName(Context context, Uri uri) {
        String string;
        String str = "";
        Cursor query = context.getContentResolver().query(uri, null, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    string = query.getString(query.getColumnIndex("_display_name"));
                    query.close();
                    return string;
                }
            } catch (Exception unused) {
                query.close();
                return str;
            } catch (Throwable unused2) {
                query.close();
                return str;
            }
        }
        string = str;
        query.close();
        return string;
    }

    public static String deleteFile(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
            return "";
        } catch (Exception unused) {
            return "";
        } catch (Throwable unused2) {
            return "";
        }
    }

    public static double formatDecimal(Double d) {
        return new BigDecimal(d.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
