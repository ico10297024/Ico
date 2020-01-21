package ico.ico.network;

import android.content.Context;

/**
 * 网络请求回调处理
 */
public interface IHttpCallback {

    /**
     * 请求开始前被调用
     *
     * @param context
     */
    void onReady(Context context);

    /**
     * 请求结束后被调用
     *
     * @param context
     */
    void onFinish(Context context);

    /**
     * 请求失败了被调用
     *
     * @param context
     * @param statusCode
     * @param throwable
     */
    void onFailure(Context context, int statusCode, Throwable throwable);
}