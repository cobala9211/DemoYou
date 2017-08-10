package com.songskids.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.songskids.R;

import java.util.List;

public class AddViewUtils {

    //    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-8678171700287005/5854851176";
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-8678171700287005/4980756773";

    public static void loadAdView(Context context, final FrameLayout view) {
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.SMART_BANNER);
        view.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                view.setVisibility(View.GONE);
            }
        });

    }


    /**
     * Populates a {@link NativeAppInstallAdView} object with data from a given
     * {@link NativeAppInstallAd}.
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     * @param adView             the view to be populated
     */
    public static void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                                NativeAppInstallAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setImageView(adView.findViewById(R.id.appinstall_image));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());
        ((RatingBar) adView.getStarRatingView())
                .setRating(nativeAppInstallAd.getStarRating().floatValue());

        List<NativeAd.Image> images = nativeAppInstallAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    /**
     * Populates a {@link NativeContentAdView} object with data from a given
     * {@link NativeContentAd}.
     *
     * @param nativeContentAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    public static void populateContentAdView(NativeContentAd nativeContentAd,
                                             NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());
        List<NativeAd.Image> images = nativeContentAd.getImages();
        if (images != null && images.size() > 0) {
            ((ImageView) adView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }
        NativeAd.Image logoImage = nativeContentAd.getLogo();
        if (logoImage != null) {
            ((ImageView) adView.getLogoView())
                    .setImageDrawable(logoImage.getDrawable());
        }
        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    public static void refreshAd(final Activity activity, final FrameLayout view) {
        AdLoader.Builder builder = new AdLoader.Builder(activity, ADMOB_AD_UNIT_ID);
        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                LayoutInflater inflater = activity.getLayoutInflater();
                NativeAppInstallAdView adView = (NativeAppInstallAdView) inflater.inflate(R.layout.ad_app_install, null);
                populateAppInstallAdView(ad, adView);
                view.removeAllViews();
                view.addView(adView);
            }
        });

        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd ad) {
                LayoutInflater inflater = activity.getLayoutInflater();
                NativeContentAdView adView = (NativeContentAdView) inflater.inflate(R.layout.ad_content, null);
                populateContentAdView(ad, adView);
                view.removeAllViews();
                view.addView(adView);
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                view.setVisibility(View.GONE);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
