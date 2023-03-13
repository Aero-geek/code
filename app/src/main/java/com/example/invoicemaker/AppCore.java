package com.example.invoicemaker;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.example.invoicemaker.Database.DatabaseHelper;
import com.example.invoicemaker.Database.DatabaseManager;
import com.example.invoicemaker.Database.LoadDatabase;
import com.example.invoicemaker.Dto.SettingsDTO;
import com.example.invoicemaker.ads.AdsConstant;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AppCore extends Application {

    public static InterstitialAd admobInterstitialAd = null;
    private static AppCore mInstance;

    public static AppCore getInstance() {
        return mInstance;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void loadAdmobInterstitial(Context context) {
        if (AppCore.admobInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, AdsConstant.ADMOB_INTERSTITIAL, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    AppCore.admobInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    AppCore.admobInterstitialAd = null;
                }
            });
        }
    }

    public void onCreate() {
        mInstance = this;
        super.onCreate();
        init();

        AdsConstant.isAvailable = getResources().getBoolean(R.bool.isAdAvailable);

        AdsConstant.ADMOB_BANNER = getString(R.string.admobBanner);
        AdsConstant.ADMOB_NATIVE = getString(R.string.admobNative);
        AdsConstant.ADMOB_INTERSTITIAL = getString(R.string.admobInterstitial);
        AdsConstant.ADMOB_APP_OPEN = getString(R.string.admobAppOpen);

        MobileAds.initialize(this, initializationStatus -> {
        });
        loadAdmobInterstitial(this);
    }

    public void init() {
        DatabaseManager.initializeInstance(new DatabaseHelper(getApplicationContext()));
        SettingsDTO.setSettingsDTO(LoadDatabase.getInstance().getSettings());
        LoadDatabase.getInstance().viewData();
    }
}
