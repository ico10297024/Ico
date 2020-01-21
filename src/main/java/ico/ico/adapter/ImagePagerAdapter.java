package ico.ico.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;


/**
 * ViewPager的适配器,用于显示图片,一般用于广告横幅
 */
public abstract class ImagePagerAdapter<V extends ImageView, T> extends PagerAdapter {
    /** 源数据 */
    List<T> mData;
    /** 空闲的组件 */
    Vector<V> freeImageview = new Vector<>();

    Context mContext;
    Callback<T> mCallback;

    public ImagePagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(T... t) {
        this.mData = Arrays.asList(t);
    }

    public void setData(List<T> data) {
        this.mData = new ArrayList<>(data);
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public int getRealPosition(int position) {
        return Math.abs(getCount() + position % getCount()) % getCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        V imageView = null;
        if (freeImageview.size() == 0) {
            imageView = onCreateView();
        } else {
            imageView = freeImageview.remove(0);
        }
        onWidgetInit(imageView, position, mData.get(position));
        imageView.setTag(position + "");
        final V finalImageView = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    int position = Integer.valueOf(v.getTag().toString());
                    mCallback.onItemClick(finalImageView, position, mData.get(position));
                }
            }
        });
        //将图片控件添加到父容器中
        container.addView(imageView);
        return imageView;
    }

    /** 当空闲组件中没有可用的组件时创建新的组件 */
    public abstract V onCreateView();

    /** 初始化组件 */
    public abstract void onWidgetInit(V v, int position, T t);

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        if (object instanceof ImageView) {
            freeImageview.add((V) object);
        }
    }

    public Callback<T> getCallback() {
        return mCallback;
    }

    public void setCallback(Callback<T> callback) {
        this.mCallback = callback;
    }

    public interface Callback<T> {
        void onItemClick(ImageView view, int position, T t);
    }
}
