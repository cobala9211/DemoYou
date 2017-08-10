package com.songskids.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.songskids.R;
import com.songskids.adapter.DetailAdapter;
import com.songskids.adapter.HomeAdapter;
import com.songskids.models.Songs;
import com.songskids.network.apis.AuthApi;
import com.songskids.network.core.Callback;
import com.songskids.utils.AddViewUtils;
import com.songskids.utils.ConnectionUtil;
import com.songskids.utils.Utils;
import com.songskids.wiget.VideoControllerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment implements HomeAdapter.OnAdapterListener, VideoControllerView.MediaPlayerControl {

    private HomeAdapter mAdapter;
    private DetailAdapter mAdapterDetail;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Songs> mDatas = new ArrayList<>();

    @ViewById(R.id.flMainUi)
    FrameLayout mFlMainUi;
    @ViewById(R.id.rlVideoview)
    RelativeLayout mFrameLayoutVideoPlayerContainer;
    @ViewById(R.id.videoSurface)
    VideoView mVideoView;
    @ViewById(R.id.recyclerViewHome)
    RecyclerView mRecyclerViewHome;
    @ViewById(R.id.recyclerViewDetail)
    RecyclerView mRecyclerViewDetail;
    @ViewById(R.id.progressBarLoading)
    ProgressBar mProgressBarLoading;
    @ViewById(R.id.progressBarLoadVideo)
    ProgressBar mProgressBarLoadVideo;

    @ViewById(R.id.rlError)
    RelativeLayout mRlError;

    @ViewById(R.id.draggable_view)
    DraggableView mDraggableView;

    @ViewById(R.id.flAdViewTmp)
    FrameLayout mFlAdViewTmp;
    @ViewById(R.id.flAdView)
    FrameLayout mFlAdView;


    private String mIdSelect;
    private String mNameSelect;
    private String mYoutubeidSelect;

    private VideoControllerView mController;
    private String mUrlPlay;
    private Handler mHandlerLocal = new Handler(Looper.getMainLooper());

    @AfterViews
    void init() {
        Utils.setupUI(mFlMainUi, mContext);
        AddViewUtils.loadAdView(mContext, mFlAdViewTmp);
        AddViewUtils.loadAdView(mContext, mFlAdView);
        // list background
        mAdapter = new HomeAdapter(mContext, mDatas, HomeAdapter.TYPE_HOME, this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerViewHome.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewHome.setHasFixedSize(true);
        mRecyclerViewHome.setAdapter(mAdapter);
        //listDetail
        mAdapterDetail = new DetailAdapter(mContext, mDatas, this);
        LinearLayoutManager linearLayoutManagerDetail = new LinearLayoutManager(mContext);
        mRecyclerViewDetail.setLayoutManager(linearLayoutManagerDetail);
        mRecyclerViewDetail.setHasFixedSize(true);
        mRecyclerViewDetail.setAdapter(mAdapterDetail);
        mRecyclerViewHome.setOnScrollListener(onScrollListener);
        checkNetWork();
        initializeDraggableView();
        hookDraggableViewListener();
        initMedia();


    }

    private void checkNetWork() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
        mRlError.setVisibility(View.GONE);
        if (!ConnectionUtil.isConnected(mContext)) {
            mRlError.setVisibility(View.VISIBLE);
            mRecyclerViewHome.setVisibility(View.GONE);
            mProgressBarLoading.setVisibility(View.GONE);
            mDraggableView.setVisibility(View.GONE);
        } else {
            mRlError.setVisibility(View.GONE);
            mProgressBarLoading.setVisibility(View.VISIBLE);
            mRecyclerViewHome.setVisibility(View.VISIBLE);
            loadData("0");
        }
    }

    private void loadData(final String id) {
        AuthApi.getSongListPage(id, new Callback<List<Songs>>() {
            @Override
            public void success(List<Songs> datas) {
                if (datas.size() != 0) {
                    mDatas.addAll(datas);
                    mAdapter.notifyDataSetChanged();
                }
                mProgressBarLoading.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error, com.songskids.network.Error myError) {
                mProgressBarLoading.setVisibility(View.GONE);
                if (id.equals("0")) {
                    mRlError.setVisibility(View.VISIBLE);
                    mRecyclerViewHome.setVisibility(View.GONE);
                    mProgressBarLoading.setVisibility(View.GONE);
                    mDraggableView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Click(R.id.imgReplay)
    void replay() {
        checkNetWork();
    }

    private void initMedia() {
        mController = new VideoControllerView(mContext, false);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                showInterstitial();
                mDraggableView.minimize();
            }
        });

        mVideoView.setOnPreparedListener(mOnPreparedListener);
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mController.setMediaPlayer(HomeFragment.this);
            mController.setAnchorView(mFrameLayoutVideoPlayerContainer);
            mVideoView.setBackgroundColor(getResources().getColor(R.color.transparent));
            mController.setNameVideo(mNameSelect);
            mProgressBarLoadVideo.setVisibility(View.GONE);
            if (mDataFavorites.size() != 0) {
                for (int i = 0; i < mDataFavorites.size(); i++) {
                    if (mIdSelect.equals(mDataFavorites.get(i).getId())) {
                        mController.setIsFavorite(true);
                        break;
                    } else {
                        mController.setIsFavorite(false);
                    }
                }
            } else {
                mController.setIsFavorite(false);
            }
            mController.updateFavorite();
            mVideoView.start();
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        int firstVisibleItem;
        int visibleItemCount;
        int totalItemCount;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = mRecyclerViewHome.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem)) {
                loadData(mDatas.get(mDatas.size() - 1).getId());
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    /**
     * Initialize DraggableView.
     */
    private void initializeDraggableView() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDraggableView.setVisibility(View.GONE);
                mDraggableView.closeToRight();
            }
        }, DELAY_MILLIS);
    }

    /**
     * Hook DraggableListener to draggableView to pause or resume VideoView.
     */
    private void hookDraggableViewListener() {
        mDraggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                if (mOnBaseFragmentListener != null) mOnBaseFragmentListener.hideHeaderBar();
            }

            //Empty
            @Override
            public void onMinimized() {
                mController.hide();
                if (mOnBaseFragmentListener != null) mOnBaseFragmentListener.showHeaderBar();
            }

            @Override
            public void onClosedToLeft() {
                if (mOnBaseFragmentListener != null) mOnBaseFragmentListener.showHeaderBar();
            }

            @Override
            public void onClosedToRight() {
                if (mOnBaseFragmentListener != null) mOnBaseFragmentListener.showHeaderBar();
            }

            @Override
            public void onTouchDraggableView() {
                mController.show();
            }
        });
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
        return mVideoView.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mVideoView.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public void pause() {
        mVideoView.pause();
    }

    @Override
    public void seekTo(int i) {
        mVideoView.seekTo(i);
    }

    @Override
    public void start() {
        mVideoView.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
        mVideoView.pause();
        int positionVideo = mVideoView.getCurrentPosition();
        if (mOnBaseFragmentListener != null) {
            Songs songs = new Songs();
            songs.setYoutubeid(mYoutubeidSelect);
            songs.setNames(mNameSelect);
            songs.setId(mIdSelect);
            mOnBaseFragmentListener.fullScreen(positionVideo, mUrlPlay, songs, mController.isFavorite());
        }
    }

    @Override
    public void setFavorites(ImageButton button, boolean isFavorites) {
        if (isFavorites) {
            long result = mDbManager.remove(mIdSelect);
            if (result > 0) {
                mController.setIsFavorite(false);
                button.setImageResource(R.drawable.ic_stars_white_24dp);
            }
        } else {
            Songs songs = new Songs();
            songs.setYoutubeid(mYoutubeidSelect);
            songs.setNames(mNameSelect);
            songs.setId(mIdSelect);
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.youtube_link) + mYoutubeidSelect);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name) + ":)"));
    }

    public void OnResumeVideo(int position) {
        if (mVideoView != null) {
            mVideoView.seekTo(position);
            mVideoView.start();
        }
    }


    @Override
    public void OnItemClick(String id, String name, String youtubeid) {
        mRecyclerViewDetail.getLayoutManager().scrollToPosition(0);
        mIdSelect = id;
        mNameSelect = name;
        mYoutubeidSelect = youtubeid;
        mVideoView.pause();
        mVideoView.setBackgroundColor(getResources().getColor(R.color.black));
        mController.hide();
        convertUrl();
    }

    private void convertUrl() {
        mAdapterDetail.setTitleHeader(mNameSelect);
        mAdapterDetail.notifyDataSetChanged();
        mDraggableView.setVisibility(View.VISIBLE);
        mDraggableView.maximize();
        mProgressBarLoadVideo.setVisibility(View.VISIBLE);
        Utils.convertUrlYoutube(BASE_URL + mYoutubeidSelect, new Utils.OnConvertListener() {
            @Override
            public void onResultConvert(final String urlVideo) {
                Runnable runnableLocal = new Runnable() {
                    @Override
                    public void run() {
                        mDraggableView.setVisibility(View.VISIBLE);
                        mDraggableView.maximize();
                        mUrlPlay = urlVideo;
                        Uri video = Uri.parse(urlVideo);
                        mVideoView.setVideoURI(video);
                    }
                };
                mHandlerLocal.postDelayed(runnableLocal, 100);
            }
        });
    }

    public void onBackPressed() {
        if (mDraggableView.isShown()) {
            if (mDraggableView.isMaximized()) {
                mDraggableView.minimize();
            } else {
                mContext.finish();
            }
        } else {
            mContext.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AddViewUtils.loadAdView(mContext, mFlAdViewTmp);
        AddViewUtils.loadAdView(mContext, mFlAdView);
    }

    public void onSearchChange(String text) {
        mAdapter.getFilter().filter(text);
    }
}
