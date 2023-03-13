package com.example.invoicemaker.ads;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.invoicemaker.AppCore;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;

public class AdsProvider {

    public static AdsProvider provider = new AdsProvider();
    private AdsListener listener = null;

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true; // connected to wifi
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;// connected to the mobile provider's data plan
            }
        }
        return false;
    }

    private void onPress() {
        if (listener != null) {
            listener.onPress();
        }
    }

    public void addBanner(Context context, View customView) {
        if (AdsConstant.isAvailable && checkConnection(context) && AdsConstant.ADMOB_BANNER != null && !AdsConstant.ADMOB_BANNER.equals("") && customView != null) {
            AdView mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(AdsConstant.ADMOB_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            if (customView instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) customView;
                layout.removeAllViews();
                layout.addView(mAdView);
            } else if (customView instanceof RelativeLayout) {
                RelativeLayout layout = (RelativeLayout) customView;
                layout.removeAllViews();
                layout.addView(mAdView);
            } else if (customView instanceof FrameLayout) {
                FrameLayout layout = (FrameLayout) customView;
                layout.removeAllViews();
                layout.addView(mAdView);
            }

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // TODO Auto-generated method stub
                    customView.setVisibility(View.VISIBLE);
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }
            });

        }
    }

    public void showInterstitial(Activity activity, AdsListener adsListener) {
        this.listener = adsListener;

        if (AdsConstant.isAvailable && checkConnection(activity) && AdsConstant.ADMOB_INTERSTITIAL != null && !AdsConstant.ADMOB_INTERSTITIAL.equals("")) {

            AppCore.admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    AppCore.admobInterstitialAd = null;
                    AppCore.loadAdmobInterstitial(activity);
                    onPress();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    AppCore.admobInterstitialAd = null;
                    AppCore.loadAdmobInterstitial(activity);
                    onPress();
                }
            });


            AppCore.admobInterstitialAd.show(activity);
        } else {
            onPress();
        }
    }

}
