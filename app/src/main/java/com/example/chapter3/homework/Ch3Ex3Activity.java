package com.example.chapter3.homework;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * 使用 ViewPager 和 Fragment 做一个简单版的好友列表界面
 * 1. 使用 ViewPager 和 Fragment 做个可滑动界面
 * 2. 使用 TabLayout 添加 Tab 支持
 * 3. 对于好友列表 Fragment，使用 Lottie 实现 Loading 效果，在 5s 后展示实际的列表，要求这里的动效是淡入淡出
 */
public class Ch3Ex3Activity extends AppCompatActivity {
    private String[] titles = new String[]{"Friend List1", "Friend List2", "Friend List3"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;
    private ArrayList<PlaceholderFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch3ex3);



        // TODO: ex3-1. 添加 ViewPager 和 Fragment 做可滑动界面
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        for (int i = 0; i < titles.length; i++) {
            //instantiate fragments
            fragments.add(PlaceholderFragment.newInstance(titles[i]));
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }//essential, otherwise the tags' text will be invisible
        });
        // TODO: ex3-2, 添加 TabLayout 支持 Tab
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //fill the tags
        for (int i = 0; i < titles.length; i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }
        tabLayout.setupWithViewPager(viewPager,false);

    }
}
