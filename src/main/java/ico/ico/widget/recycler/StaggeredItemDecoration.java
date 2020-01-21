package ico.ico.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class StaggeredItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public StaggeredItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = this.space;
        outRect.left = this.space;
        outRect.right = this.space;
        outRect.bottom = this.space;
    }
}
