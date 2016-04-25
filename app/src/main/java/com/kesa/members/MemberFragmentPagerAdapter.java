package com.kesa.members;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * represents each page as a {@link Fragment} that is persistently
 * kept in the fragment manager as long as the user can return to the page.
 *
 * @author hongil
 */
public class MemberFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> fragmentTitles;

    public MemberFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    public MemberFragmentPagerAdapter setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
        return this;
    }

    public MemberFragmentPagerAdapter setFragmentTitles(List<String> fragmentTitles) {
        this.fragmentTitles = fragmentTitles;
        return this;
    }
}
