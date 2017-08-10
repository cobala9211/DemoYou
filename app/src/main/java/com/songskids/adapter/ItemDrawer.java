package com.songskids.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songskids.R;
import com.songskids.models.NavDrawerItem;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.item_drawer)
public class ItemDrawer extends RelativeLayout {

    @ViewById(R.id.imgDrawer)
    ImageView mImgCover;
    @ViewById(R.id.tvTitleDrawer)
    TextView mTvTitleDrawer;

    private Context mContext;

    public ItemDrawer(Context context) {
        super(context);
        mContext = context;
    }

    public void bin(NavDrawerItem item) {
        mImgCover.setImageResource(item.getIcon());
        mTvTitleDrawer.setText(item.getTitle());
    }
}
