package com.songskids;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.songskids.fragments.BaseFragment;
import com.songskids.fragments.DrawerFragment;
import com.songskids.fragments.DrawerFragment_;
import com.songskids.fragments.FavoriteFragment_;
import com.songskids.fragments.HomeFragment_;
import com.songskids.models.Songs;
import com.songskids.utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements DrawerFragment.FragmentDrawerListener, BaseFragment.OnBaseFragmentListener {

    private static final int ACTIVITY_REQUEST_CODE = 1;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.rlToolbarSearch)
    RelativeLayout mRlToolbarSearch;
    @ViewById(R.id.edtSearch)
    EditText mEdtSearch;

    private Fragment mFragment = null;
    private Menu mMenu;

    @AfterViews
    void init() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerFragment_ drawerFragment = (DrawerFragment_)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);

        //init edittext search
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mFragment instanceof HomeFragment_) {
                    ((HomeFragment_) mFragment).onSearchChange(charSequence.toString());
                }
                if (mFragment instanceof FavoriteFragment_) {
                    ((FavoriteFragment_) mFragment).onSearchChange(charSequence.toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                mFragment = new HomeFragment_();
                title = getString(R.string.news);
                break;
            case 1:
                mFragment = new FavoriteFragment_();
                title = getString(R.string.favorites);
                break;
            case 2:
                title = getString(R.string.rate_review);
                AboutActivity_.intent(this).start();
                break;
            default:
                break;
        }

        if (mFragment != null) {
            replaceFragment(mFragment, false);
            // set the toolbar title
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
        } else {
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        } else {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.replace(R.id.container_body, fragment);
        transaction.commit();
    }

    @Override
    public void fullScreen(int positionVideo, String url, Songs songs, boolean isFavorites) {
        DetailActivity_.intent(this).mPositionStart(positionVideo).mUrlPlay(url).mSongs(songs).mIsFavorite(isFavorites).startForResult(ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void showHeaderBar() {
        if (getSupportActionBar() != null) getSupportActionBar().show();
    }

    @Override
    public void hideHeaderBar() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        if (mFragment instanceof HomeFragment_) {
            ((HomeFragment_) mFragment).onBackPressed();
        } else {
            ((FavoriteFragment_) mFragment).onBackPressed();
        }
    }

    @OnActivityResult(ACTIVITY_REQUEST_CODE)
    protected void onActivityResult(int resultCode, Intent data) {
        if (resultCode == 2) {
            int positionVideo = data.getIntExtra("MESSAGE", 0);
            if (mFragment instanceof HomeFragment_) {
                ((HomeFragment_) mFragment).OnResumeVideo(positionVideo);
            } else {
                ((FavoriteFragment_) mFragment).OnResumeVideo(positionVideo);
            }
        }
    }

    @Click(R.id.imgClearSearch)
    void clearSearch() {
        mEdtSearch.setText("");
        mRlToolbarSearch.setVisibility(View.GONE);
        mMenu.findItem(R.id.action_search).setVisible(true);
        Utils.hideSoftKeyboard(this);
        if (mFragment instanceof HomeFragment_) {
            ((HomeFragment_) mFragment).onSearchChange("");
        }
        if (mFragment instanceof FavoriteFragment_) {
            ((FavoriteFragment_) mFragment).onSearchChange("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mMenu.findItem(R.id.action_search).setVisible(false);
                mRlToolbarSearch.setVisibility(View.VISIBLE);
                Utils.showKeyboard(this, mEdtSearch);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
