package com.java.jiangbaisheng;

import androidx.fragment.app.*;

import java.util.List;

public class KocoFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public KocoFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {//必须实现
        return mFragments.get(position);
    }

    @Override
    public int getCount() {//必须实现
        return mFragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {//选择性实现
//        return mFragments.get(position).getClass().getSimpleName();
//    }
}
