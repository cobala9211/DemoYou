package com.songskids.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.songskids.R;
import com.songskids.adapter.DrawerAdapter;
import com.songskids.models.NavDrawerItem;
import com.songskids.utils.AddViewUtils;
import com.songskids.view.LollipopDrawablesCompat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_navigation_drawer)
public class DrawerFragment extends Fragment {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mContainerView;
    private String[] mTitles = null;
    private TypedArray mDrawablesNormal = null;
    private TypedArray mDrawablesSelect = null;
    private FragmentDrawerListener mDrawerListener;

    private LinearLayout mLlChildMainDrawer;
    private ImageView mImgChildDrawer;
    private TextView mTvChildDrawer;
    private int mPositionSelect;
    private boolean isShowItemSelectedDefault;
    private View mViewItemSelectedDefaut;

    @ViewById(R.id.lvDrawerList)
    ListView mLvDrawerList;
    @ViewById(R.id.tvTitleApp)
    TextView mTvTitleApp;
    @ViewById(R.id.tvVersionApp)
    TextView mTvVersionApp;
    @ViewById(R.id.flAdView)
    FrameLayout mFlAdView;

    @Bean
    DrawerAdapter adapter;

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.mDrawerListener = listener;
    }

    private List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();
        for (int i = 0; i < mDrawablesNormal.length(); i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(mTitles[i]);
            navItem.setIcon(mDrawablesNormal.getResourceId(i, -1));
            data.add(navItem);
        }
        return data;
    }

    @AfterViews
    void init() {
        mTitles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        mDrawablesNormal = getResources().obtainTypedArray(R.array.drawer_icon);
        mDrawablesSelect = getResources().obtainTypedArray(R.array.drawer_icon_select);

        adapter.setAdapter(getData());
        mLvDrawerList.setSelector(getDrawable2(R.drawable.list_selector));
        mLvDrawerList.setAdapter(adapter);
        mLvDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mViewItemSelectedDefaut.setBackgroundColor(getResources().getColor(R.color.transparent));
                ImageView ivNavIcon = (ImageView) mViewItemSelectedDefaut.findViewById(R.id.imgDrawer);
                TextView tvTitle = (TextView) mViewItemSelectedDefaut.findViewById(R.id.tvTitleDrawer);
                ivNavIcon.setImageResource(mDrawablesNormal.getResourceId(0, -1));
                tvTitle.setTextColor(getResources().getColor(R.color.gray));
                setDrawer(view, position);
            }
        });

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/ufonts.com_matura_mt_script_capitals.ttf");
        mTvTitleApp.setText(getActivity().getResources().getString(R.string.app_name));
        mTvTitleApp.setTypeface(tf);

        PackageInfo pinfo;
        try {
            pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            mTvVersionApp.setText(getResources().getString(R.string.version) + " " + pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setViewItemSelectedDefaut();
//        AddViewUtils.refreshAd(getActivity(), mFlAdView);
    }

    /**
     * Set color for child view of recyclerview at position 0 to make view default
     */
    private void setViewItemSelectedDefaut() {
        mLvDrawerList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isShowItemSelectedDefault) {
                    mViewItemSelectedDefaut = mLvDrawerList.getChildAt(0);
                    mViewItemSelectedDefaut.setBackgroundColor(getResources().getColor(R.color.opacity_button));
                    ImageView ivNavIcon = (ImageView) mViewItemSelectedDefaut.findViewById(R.id.imgDrawer);
                    TextView tvTitle = (TextView) mViewItemSelectedDefaut.findViewById(R.id.tvTitleDrawer);
                    ivNavIcon.setImageResource(mDrawablesSelect.getResourceId(0, -1));
                    tvTitle.setTextColor(getResources().getColor(R.color.pink_500));
                    isShowItemSelectedDefault = true;
                }
            }
        });
    }

    private void setDrawer(View view, int position) {
        if (null != mLlChildMainDrawer && null != mImgChildDrawer) {
            mImgChildDrawer.setImageResource(mDrawablesNormal.getResourceId(mPositionSelect, -1));
            mTvChildDrawer.setTextColor(getResources().getColor(R.color.gray));
            mLlChildMainDrawer.setSelected(false);
        }

        ImageView icon = (ImageView) view.findViewById(R.id.imgDrawer);
        TextView textView = (TextView) view.findViewById(R.id.tvTitleDrawer);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llMainDrawer);

        mPositionSelect = position;
        mImgChildDrawer = icon;
        mTvChildDrawer = textView;
        mLlChildMainDrawer = linearLayout;
        icon.setImageResource(mDrawablesSelect.getResourceId(mPositionSelect, -1));
        textView.setTextColor(getResources().getColor(R.color.pink_500));
        mLlChildMainDrawer.setSelected(true);

        if (mDrawerListener != null) mDrawerListener.onDrawerItemSelected(view, position);
        if (position < 2) mDrawerLayout.closeDrawer(mContainerView);
    }

    /**
     * {@link #(int)} is already taken by Android API
     * and method is final, so we need to give another name :(
     */
    public Drawable getDrawable2(int id) {
        return LollipopDrawablesCompat.getDrawable(getResources(), id, getActivity().getTheme());
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }
}
