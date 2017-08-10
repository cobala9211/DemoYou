package com.songskids.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.songskids.R;
import com.songskids.models.Songs;
import com.songskids.utils.ScreenUtil;
import com.songskids.wiget.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mTvName;
        private SelectableRoundedImageView mIvCover;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mTvName = (TextView) itemView.findViewById(R.id.tvTitle);
            mIvCover = (SelectableRoundedImageView) itemView.findViewById(R.id.imgCover);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mCardView.setPreventCornerOverlap(false);
            }
        }
    }

    private class ViewHolderFooter extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBarLoad;

        ViewHolderFooter(View itemView) {
            super(itemView);
            mProgressBarLoad = (ProgressBar) itemView.findViewById(R.id.progressBarLoad);
        }
    }

    public static int TYPE_HOME = 0;
    public static int TYPE_FAVORITES = 1;

    private enum TypeView {
        TYPE_ITEM(1), TYPE_FOOTER(2);

        private final int value;

        TypeView(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private List<Songs> mListItem;
    private ArrayFilter mFilter;
    private List<Songs> mArrayListOld;
    private final Object mLock = new Object();
    private OnAdapterListener mListener;
    private int mWidthItem;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int mType;


    public HomeAdapter(Context context, List<Songs> persons, int type, OnAdapterListener listener) {
        this.mListItem = persons;
        this.mArrayListOld = persons;
        mListener = listener;
        mType = type;
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        mWidthItem = ScreenUtil.getWidthScreen(context) - (int) context.getResources().getDimension(R.dimen.layout_20);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (type == TypeView.TYPE_ITEM.getValue()) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer, viewGroup, false);
            return new ViewHolderFooter(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        if (holder instanceof ViewHolder) {
            final Songs item = mListItem.get(pos);
            final ViewHolder vhItem = (ViewHolder) holder;
            vhItem.mTvName.setText(item.getNames());
            String urlCover = "http://img.youtube.com/vi/" + item.getYoutubeid() + "/0.jpg";
            mImageLoader.displayImage(urlCover, vhItem.mIvCover, mOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    int widthBmp = loadedImage.getWidth();
                    int heightBmp = loadedImage.getHeight();
                    vhItem.mIvCover.getLayoutParams().height = heightBmp * mWidthItem / widthBmp;
                    vhItem.mIvCover.setImageBitmap(loadedImage);


                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });


            vhItem.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.OnItemClick(item.getId(), item.getNames(), item.getYoutubeid());
                    }
                }
            });

        } else {
            ViewHolderFooter vhFooter = (ViewHolderFooter) holder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setFullSpan(true);
            vhFooter.itemView.setLayoutParams(layoutParams);
            if (mType == TYPE_HOME) {
                vhFooter.mProgressBarLoad.setVisibility(View.VISIBLE);
            } else {
                vhFooter.mProgressBarLoad.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position))
            return TypeView.TYPE_FOOTER.getValue();
        return TypeView.TYPE_ITEM.getValue();
    }

    public boolean isPositionFooter(int position) {
        return position == mListItem.size();
    }

    @Override
    public int getItemCount() {
        if (mListItem.isEmpty()) {
            return mListItem.size();
        } else {
            return mListItem.size() + 1;
        }
    }

    public interface OnAdapterListener {
        void OnItemClick(String id, String name, String youtubeid);
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

//            if (mArrayListSearch == null) {
//                synchronized (mLock) {
//                    mArrayListSearch = new ArrayList<>();
//                }
//            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    results.values = mArrayListOld;
                    results.count = mArrayListOld.size();
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();
                final int count = mArrayListOld.size();

                final ArrayList<Songs> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    final Songs value = mArrayListOld.get(i);
                    String name = "" + value.getNames();
                    if (name.toLowerCase().trim()
                            .contains(prefixString)) {
                        newValues.add(value);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // noinspection unchecked
            mListItem = (ArrayList<Songs>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
        }
    }

}