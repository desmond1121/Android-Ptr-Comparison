package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.android.support.SwipeRefreshLayout;
import com.desmond.ptrcomarison.R;

/**
 * Created by desmond on 2015/12/14.
 */
public class SwipeFragment extends AbstractFragment{
    private static final String TAG = "SwipeFragment";
    private SwipeRefreshLayout mSwipe;

    @Override
    public String getTitle() {
        return "Swipe Refresh";
    }

    @Override
    protected ListView getListView() {
        return (ListView) mLayout.findViewById(R.id.list_view);
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_swipe_refresh;
    }

    @Override
    protected void init() {
        mSwipe = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipe_refresh);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        super.init();
    }

    @Override
    protected void doRefresh() {
        mAdapter.nextPage();
    }

    @Override
    protected void resetPtr() {
        mSwipe.setRefreshing(false);
    }
}
