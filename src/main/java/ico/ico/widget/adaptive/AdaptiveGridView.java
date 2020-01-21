package ico.ico.widget.adaptive;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 平铺所有列表项
 */
public class AdaptiveGridView extends GridView {
    public AdaptiveGridView(Context context) {
        super(context);
    }

    public AdaptiveGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        heightMeasureSpec = expandSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
