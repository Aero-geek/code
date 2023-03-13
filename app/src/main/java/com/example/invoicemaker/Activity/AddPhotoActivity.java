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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.ImageDTO;
import com.example.invoicemaker.Listener.ConfirmListener;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;
import com.example.invoicemaker.utils.MyConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

public class AddPhotoActivity extends AppCompatActivity implements OnClickListener {
    public static String TAG = "AddPhotoActivity";
    private LinearLayout add_photo_layout;
    private EditText additional_details;
    private long catalogId;
    private ImageView delete_item;
    private EditText description;
    private AlertDialog dialog;
    private ImageDTO imageDTO;
    private ImageView item_image;
    private String newPath = "";
    private String oldPath = "";
    private Toolbar toolbar;
    private Uri uri;

    public static void start(Context context, ImageDTO imageDTO, long j) {
        Intent intent = new Intent(context, AddPhotoActivity.class);
        intent.putExtra(MyConstants.IMAGE_DTO, imageDTO);
        intent.putExtra(MyConstants.CATALOG_DTO, j);
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
        setContentView((int) R.layout.activity_add_photo);
        if (AppCore.isNetworkAvailable(AddPhotoActivity.this)) {
            addBanner();
        }
        getIntentData();
        initLayout();
        if (this.imageDTO != null) {
            updateImageInfo();
        }
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, AddPhotoActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void updateImageInfo() {
        this.description.setText(this.imageDTO.getDescription());
        this.additional_details.setText(this.imageDTO.getAdditionalDetails());
        this.oldPath = this.imageDTO.getImageUrl();
        MyConstants.loadImage((Context) this, this.oldPath, this.item_image);
    }

    private void getIntentData() {
        this.imageDTO = (ImageDTO) getIntent().getSerializableExtra(MyConstants.IMAGE_DTO);
        this.catalogId = getIntent().getLongExtra(MyConstants.CATALOG_DTO, 0);
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((CharSequence) "Photo");
        this.add_photo_layout = (LinearLayout) findViewById(R.id.add_photo_layout);
        this.add_photo_layout.setOnClickListener(this);
        this.description = (EditText) findViewById(R.id.description);
        this.additional_details = (EditText) findViewById(R.id.additional_details);
        this.item_image = (ImageView) findViewById(R.id.item_image);
        this.delete_item = (ImageView) findViewById(R.id.delete_item);
        this.delete_item.setOnClickListener(this);
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
        if (id != R.id.add_photo_layout) {
            if (id == R.id.delete_item) {
                deleteImage();
            }
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, 103);
        } else {
            requestPermission(this);
        }
    }

    private void deleteImage() {
        if (TextUtils.isEmpty(this.oldPath)) {
            finish();
        } else {
            MyConstants.showConfirmDialog(this, "Remove item", "Are you sure you want to remove this item from invoice? ", new ConfirmListener() {
                public void cancel() {
                }

                public void ok() {
                    LoadDatabase.getInstance().deleteInvoiceImage(AddPhotoActivity.this.imageDTO);
                    AddPhotoActivity.this.finish();
                }
            });
        }
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
                AddPhotoActivity.this.startActivityForResult(intent, 103);
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
        if (i == 103 && i2 == -1 && intent != null) {
            this.uri = intent.getData();
            MyConstants.loadImage((Context) this, this.uri, this.item_image);
            this.description.setText(MyConstants.getFileName(this, this.uri));
        }
    }

    private void updateOnBackPressed() {
        Exception exception;
        Throwable th;
        if (this.uri != null) {
            FileOutputStream fileOutputStream = null;
            try {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String rootDirectory = MyConstants.getRootDirectory(getApplicationContext());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(rootDirectory);
                stringBuilder.append(File.separator);
                stringBuilder.append(timestamp);
                stringBuilder.append(".jpg");
                this.newPath = stringBuilder.toString();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), this.uri);
                OutputStream fileOutputStream2 = new FileOutputStream(new File(this.newPath));
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream2);
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e2) {
                    exception = e2;

                    try {
                        exception.printStackTrace();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (this.catalogId == 0) {
                            this.catalogId = MyConstants.createNewInvoice();
                        }
                        if (this.imageDTO == null) {
                            this.imageDTO = new ImageDTO();
                        }
                        this.imageDTO.setCatalogId(this.catalogId);
                        this.imageDTO.setImageUrl(this.newPath);
                        this.imageDTO.setDescription(this.description.getText().toString().trim());
                        this.imageDTO.setAdditionalDetails(this.additional_details.getText().toString().trim());
                        if (this.imageDTO.getId() > 0) {
                            LoadDatabase.getInstance().saveInvoiceImage(this.imageDTO);
                        } else {
                            LoadDatabase.getInstance().updateInvoiceImage(this.oldPath, this.imageDTO);
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
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
                if (this.catalogId == 0) {
                    this.catalogId = MyConstants.createNewInvoice();
                }
                if (this.imageDTO == null) {
                    this.imageDTO = new ImageDTO();
                }
                this.imageDTO.setCatalogId(this.catalogId);
                this.imageDTO.setImageUrl(this.newPath);
                this.imageDTO.setDescription(this.description.getText().toString().trim());
                this.imageDTO.setAdditionalDetails(this.additional_details.getText().toString().trim());
                if (this.imageDTO.getId() > 0) {
                    LoadDatabase.getInstance().updateInvoiceImage(this.oldPath, this.imageDTO);
                } else {
                    LoadDatabase.getInstance().saveInvoiceImage(this.imageDTO);
                }
            }
            if (this.catalogId == 0) {
                this.catalogId = MyConstants.createNewInvoice();
            }
            if (this.imageDTO == null) {
                this.imageDTO = new ImageDTO();
            }
            this.imageDTO.setCatalogId(this.catalogId);
            this.imageDTO.setImageUrl(this.newPath);
            this.imageDTO.setDescription(this.description.getText().toString().trim());
            this.imageDTO.setAdditionalDetails(this.additional_details.getText().toString().trim());
            if (this.imageDTO.getId() > 0) {
                LoadDatabase.getInstance().updateInvoiceImage(this.oldPath, this.imageDTO);
            } else {
                LoadDatabase.getInstance().saveInvoiceImage(this.imageDTO);
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
