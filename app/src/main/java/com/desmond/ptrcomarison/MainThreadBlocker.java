package com.desmond.ptrcomarison;

import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiayi Yao on 2015/12/9.
 */
public class MainThreadBlocker {
    public static boolean DISABLE = false;
    private static final int THREAD_COUNT = 2;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static List<Runnable> sRunnableList;

    static {
        sRunnableList = new ArrayList<>();
        for(int i=0; i<THREAD_COUNT; i++){
            sRunnableList.add(new Runnable() {
                @Override
                public void run() {
                    Path path = new Path();
                    compute(path);
                    System.gc();
                    sHandler.post(this);
                }
            });
        }
    }

    public static void start(){
        if(DISABLE) return;
        for (Runnable run: sRunnableList){
            sHandler.post(run);
        }
    }

    public static void stop(){
        if(DISABLE) return;
        for (Runnable run: sRunnableList){
            sHandler.removeCallbacks(run);
        }
    }

    private static void compute(Path path){
        if(path == null) return;

        float i = 1233f;
        float j = 6543f;
        for(float k=1; k<100; k++){
            path.moveTo(0, 0);
            path.cubicTo(i, i, j, j, k, k);
        }
    }
}
