package com.desmond.ptrcomarison.fragments;

import android.widget.ListView;

import com.desmond.ptrcomarison.R;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Ptr Fragment using {@link "https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh"}
 *
 * Created by desmond on 2015/12/9.
 */
public class LiaohuqiuPtrFragment extends AbstractFragment {
    private PtrClassicFrameLayout mPtr;

    @Override
    public String getTitle() {
        return "Huqiu Liao";
    }

    @Override
    protected ListView getListView() {
        if (mPtr == null) {
            mPtr = (PtrClassicFrameLayout) mLayout.findViewById(R.id.ptr_frame_layout);
        }

        if (mPtr.getContentView() instanceof ListView){
            return (ListView) mPtr.getContentView();
        }
        return null;
    }

    @Override
    protected int getViewId() {
        return R.layout.layout_liaohuqiu;
    }

    @Override
    protected void init(){
        super.init();
        mPtr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
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
        mPtr.refreshComplete();
    }
}
