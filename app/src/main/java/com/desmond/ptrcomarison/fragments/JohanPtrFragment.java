package com.desmond.ptrcomarison.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.desmond.ptrcomarison.R;
import com.markupartist.android.widget.PullToRefreshListView;

/**
 * Created by Jiayi Yao on 2015/12/9.
 */
public class JohanPtrFragment extends AbstractFragment{
    private static final String TAG = "JohanPtrFragment";
    private PullToRefreshListView mListView;

    @Override
    public String getTitle() {
        return "Johan Nilsson";
    }

    @Override
    protected ListView getListView() {
        if(mListView == null){
            mListView = (PullToRefreshListView) mLayout.findViewById(R.id.johan_list_view);
        }
        return mListView;
    }

    @Override
    protected void init(){
        super.init();
        mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_johan;
    }

    @Override
    protected void doRefresh() {
        mAdapter.nextPage();
    }

    @Override
    protected void resetPtr() {
        mListView.onRefreshComplete();
    }
}
