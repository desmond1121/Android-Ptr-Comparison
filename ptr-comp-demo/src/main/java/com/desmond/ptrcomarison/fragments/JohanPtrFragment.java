package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.desmond.ptrcomarison.R;
import com.markupartist.android.widget.PullToRefreshListView;

/**
 * Ptr Fragment using {@link "https://github.com/johannilsson/android-pulltorefresh"}
 *
 * Created by desmond on 2015/12/9.
 */
public class JohanPtrFragment extends AbstractFragment{
    private static final String TAG = "JohanP trFragment";
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
