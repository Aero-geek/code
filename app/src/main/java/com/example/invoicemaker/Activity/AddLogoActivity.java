package com.example.invoicemaker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.BusinessDTO;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

public class AddLogoActivity extends AppCompatActivity implements OnClickListener {
    public static String TAG = "AddLogoActivity";
    private long businessId;
    private TextView choose_logo;
    private TextView delete_logo;
    private AlertDialog dialog;
    private String imgUrl = "";
    private ImageView logo_image;
    private Toolbar toolbar;
    private Uri uri = null;

    public static void start(Context context, String str, long j) {
        Intent intent = new Intent(context, AddLogoActivity.class);
        intent.putExtra(MyConstants.IMAGE_URL, str);
        intent.putExtra(MyConstants.BUSINESS_DTO, j);
        context.startActivity(intent);
    }

    private static void requestPermission(final Context context) {
        Activity activity = (Activity) context;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            new AlertDialog.Builder(context).setMessage(context.getResources().getString(R.string.permission_storage)).setPositiveButton((CharSequence) "ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
                }
            }).setNegativeButton((CharSequence) "no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_add_logo);
        if (AppCore.isNetworkAvailable(AddLogoActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        updateLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, AddLogoActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void getIntentData() {
        this.imgUrl = getIntent().getStringExtra(MyConstants.IMAGE_URL);
        this.businessId = getIntent().getLongExtra(MyConstants.BUSINESS_DTO, 0);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((CharSequence) "Business Logo");
        this.logo_image = (ImageView) findViewById(R.id.logo_image);
        this.choose_logo = (TextView) findViewById(R.id.choose_logo);
        this.choose_logo.setOnClickListener(this);
        this.delete_logo = (TextView) findViewById(R.id.delete_logo);
        this.delete_logo.setOnClickListener(this);
    }

    private void updateLayout() {
        if (!TextUtils.isEmpty(this.imgUrl)) {
            MyConstants.loadImage((Context) this, this.imgUrl, this.logo_image);
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else if (iArr.length != 0 && iArr[0] == 0) {
            showPhotoChooserDialog();
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.choose_logo) {
            Log.e("choose_logo", "1");
            if (id == R.id.delete_logo) {
                Log.e("choose_logo", "2");
                deleteImage();
            }
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            Log.e("choose_logo", "3");
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, 103);
        } else {
            requestPermission(this);
        }
    }

    private void deleteImage() {
        Log.e("choose_logo", "5");
        if (!TextUtils.isEmpty(this.imgUrl)) {
            Log.e("choose_logo", "6");
            this.imgUrl = MyConstants.deleteFile(this.imgUrl);
            LoadDatabase.getInstance().updateBusinessLogo(this.businessId, this.imgUrl);
            //Glide.with((FragmentActivity) this).load(Integer.valueOf(getResources().getIdentifier("ic_add_img", "drawable", getPackageName()))).into(this.logo_image);
            Glide.with(AddLogoActivity.this).load(R.drawable.ic_add_img).into(this.logo_image);
        }
        Glide.with(AddLogoActivity.this).load(R.drawable.ic_add_img).into(this.logo_image);
        this.uri = null;
    }

    private void showPhotoChooserDialog() {
        View inflate = getLayoutInflater().inflate(R.layout.photo_choose_options, null);
        ((TextView) inflate.findViewById(R.id.take_photo)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        ((TextView) inflate.findViewById(R.id.browse_photo)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                AddLogoActivity.this.startActivityForResult(intent, 103);
            }
        });
        this.dialog = new AlertDialog.Builder(this).setView(inflate).show();
    }

    protected void onPause() {
        super.onPause();
        if (this.dialog != null) {
            this.dialog.dismiss();
        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 103 && i2 == -1 && intent != null) {
            this.uri = intent.getData();
            MyConstants.loadImage((Context) this, this.uri, this.logo_image);
        }
    }

    private void updateOnBackPressed() {
        Exception exception;
        Throwable th;
        if (this.uri != null) {
            FileOutputStream fileOutputStream = null;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MyConstants.getRootDirectory(AddLogoActivity.this));
            stringBuilder.append(File.separator);
            stringBuilder.append("business_logo_");
            stringBuilder.append(timestamp);
            stringBuilder.append(".jpg");
            String stringBuilder2 = stringBuilder.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), this.uri);
                OutputStream fileOutputStream2 = new FileOutputStream(new File(stringBuilder2));
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream2);
                    bitmap.recycle();
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    deleteImage();
                    if (this.businessId <= 0) {
                        this.businessId = LoadDatabase.getInstance().saveBusinessInformation(new BusinessDTO());
                    }
                    LoadDatabase.getInstance().updateBusinessLogo(this.businessId, stringBuilder2);
                } catch (Exception e2) {
                    exception = e2;

                    try {
                        exception.printStackTrace();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        deleteImage();
                        if (this.businessId <= 0) {
                            this.businessId = LoadDatabase.getInstance().saveBusinessInformation(new BusinessDTO());
                        }
                        LoadDatabase.getInstance().updateBusinessLogo(this.businessId, stringBuilder2);
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                                try {
                                    throw th;
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        }
                        deleteImage();
                        if (this.businessId <= 0) {
                            this.businessId = LoadDatabase.getInstance().saveBusinessInformation(new BusinessDTO());
                        }
                        LoadDatabase.getInstance().updateBusinessLogo(this.businessId, stringBuilder2);
                        try {
                            throw th;
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    deleteImage();
                    if (this.businessId <= 0) {
                        this.businessId = LoadDatabase.getInstance().saveBusinessInformation(new BusinessDTO());
                    }
                    LoadDatabase.getInstance().updateBusinessLogo(this.businessId, stringBuilder2);
                    try {
                        throw th;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            } catch (Exception e4) {
                exception = e4;
                exception.printStackTrace();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                deleteImage();
                if (this.businessId <= 0) {
                    this.businessId = LoadDatabase.getInstance().saveBusinessInformation(new BusinessDTO());
                }
                LoadDatabase.getInstance().updateBusinessLogo(this.businessId, stringBuilder2);
            }
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
}
