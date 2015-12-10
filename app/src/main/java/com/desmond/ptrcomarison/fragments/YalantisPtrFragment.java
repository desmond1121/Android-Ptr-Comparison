package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.desmond.ptrcomarison.R;
import com.yalantis.phoenix.PullToRefreshView;

/**
 * Ptr Fragment using {@link "https://github.com/Yalantis/Phoenix"}
 * <p/>
 * Created by desmond on 2015/12/9.
 */
public class YalantisPtrFragment extends AbstractFragment{
    private PullToRefreshView mPtr;
    private ListView mListView;

    @Override
    public String getTitle() {
        return "Yalantis";
    }

    @Override
    protected ListView getListView() {
        if(mPtr != null){
            mListView = (ListView) mPtr.findViewById(R.id.yalantis_list_view);
        }
        return mListView;
    }

    @Override
    protected void init() {
        mPtr = (PullToRefreshView) mLayout.findViewById(R.id.pull_to_refresh);
        mPtr.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        super.init();
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_yalantis;
    }

    @Override
    protected void doRefresh() {
        mAdapter.nextPage();
    }

    @Override
    protected void resetPtr() {
        mPtr.setRefreshing(false);
    }
}
