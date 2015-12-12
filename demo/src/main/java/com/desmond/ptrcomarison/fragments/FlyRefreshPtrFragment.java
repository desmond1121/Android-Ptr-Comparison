package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.desmond.ptrcomarison.R;
import com.race604.flyrefresh.FlyRefreshLayout;

/**
 * Ptr Fragment using {@link "https://github.com/race604/FlyRefresh"}
 * <p>
 * Created by desmond on 2015/12/9.
 */
public class FlyRefreshPtrFragment extends AbstractFragment {
    private FlyRefreshLayout mPtr;
    private ListView mListView;

    @Override
    public String getTitle() {
        return "FlyRefresh";
    }

    @Override
    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) mLayout.findViewById(R.id.race_list_view);
        }
        return mListView;
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_race604;
    }

    @Override
    protected void init() {
        if (mPtr == null) {
            mPtr = (FlyRefreshLayout) mLayout.findViewById(R.id.fly_layout);
            mPtr.setOnPullRefreshListener(new FlyRefreshLayout.OnPullRefreshListener() {
                @Override
                public void onRefresh(FlyRefreshLayout view) {
                    refresh();
                }

                @Override
                public void onRefreshAnimationEnd(FlyRefreshLayout view) {

                }
            });
        }

        super.init();
    }

    @Override
    protected void doRefresh() {
        mAdapter.nextPage();
    }

    @Override
    protected void resetPtr() {
        mPtr.onRefreshFinish();
    }
}
