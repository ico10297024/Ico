package ico.ico.network;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.ybq.endless.Endless;

import ico.ico.widget.SwipeRefreshLayout;
import rx.Subscription;

/**
 * 所有网络请求回调处理
 * 开发者需继承该class，然后添加自己的方法，然后通过Dispatcher转发到自己的方法中进行对应url的处理
 */
public class RefreshHttpCallback implements IHttpCallback {
    SwipeRefreshLayout mSwipeRefreshLayout;
    Endless endless;
    Subscription mSubscription;
    Toast mToast;

    public RefreshHttpCallback(SwipeRefreshLayout mSwipeRefreshLayout, Endless endless) {
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.endless = endless;
    }

    /**
     * 准备开始请求时，回调该方法
     *
     * @param context
     */
    @Override
    public void onReady(Context context) {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    /**
     * 请求结束并且所有回调全部完成后回调该方法
     *
     * @param context
     */
    @Override
    public void onFinish(Context context) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (endless != null) {
            endless.loadMoreComplete();
        }
    }

    /**
     * 取消请求操作
     */
    public void cancelRq() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    /**
     * 如果请求失败了，将会回调该方法
     *
     * @param context
     */
    @Override
    public void onFailure(Context context, int statusCode, Throwable throwable) {
        CharSequence text = HttpUtil.getCodeMsg(context, statusCode, throwable);
        CharSequence content = TextUtils.isEmpty(text) ?/* getResources().getString(R.string.ico_application_error)*/"程序出错，请稍候再试!" : text;
        if (mToast != null) {
            mToast.setText(content);
            mToast.show();
            return;
        }
        mToast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        mToast.show();
    }

}