package ico.ico.network;

import android.content.Context;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import ico.ico.ico.R;
import ico.ico.network.dispather.IDispather;
import ico.ico.util.Common;
import ico.ico.util.log;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ICO on 2017/1/9 0009.
 */
public class HttpUtil {
    /**
     * 自定义的statusCode
     */
    public final static int SC_FAIL = -1;//一般请求成功，在处理返回数据时出现异常

    /**
     * 根据网络状态码返回对应的提示信息
     *
     * @param context
     * @param statusCode
     * @return
     */
    public static CharSequence getCodeMsg(Context context, int statusCode, Throwable throwable) {
        CharSequence text = "";
        switch (statusCode) {
            case 404://HttpStatus.SC_NOT_FOUND:
                text = context.getResources().getString(R.string.ico_network_connect_404);
                break;
            case 408://HttpStatus.SC_REQUEST_TIMEOUT:
                text = context.getResources().getString(R.string.ico_network_request_timesout);
                break;
            case 500://HttpStatus.SC_INTERNAL_SERVER_ERROR:
                text = context.getResources().getString(R.string.ico_network_internal_server_error);
                break;
            case 400://HttpStatus.SC_BAD_REQUEST:
                text = context.getResources().getString(R.string.ico_network_bad_request);
                break;
            case SC_FAIL:
                text = context.getResources().getString(R.string.ico_network_parameter_analysis_error);
                break;
            case 0://没有网络|无法连接上服务器
                if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectTimeoutException) {
                    text = context.getResources().getString(R.string.ico_network_server_timesout);
                } else {
                    text = context.getResources().getString(R.string.ico_network_no);
                }
                break;
            default:
                text = context.getResources().getString(R.string.ico_network_connect_unknown_error);
                break;
        }
        return text;
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static Subscription execute(final Context context, final IHttpCallback iHttpCallback, final Call<byte[]> call, final IDispather dispather) {
        return Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Func1<String, HttpResult>() {
                    @Override
                    public HttpResult call(String s) {
                        /*同步请求，获取Response*/
                        Response<byte[]> res = null;
                        Exception exception = null;
                        try {
                            res = call.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                            exception = e;
                        }
                        return new HttpResult(res, exception);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        log.w("======doOnUnsubscribe");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
                    @Override
                    public void onStart() {
                        //日志记录
                        log.w(String.format("请求准备，%s,url：%s；请求参数：%s", call.request().method(), call.request().url(), bodyToString(call.request().body())), "HTTP");
                        //调用回调
                        iHttpCallback.onReady(context);
                    }

                    @Override
                    public void onCompleted() {
                        //调用回调
                        iHttpCallback.onFinish(context);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //日志记录
                        log.w(String.format("程序出错，Throwable：%s；", e.toString()), "HTTP");
                        e.printStackTrace();
                        //交由转发器进行转发处理
                        dispather.dispath(new HttpResult(null, new Exception(e)));
                        iHttpCallback.onFinish(context);
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        //日志记录
                        if (httpResult.getResponse() == null) {
                            log.w(String.format("请求失败，url：%s；状态码：%d；数据：%s", call.request().url(), 0, httpResult.getException().toString()), "HTTP");
                        } else if (httpResult.getResponse().code() == 200) {
                            log.w(String.format("请求成功，url：%s；状态码：%d；数据：%s", call.request().url(), 200, new String(httpResult.getResponse().body())), "HTTP");
                            log.d(String.format("请求成功，url：%s；状态码：%d；数据：%s", call.request().url(), 200, Common.bytes2Int16(" ", httpResult.getResponse().body())), "HTTP");
                        } else {
                            log.w(String.format("请求失败，url：%s；状态码：%d；数据：%s", call.request().url(), httpResult.getResponse().code(), httpResult.getResponse().body() != null ? String.valueOf(httpResult.getResponse().body()).toString() : (httpResult.getException() != null ? httpResult.getException().toString() : "无")), "HTTP");
                        }
                        //交由转发器进行转发处理
                        dispather.dispath(httpResult);
                    }
                });
    }
}
