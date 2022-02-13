package com.danhtran12797.thd.foodyapp.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class LoginAdapter extends FragmentPagerAdapter {

    ArrayList<String> arrTitle = new ArrayList<>();
    ArrayList<Fragment> arrFragments = new ArrayList<>();

    public LoginAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return arrFragments.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        arrTitle.add(title);
        arrFragments.add(fragment);
    }

    @Override
    public int getCount() {
        return arrFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arrTitle.get(position);
    }
}
