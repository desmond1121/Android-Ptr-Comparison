package com.desmond.ptrcomarison.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.desmond.ptrcomarison.CommonListAdapter;
import com.desmond.ptrcomarison.MainActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Jiayi Yao on 2015/12/9.
 */
public abstract class AbstractFragment extends Fragment{
    private static final String TAG = "Ptr";

    protected MainActivity mActivity;
    protected View mLayout;
    protected CommonListAdapter mAdapter;

    public abstract String getTitle();

    protected abstract ListView getListView();

    protected abstract int getViewId();

    protected abstract void doRefresh();

    protected abstract void resetPtr();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "AbstractFragment->onCreateView :" + this.getClass().getSimpleName());
        if(mLayout == null){
            mLayout = inflater.inflate(getViewId(), null);
            init();
        }
        return mLayout;
    }

    protected void init(){
        mAdapter = new CommonListAdapter(getActivity());
        mAdapter.setRequestTag(TAG);
        getListView().setAdapter(mAdapter);
    }

    protected void refresh(){
        new RefreshTask().execute();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "AbstractFragment->onDestroyView :" + this.getClass().getSimpleName());
        super.onDestroyView();
        Picasso.with(getActivity()).cancelTag(TAG);
    }

    public CommonListAdapter getAdapter() {
        return mAdapter;
    }

    class RefreshTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            doRefresh();
            resetPtr();
        }
    }
}
