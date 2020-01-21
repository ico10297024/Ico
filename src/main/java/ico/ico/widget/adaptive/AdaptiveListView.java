package ico.ico.widget.adaptive;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 平铺所有列表项
 */
public class AdaptiveListView extends ListView {


    public AdaptiveListView(Context context) {
        this(context, null);
    }

    public AdaptiveListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdaptiveListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        heightMeasureSpec = expandSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
