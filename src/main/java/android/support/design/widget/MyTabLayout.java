/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.design.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 项目需求,下划线宽度比文字宽度小,设置的地方是私有的,所以对TabLayout源码进行了导出并且重命名为BaseLayout,然后使用MyTabLayout进行了继承
 */
@ViewPager.DecorView
public class MyTabLayout extends BaseTabLayouts {
    public MyTabLayout(Context context) {
        super(context);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置tab
     *
     * @param l
     * @param r
     */
    public void setTabIndicatorPadding(int l, int r) {
        tabIndicatorOffsetLeft = l;
        tabIndicatorOffsetRight = r;
        mTabStrip.postInvalidate();
    }
}
