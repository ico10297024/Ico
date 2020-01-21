package ico.ico.network.dispather;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;

import ico.ico.network.HttpResult;
import ico.ico.network.IHttpCallback;

/**
 * Created by ICO on 2017/3/21 0021.
 */

public abstract class SimpleDispather<T> implements IDispather {
    Context mContext;
    IHttpCallback mCallback;

    public SimpleDispather(Context context, IHttpCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public void dispath(HttpResult httpResult) {
        //数据解析
        int statusCode = httpResult.getResponse() == null ? 0 : httpResult.getResponse().code();
        T t = null;
        if (statusCode == 200) {
            try {
                t = onDataHandle(httpResult.getResponse().body());
            } catch (Exception e) {
                e.printStackTrace();
                statusCode = -1;
            }
        }
        //调用处理
        if (!onCallback(statusCode, t)) {
            if (statusCode != 200 || httpResult.getResponse() == null) {
                if (httpResult.getResponse() == null) {
                    mCallback.onFailure(mContext, 0, httpResult.getException());
                } else {
                    mCallback.onFailure(mContext, statusCode, httpResult.getException());
                }
            }
        }
    }

    public abstract T onDataHandle(byte[] data) throws IOException, JSONException;

    public abstract boolean onCallback(int statusCode, T t);
}
