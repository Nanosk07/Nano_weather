package com.nano.nano_weather.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ViewPageAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTab(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);

    }
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }
    public CharSequence getPageTitle(int position){
        return titles.get(position);
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    public void replaceFragment(int index, Fragment fragment) {
        fragments.remove(index);
        fragments.add(index, fragment);
        notifyDataSetChanged();
    }
}
