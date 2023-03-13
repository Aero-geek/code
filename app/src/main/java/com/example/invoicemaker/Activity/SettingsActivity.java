package com.example.invoicemaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invoicemaker.AppCore;
import com.example.invoicemaker.R;
import com.example.invoicemaker.ads.AdsProvider;

public class SettingsActivity extends AppCompatActivity implements OnClickListener {
    private TextView insert_logo_text;

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.settings_activity);
        if (AppCore.isNetworkAvailable(SettingsActivity.this)) {
            addBanner();
        }
        initLayout();
    }

    private void addBanner() {
        RelativeLayout bnnr = (RelativeLayout) findViewById(R.id.adView);
        Banner(bnnr, SettingsActivity.this);
    }

    public void Banner(final RelativeLayout Ad_Layout, final Context context) {
        AdsProvider.provider.addBanner(context, Ad_Layout);
    }

    private void initLayout() {
        this.insert_logo_text = (TextView) findViewById(R.id.insert_logo_text);
        this.insert_logo_text.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.insert_logo_text) {
            startActivity(new Intent(this, PhotoUploadActivity.class));
        }
    }
}
