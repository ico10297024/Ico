package ico.ico.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用于ViewPager的适配器,使用fragment来填充item
 */
public class SimpleRecyclerPagerAdapter extends PagerAdapter {

    RecyclerView[] mRecyclerViews;
    RecyclerView.Adapter[] mAdapters;
    String[] mTitles;
    Context mContext;
    OnRecyclerListener mOnRecyclerListener;

    public SimpleRecyclerPagerAdapter(Context context, String[] mTitles, RecyclerView.Adapter... adapter) {
        this.mAdapters = adapter;
        this.mRecyclerViews = new RecyclerView[mAdapters.length];
        this.mContext = context;
        this.mTitles = mTitles;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mRecyclerViews[position] != null) {
            return mRecyclerViews[position];
        }

        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapters[position]);
        if (this.mOnRecyclerListener != null) {
            this.mOnRecyclerListener.onRecycler(recyclerView);
        }


        mRecyclerViews[position] = recyclerView;
        container.addView(mRecyclerViews[position]);

        return mRecyclerViews[position];
    }

    @Override
    public int getCount() {
        return mAdapters.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles == null || mTitles.length == 0) {
            return super.getPageTitle(position);
        }
        return mTitles[position];
    }

    public RecyclerView[] getRecyclerViews() {
        return mRecyclerViews;
    }

    public SimpleRecyclerPagerAdapter setOnRecyclerListener(OnRecyclerListener mOnRecyclerListener) {
        this.mOnRecyclerListener = mOnRecyclerListener;
        return this;
    }

    public interface OnRecyclerListener {
        void onRecycler(RecyclerView recyclerView);
    }
}
