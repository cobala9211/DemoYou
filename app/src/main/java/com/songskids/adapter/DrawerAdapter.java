package com.songskids.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.songskids.models.NavDrawerItem;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

@EBean
public class DrawerAdapter extends BaseAdapter {

    private List<NavDrawerItem> mDatas;
    @RootContext
    Context context;

    public void setAdapter(List<NavDrawerItem> datas) {
        mDatas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemDrawer itemList;
        if (convertView == null) {
            itemList = ItemDrawer_.build(context);
        } else {
            itemList = (ItemDrawer) convertView;
        }
        itemList.bin(getItem(position));

        return itemList;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public NavDrawerItem getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
