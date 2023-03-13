package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;

public class PhotoUploadActivity extends AppCompatActivity implements OnClickListener {
    private static final int SELECT_PICTURE = 1;
    public String TAG = "PhotoUploadActivity";
    String filePath = "";
    private ImageView uploaded_image;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_photo_upload);
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, PhotoUploadActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {
        findViewById(R.id.choose_logo_text).setOnClickListener(this);
        findViewById(R.id.delete_logo_text).setOnClickListener(this);
        this.uploaded_image = (ImageView) findViewById(R.id.uploaded_image);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.choose_logo_text) {
            choosePhoto();
        }
    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            this.filePath = getPath(intent.getData());
            if (this.filePath != null) {
                System.out.println(this.filePath);
            } else {
                System.out.println("selectedImagePath is null");
            }
            if (this.filePath != null) {
                System.out.println("selectedImagePath is the right one for you!");
            } else {
                System.out.println("filemanagerstring is the right one for you!");
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor managedQuery = managedQuery(uri, new String[]{"_data"}, null, null, null);
        if (managedQuery == null) {
            return null;
        }
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }
}
