package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.desmond.common.ClassicListView;
import com.desmond.ptrcomarison.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Ptr Fragment using {@link "https://github.com/chrisbanes/Android-PullToRefresh"}
 *
 * Created by desmond on 2015/12/9.
 */
public class ChrisBanesPtrFragment extends AbstractFragment {
    private PullToRefreshListView mPtr;

    @Override
    public String getTitle() {
        return "Chris Banes";
    }

    @Override
    protected ListView getListView() {
        if (mPtr == null) {
            mPtr = (PullToRefreshListView) mLayout.findViewById(R.id.list_view);
        }
        return mPtr.getRefreshableView();
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_chirs_banes;
    }


    @Override
    protected void init(){
        super.init();
        mPtr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ClassicListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ClassicListView> refreshView) {
                refresh();
            }
        });
    }

    @Override
    protected void doRefresh() {
        mAdapter.nextPage();
    }

    @Override
    protected void resetPtr() {
        mPtr.onRefreshComplete();
    }
}
