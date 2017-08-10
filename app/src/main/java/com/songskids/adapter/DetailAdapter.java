package com.songskids.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songskids.R;
import com.songskids.models.Songs;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLlMainDetail;
        private TextView mTvTitleDetail;
        private ImageView mIvCoverDetail;

        ViewHolder(View itemView) {
            super(itemView);
            mLlMainDetail = (LinearLayout) itemView.findViewById(R.id.llMainDetail);
            mTvTitleDetail = (TextView) itemView.findViewById(R.id.tvTitleDetail);
            mIvCoverDetail = (ImageView) itemView.findViewById(R.id.imgCoverDetail);
        }
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder {
        private TextView mTvTitleHeaderDetail;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            mTvTitleHeaderDetail = (TextView) itemView.findViewById(R.id.tvTitleHeaderDetail);
        }
    }

    private enum TypeView {
        TYPE_HEADER(1), TYPE_ITEM(2);

        private final int value;

        TypeView(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private List<Songs> mListItem;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private HomeAdapter.OnAdapterListener mListener;

    public String mTitleHeader;

    public DetailAdapter(Context context, List<Songs> persons, HomeAdapter.OnAdapterListener listener) {
        this.mListItem = persons;
        mListener = listener;

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    public String getTitleHeader() {
        return mTitleHeader;
    }

    public void setTitleHeader(String mTitleHeader) {
        this.mTitleHeader = mTitleHeader;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (type == TypeView.TYPE_ITEM.getValue()) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_detail, viewGroup, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_detail, viewGroup, false);
            return new ViewHolderHeader(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        if (holder instanceof ViewHolder) {
            final Songs item = mListItem.get(pos - 1);
            final ViewHolder vhItem = (ViewHolder) holder;
            vhItem.mTvTitleDetail.setText(item.getNames());
            String urlCover = "http://img.youtube.com/vi/" + item.getYoutubeid() + "/0.jpg";
            mImageLoader.displayImage(urlCover, vhItem.mIvCoverDetail, mOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    vhItem.mIvCoverDetail.setImageBitmap(loadedImage);


                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });

            vhItem.mLlMainDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.OnItemClick(item.getId(), item.getNames(), item.getYoutubeid());
                    }
                }
            });
        } else {
            ViewHolderHeader vhHeader = (ViewHolderHeader) holder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setFullSpan(true);
            vhHeader.itemView.setLayoutParams(layoutParams);
            vhHeader.mTvTitleHeaderDetail.setText(getTitleHeader());

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TypeView.TYPE_HEADER.getValue();
        return TypeView.TYPE_ITEM.getValue();
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        if (mListItem.isEmpty()) {
            return mListItem.size();
        } else {
            return mListItem.size() + 1;
        }
    }
}