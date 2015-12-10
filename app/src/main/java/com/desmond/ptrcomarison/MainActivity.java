package com.desmond.ptrcomarison;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.desmond.ptrcomarison.common.MainThreadBlocker;
import com.desmond.ptrcomarison.fragments.AbstractFragment;
import com.desmond.ptrcomarison.fragments.ChrisBanesPtrFragment;
import com.desmond.ptrcomarison.fragments.JohanPtrFragment;
import com.desmond.ptrcomarison.fragments.LiaohuqiuPtrFragment;
import com.desmond.ptrcomarison.fragments.FlyRefreshPtrFragment;
import com.desmond.ptrcomarison.fragments.YalantisPtrFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<AbstractFragment> mFgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        initFragments();
        vp.setAdapter(new MyAdapter(getSupportFragmentManager()));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(vp);

        MainThreadBlocker.DISABLE = true;
        MainThreadBlocker.start();
    }

    private void initFragments(){
        mFgList = new ArrayList<>();
        mFgList.add(new ChrisBanesPtrFragment());
        mFgList.add(new LiaohuqiuPtrFragment());
        mFgList.add(new JohanPtrFragment());
        mFgList.add(new YalantisPtrFragment());
        mFgList.add(new FlyRefreshPtrFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            Picasso.with(this).shutdown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter{

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
