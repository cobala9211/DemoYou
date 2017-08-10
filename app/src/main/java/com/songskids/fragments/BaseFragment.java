package com.songskids.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.songskids.R;
import com.songskids.database.Database;
import com.songskids.models.Songs;
import com.songskids.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {
    protected int DELAY_MILLIS = 10;
    protected FragmentActivity mContext;
    protected OnBaseFragmentListener mOnBaseFragmentListener;
    protected Database mDbManager;
    protected List<Songs> mDataFavorites = new ArrayList<>();

    protected String BASE_URL = "http://keepvid.com/?url=https://www.youtube.com/watch?v=";

    protected InterstitialAd mInterstitialAd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnBaseFragmentListener = (OnBaseFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBaseFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        startGame();
        mDbManager = new Database(mContext);
        Utils.getData(mDbManager, new Utils.OnDatabaseListenner() {
            @Override
            public void onResult(List<Songs> result) {
                if (result.size() != 0) mDataFavorites.addAll(result);
            }
        });


    }

    public interface OnBaseFragmentListener{
        void fullScreen(int positionVideo, String url, Songs songs, boolean isFavorites);
        void showHeaderBar();
        void hideHeaderBar();
    }

    protected void showInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            startGame();
        }
    }

    private void startGame() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}