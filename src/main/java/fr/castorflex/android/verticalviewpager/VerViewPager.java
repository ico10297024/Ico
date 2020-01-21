package fr.castorflex.android.verticalviewpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 基于竖向的ViewPager，开发一个自定义的，用于实现淘宝商品详情页的ViewPager
 * 目前网络上有很多实现淘宝商品详情页的demo，比较经典的chenjing所做的ScrollViewContainer
 * 它是在ScrollView中嵌套2个ScrollView,在这样的结构下，如果我们要在其中一个ScrollView固定一个标签页，这会相对比较麻烦，而且一个详情页的逻辑会特别的多，全部写在一个activiy不利于代码的可维护性，阅读性，而且这只能嵌套ScrollView，数量限制为2个
 * 本ViewPager，需要使用FragmentPageAdapter，并且其中每一个Fragment，需要实现BoundaryDetection的接口
 */
public class VerViewPager extends VerticalViewPager {

    //    public List<BoundaryDetection> mBoundaryDetection = new ArrayList<>();
    public FragmentPagerAdapter mFragmentPagerAdapter;
    //用于判断手势动作
    public GestureDetectorCompat mGesturedDetectorCompat;
    protected int startScrollY;


    public VerViewPager(Context context) {
        super(context);
        mGesturedDetectorCompat = new GestureDetectorCompat(getContext(), new MyGestureListener());
    }

    public VerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGesturedDetectorCompat = new GestureDetectorCompat(getContext(), new MyGestureListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        //若没有适配器，则直接跳过
        if (mFragmentPagerAdapter == null) {
            return super.dispatchTouchEvent(ev);
        }
        //记录事件起始时的滚动位置
        if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_DOWN) {
            startScrollY = getScrollY();
            super.dispatchTouchEvent(ev);
        }
        //手势监听
        boolean isReturn = mGesturedDetectorCompat.onTouchEvent(ev);
        //事件结束
        if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_UP || MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_CANCEL) {
            int currItem = getCurrentItem();
            int diffScrollY = getScrollY() - startScrollY;
            if (Math.abs(diffScrollY) > getHeight() / 5) {
                if (diffScrollY > 0) {
                    currItem++;
                } else {
                    currItem--;
                }
            }
            setCurrentItemInternal(currItem, true, true, 1);
            super.dispatchTouchEvent(ev);
        }
//        log.i("-------------" + isReturn + "|" + MotionEventCompat.getActionMasked(ev));
        //在滚动时，虽然在手势判断中scroll返回了true，但是若是没有触发任何手势函数，同样有可能返回false,所以还要根据实际情况进行判断
        if (isReturn == false && getScrollY() % getHeight() == 0) {
            isReturn = super.dispatchTouchEvent(ev);
        }
        return isReturn;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        return false;
    }

    /**
     * The adapter must be set using the method
     * 必须使用该方法设置适配器
     *
     * @param adapter
     * @throws IllegalArgumentException Each fragment in the adapter must implement a boundary decetion interface.
     *                                  这个适配器中所有的fragment必须实现BoundaryDetection接口
     */
    public void setFragmentPagerAdapter(FragmentPagerAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getItem(i);
            //未实现接口，抛出异常
            if (!(fragment instanceof BoundaryDetection)) {
                throw new IllegalArgumentException("Each fragment in the adapter must implement a boundary decetion interface");
            }
        }
        this.mFragmentPagerAdapter = adapter;
        super.setAdapter(adapter);
    }

    /**
     * 边界检测器，每一个fragment必须实现该接口
     */
    public interface BoundaryDetection {
        boolean isTop();

        boolean isBottom();
    }

    /**
     * 手势探测器
     */
    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
//            log.i("=============onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            log.i("=============onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            log.i("=============onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            log.i("=============onScroll");
            //distance=e1-e2
            //distance<0 scroll-
            //distance>0 scroll+
            //获得当前索引
            int currItem = getCurrentItem();
            //获取当前显示的界面所对应的碎片
            BoundaryDetection boundaryDetection = (BoundaryDetection) mFragmentPagerAdapter.getItem(currItem);
