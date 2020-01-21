package ico.ico.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ico.ico.ico.BaseFragment;

/**
 * 用于ViewPager的适配器,使用fragment来填充item
 * 只需要使用{@link SimpleFragmentPagerAdapter#SimpleFragmentPagerAdapter(FragmentManager, List)}传入fragment的集合
 * 或者使用{@link SimpleFragmentPagerAdapter#SimpleFragmentPagerAdapter(FragmentManager, List, List)}传入fragment的集合以及标题即可
 */
public class SimpleFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    List<BaseFragment> fragments;
    List<String> titles;

    public SimpleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SimpleFragmentPagerAdapter(FragmentManager fm, BaseFragment[] fragments) {
        super(fm);
        this.fragments = new ArrayList<>();
        Collections.addAll(this.fragments, fragments);
    }

    public SimpleFragmentPagerAdapter(FragmentManager fm, BaseFragment[] fragments, String[] titles) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
        Collections.addAll(this.fragments, fragments);
        Collections.addAll(this.titles, titles);
    }

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        if (fragments == null) {
            return 0;
        }
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles == null || titles.size() == 0) {
            return super.getPageTitle(position);
        }
        return titles.get(position);
    }

    public List<BaseFragment> getFragments() {
        return fragments;
    }

    public SimpleFragmentPagerAdapter setFragments(List<BaseFragment> fragments) {
        this.fragments = fragments;
        return this;
    }

    public List<String> getTitles() {
        return titles;
    }

    public SimpleFragmentPagerAdapter setTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }
}
