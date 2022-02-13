package com.danhtran12797.thd.foodyapp.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrFragment;
    private ArrayList<String> arrTitle;

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        arrFragment = new ArrayList<>();
        arrTitle = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return arrFragment.get(position);
    }

    @Override
    public int getCount() {
        return arrFragment.size();
    }

    public void addFragment(Fragment fragment, String title) {
        arrFragment.add(fragment);
        arrTitle.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arrTitle.get(position);
    }
}
