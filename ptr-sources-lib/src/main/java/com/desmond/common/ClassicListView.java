package com.desmond.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by desmond on 2015/12/30.
 */
public class ClassicListView extends ListView{
    private static final String TAG = "ClassicListView";

    public ClassicListView(Context context) {
        super(context);
    }

    public ClassicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassicListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        Log.i(TAG, "ClassicListView->dispatchTouchEvent " + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        Log.i(TAG, "ClassicListView->onTouchEvent " + result);
        return result;
    }
}
