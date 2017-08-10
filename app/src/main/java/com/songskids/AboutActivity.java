package com.songskids;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.songskids.utils.AddViewUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_about)
public class AboutActivity extends Activity {

    @ViewById(R.id.tvVersion)
    TextView mTvVersion;

    @ViewById(R.id.flAdView)
    FrameLayout mFlAdView;

    private InterstitialAd mInterstitialAd;
    private CountDownTimer mCountDownTimer;

    @AfterViews
    void init() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        AddViewUtils.loadAdView(this, mFlAdView);
        PackageInfo pinfo;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTvVersion.setText(getResources().getString(R.string.version) + " " + pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initTimer();
        startGame();

    }

    @Click(R.id.imgBackabout)
    void back() {
        finish();
    }

    @Click(R.id.tvRate)
    void clickRateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Click(R.id.tvMoreApp)
    void clickMoreApp() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=Haputech");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Haputech")));
        }
    }

    private void initTimer() {
        mCountDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUnitFinished) {
            }

            @Override
            public void onFinish() {
                showInterstitial();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        AddViewUtils.loadAdView(this, mFlAdView);
    }

    private void showInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mCountDownTimer.cancel();
        } else {
            startGame();
        }
    }

    private void startGame() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mCountDownTimer.start();
    }
}
