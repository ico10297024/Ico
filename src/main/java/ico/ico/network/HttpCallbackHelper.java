package ico.ico.network;

import android.content.Context;

import com.github.ybq.endless.Endless;

import ico.ico.widget.SwipeRefreshLayout;

/**
 * 由于callback类型比较多，使用该helper能帮助用户减少对不同callback之间的依赖
 * 通过构造函数来创建内部的callback，用户只要根据想要的功能选择构造函数，就不需要关心具体使用哪种callback
 */

public class HttpCallbackHelper implements IHttpCallback {
    protected DialogHttpCallback mDialogHttpCallback;
    protected RefreshHttpCallback mRefreshHttpCallback;

    public HttpCallbackHelper(SwipeRefreshLayout swipeRefreshLayout, Endless endless) {
        mRefreshHttpCallback = new RefreshHttpCallback(swipeRefreshLayout, endless);
    }

    public HttpCallbackHelper() {
        mDialogHttpCallback = new DialogHttpCallback();
    }

    @Override
    public void onReady(Context context) {
        if (mRefreshHttpCallback != null) {
            mRefreshHttpCallback.onReady(context);
        } else {
            mDialogHttpCallback.onReady(context);
        }
    }

    @Override
    public void onFinish(Context context) {
        if (mRefreshHttpCallback != null) {
            mRefreshHttpCallback.onFinish(context);
        } else {
            mDialogHttpCallback.onFinish(context);
        }
    }

    @Override
    public void onFailure(Context context, int statusCode, Throwable throwable) {
        if (mRefreshHttpCallback != null) {
            mRefreshHttpCallback.onFailure(context, statusCode, throwable);
        } else {
            mDialogHttpCallback.onFailure(context, statusCode, throwable);
        }
    }
}
