package com.songskids;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.songskids.database.Database;
import com.songskids.models.Songs;
import com.songskids.wiget.VideoControllerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_detail)
public class DetailActivity extends Activity implements VideoControllerView.MediaPlayerControl {

    private Database mDbManager;

    @ViewById(R.id.framelayoutDetail)
    FrameLayout mFrameLayoutVideoPlayerContainer;
    @ViewById(R.id.videoviewDetail)
    VideoView mVideoViewDetail;
    @ViewById(R.id.progressBarLoadDetail)
    ProgressBar mProgressBarLoadDetail;

    VideoControllerView mController;

    @Extra
    protected int mPositionStart;
    @Extra
    protected String mUrlPlay;
    @Extra
    protected Songs mSongs;
    @Extra
    protected boolean mIsFavorite;

    private InterstitialAd mInterstitialAd;

    @AfterViews
    void init() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        startGame();

        if (mDbManager == null) mDbManager = new Database(this);
        initMedia();
        mProgressBarLoadDetail.setVisibility(View.VISIBLE);
        Uri video = Uri.parse(mUrlPlay);
        mVideoViewDetail.setVideoURI(video);
    }

    private void initMedia() {
        mController = new VideoControllerView(this, false);
        mVideoViewDetail.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                showInterstitial();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", mVideoViewDetail.getCurrentPosition());
                setResult(2, intent);
                finish();
            }
        });

        mVideoViewDetail.setOnPreparedListener(mOnPreparedListener);
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mController.setMediaPlayer(DetailActivity.this);
            mController.setAnchorView(mFrameLayoutVideoPlayerContainer);
            mController.setNameVideo(mSongs.getNames());
            mController.setIsFavorite(mIsFavorite);
            mController.updateFavorite();
            mProgressBarLoadDetail.setVisibility(View.GONE);
            mVideoViewDetail.seekTo(mPositionStart);
            mVideoViewDetail.start();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return false;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return mVideoViewDetail.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mVideoViewDetail.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mVideoViewDetail.isPlaying();
    }

    @Override
    public void pause() {
        mVideoViewDetail.pause();
    }

    @Override
    public void seekTo(int i) {
        mVideoViewDetail.seekTo(i);
    }

    @Override
    public void start() {
        mVideoViewDetail.start();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public void setFavorites(ImageButton button, boolean isFavorites) {
        if (isFavorites) {
            long result = mDbManager.remove(mSongs.getId());
            if (result > 0) {
                mController.setIsFavorite(false);
                button.setImageResource(R.drawable.ic_stars_white_24dp);
            }
        } else {
            Songs songs = new Songs();
            songs.setYoutubeid(mSongs.getYoutubeid());
            songs.setNames(mSongs.getNames());
            songs.setId(mSongs.getId());
            long result = mDbManager.insert(songs);
            if (result > 0) {
                mController.setIsFavorite(true);
                button.setImageResource(R.drawable.ic_stars_pink_500_24dp);
            }
        }
    }

    @Override
    public void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mSongs.getYoutubeid());
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name) + ":)"));
    }

    @Override
    public void toggleFullScreen() {
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", mVideoViewDetail.getCurrentPosition());
        setResult(2, intent);
        finish();
    }

    private void showInterstitial() {
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
