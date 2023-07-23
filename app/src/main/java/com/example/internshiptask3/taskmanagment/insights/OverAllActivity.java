package com.example.internshiptask3.taskmanagment.insights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.internshiptask3.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class OverAllActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    TabItem tabItem1, tabItem2, tabItem3;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_all);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabItem1 = findViewById(R.id.assignedTab);
        tabItem2 = findViewById(R.id.missingTab);
        tabItem3 = findViewById(R.id.doneTab);



        tabLayout.setupWithViewPager(viewPager);

//        ViewPagerAdapter viewPagerAdapter =
//                new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPagerAdapter.addFragment(new AssignedFragment(), "Assigned");
        viewPagerAdapter.addFragment(new MissingFragment(), "Missing");
        viewPagerAdapter.addFragment(new DoneFragment(), "Done");
        viewPager.setAdapter(viewPagerAdapter);

//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//
//                if(tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2){
//
//                    viewPagerAdapter.notifyDataSetChanged();
//
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
}