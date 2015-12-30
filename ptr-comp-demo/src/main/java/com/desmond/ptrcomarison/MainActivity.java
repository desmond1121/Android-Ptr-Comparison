package com.desmond.ptrcomarison;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.desmond.ptrcomarison.fragments.AbstractFragment;
import com.desmond.ptrcomarison.fragments.ChrisBanesPtrFragment;
import com.desmond.ptrcomarison.fragments.FlyRefreshPtrFragment;
import com.desmond.ptrcomarison.fragments.JohanPtrFragment;
import com.desmond.ptrcomarison.fragments.LiaohuqiuPtrFragment;
import com.desmond.ptrcomarison.fragments.SwipeFragment;
import com.desmond.ptrcomarison.fragments.YalantisPtrFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<AbstractFragment> mFgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setBitmapsConfig(Bitmap.Config.ARGB_4444)
                .build();
        Fresco.initialize(this, config);

        setContentView(R.layout.activity_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        initFragments();
        vp.setAdapter(new MyAdapter(getSupportFragmentManager()));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(vp);
    }

    private void initFragments() {
        mFgList = new ArrayList<>();
        mFgList.add(new ChrisBanesPtrFragment());
        mFgList.add(new LiaohuqiuPtrFragment());
        mFgList.add(new JohanPtrFragment());
        mFgList.add(new YalantisPtrFragment());
        mFgList.add(new FlyRefreshPtrFragment());
        mFgList.add(new SwipeFragment());
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFgList.get(position).getTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return mFgList.get(position);
        }

        @Override
        public int getCount() {
            return mFgList == null ? 0 : mFgList.size();
        }
    }
}