//            log.w("dispatchTouchEvent|onScroll=====" + boundaryDetection.isTop() + "||" + boundaryDetection.isBottom() + "||" + distanceY);


            /*当前页的内部页码处于首码或尾码，并且还在往首码或尾码方向滚动，此时滚动外部页码*/
            if ((boundaryDetection.isTop() && boundaryDetection.isBottom()) ||
                    (boundaryDetection.isTop() && distanceY < 0) ||
                    (boundaryDetection.isBottom() && distanceY > 0)) {
                VerViewPager.this.scrollBy(0, (int) distanceY);
                return true;
            }
            /*当前页的内部页码处于首码或尾码，但滚动方向为尾码或首码，此时应该根据当前的外部页码和内部页码的情况判断应该滚动外部页码还是内部页码*/
            //内部页码
            //在3页甚至更多的情况下，中间页的内部页码(页面内部的组件，如ScrollView的页码，自然"外部页码"就是ViewPager)在上边界时
            //先下拉，此时会滚动页码，再上拉时，由于中间页内部页码处于内部首码，所以事件会被中间页的内部控件所消费
            // 综上所述，在已达到上或下边界，但滚动方向为反方向时，还需要判断当前

            //创建一个变量用于保存最终要滚动的距离
            int _distanceY = 0;
            //获取每一页的页码长度
            int pageCount = getHeight();
            //当前的外部页码
            int currPageNum = VerViewPager.this.getScrollY();
            //当前页
            int currPage = getCurrentItem();
            //必须使用该表达式进行包裹，因为如果用户移动的慢，distanceY是一个0.nnn的小数，而滚动使用的是int类型，那么要移动的距离就是0，最终就会导致外部页码在偏移状态下，直接滚动了内部页码
//            log.w("当前页外部首码：" + (currPageNum % pageCount));
            if (currPageNum % pageCount != 0) {
                //在首码，往尾码滚动
                if ((boundaryDetection.isTop() && distanceY > 0) && currPageNum % pageCount != 0) {
                    //当前页的首码
                    int firstPageNum = currPage * pageCount;
                    //计算当前页码偏移首码多少页码，偏移量为正数
                    int offsetY = firstPageNum - currPageNum;
//                    log.w("当前页外部首码：" + firstPageNum + "；当前页码：" + currPageNum + "；页码偏移：" + offsetY + "；distanceY：" + distanceY);
                    if (Math.abs(offsetY) > Math.abs(distanceY)) {
                        _distanceY = (int) distanceY;
                    } else {
                        _distanceY = Math.abs(offsetY);
                    }
                }
                if ((boundaryDetection.isBottom() && distanceY < 0) && currPageNum % pageCount != 0) {
                    //当前页的尾码
                    int lastPageNum = currPage * pageCount;
                    //计算当前页码偏移尾码多少页码，偏移量为负数
                    int offsetY = lastPageNum - currPageNum;
//                    log.w("当前页外部尾码：" + lastPageNum + "；当前页码：" + currPageNum + "；页码偏移：" + offsetY + "；distanceY：" + distanceY);
                    if (Math.abs(offsetY) > Math.abs(distanceY)) {
                        _distanceY = (int) distanceY;
                    } else {
                        _distanceY = -Math.abs(offsetY);
                    }
                }
                //上述判断计算完后，进行滚动
//                log.w("需要移动距离：" + _distanceY);
                if (_distanceY != 0) {
                    VerViewPager.this.scrollBy(0, _distanceY);
                }
                return true;
            }
            return false;
            //这里对上述注释中的几个名词作一个解释，用以日后查看时便于理解
            /*
              页码：页码就是控件的scroll值（当前控件是竖向的ViewPager，所以在这里的页码一般指的是scollY）
              ViewPager有页码，Fragment中若是有ScrollView，则也有页码
              由于此控件是为了做淘宝详情页，所以在这里只分为 外部页码和内部页码
              在本demo中，ViewPager的每一页具有自己的页码范围，假设当前ViewPager的高度为1000，则各页对应页码范围如下
              0     0~(1000-1)      0~999
              1     1000~(2000-1)   1000~1999
              2     2000~(3000-1)   2000~2999
            */
            /*
              首码、尾码：控件具有滚动属性，那么就有两种情况，控件滚动到了顶部和底部
              以上面假设页码范围，第1页为例，1000即为第1页的首码，1999即为第1页的尾码
              同时根据外部页码和内部页码又分为外部首码、尾码，内部首码、尾码
            */
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            log.i("=============onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            log.i("=============onFling");
            return false;
        }
    }
}
