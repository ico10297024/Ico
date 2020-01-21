package ico.ico.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ico.ico.util.log;

/**
 * WebView的帮助类
 * <p>
 * 可以用他对webview基本的初始化工作
 * <p>
 * {@link CommonWebChromeClient}实现了一些对于html中一些功能的安卓实现,如控制台打印,对话框弹出,选择图片功能
 * <p>
 * {@link CommonWebViewClient}实现对链接以TEL开头的电话拨打功能
 */
public class WebViewHelper {
    public final static String PREFIX_JS = "javascript:";
    public final static String FORMAT_JS_FUNCTION = PREFIX_JS + "%s(%s);";
    private static final String TAG = WebViewHelper.class.getSimpleName();


    /**
     * 初始化webview
     *
     * @param obj     必须传入一个Activity对象或者是Fragment对象,用于拨打电话
     * @param webView
     */
    public static void init(Object obj, WebView webView) {
        WebSettings webSettings = webView.getSettings();
        //设置页面缩放
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //设置支持js
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        //设置允许打开alert
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //允许h5获取地理位置
        webSettings.setGeolocationEnabled(true);

        //设置允许打开图片上传对话框
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebChromeClient(new CommonWebChromeClient());
        try {
            webView.setWebViewClient(new CommonWebViewClient(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** html-标记格式转换 */
    public static String escapeHtmlTagsReturn(String src) {
        String rtr = src;
        if (!StringHelper.isBlank(rtr)) {
            rtr = rtr.replaceAll("&lt;", "<");
            rtr = rtr.replaceAll("&gt;", ">");
            rtr = rtr.replaceAll("&amp;", "&");
            rtr = rtr.replaceAll("&apos;", "'");
            rtr = rtr.replaceAll("&quot;", "\"");
        }
        return rtr;
    }

    /**
     * 对字符串使用单引号包裹
     *
     * @param param 要包裹的字符串
     * @return String 返回包裹后的字符串
     */
    public static String wrapParam(Object param) {
        return wrapParam(param, "'");
    }

    /**
     * 对字符串使用指定的字符进行包裹
     *
     * @param param   要包裹的字符串
     * @param wrapStr 包裹使用的字符
     * @return String 返回包裹后的字符串
     */
    public static String wrapParam(Object param, String wrapStr) {
        return wrapStr + param + wrapStr;
    }

    /**
     * 执行js函数
     *
     * @param webView  webview对象
     * @param function 要执行的js函数名,格式限定为XXX/XXX()/xxx(...)，函数中带的参数将自动截取，与params进行拼接
     * @param params   要执行的js函数的参数列表，js函数参数不做处理直接拼接
     * @return String 返回构造完成并已执行的js代码
     */
    public static String execJsFunction(WebView webView, String function, Object... params) {
        //empty检查
        if (TextUtils.isEmpty(function)) {
            log.ew("execJsFunction,function参数为空，无法构造js代码", TAG);
            return null;
        }

        String js = genJsCode(function, params);
        if (!TextUtils.isEmpty(js)) {
            execJsCode(webView, js);
        }
        log.d("execJsFunction,执行js函数，" + js, TAG);
        return js;
    }

    /**
     * 执行js代码
     *
     * @param webView webview对象
     * @param js      要执行的js代码
     *                可以通过{@link #genJsCode(String, Object...)}构建
     */
    public static void execJsCode(WebView webView, String js) {
        //empty检查
        if (TextUtils.isEmpty(js)) {
            log.e("execJsCode,要执行的js代码为空，无法执行", TAG);
            return;
        }

        String _js = js;
        if (!js.startsWith(PREFIX_JS)) {
            _js = PREFIX_JS + js;
        }
        webView.loadUrl(_js);
        log.d("execJsCode,执行js代码，" + js, TAG);
    }

    /**
     * 创建js函数调用代码
     *
     * @param function 要执行的js函数名,格式限定为XXX/XXX()/xxx(...)，函数中带的参数将自动截取，与params进行拼接
     * @param params   要执行的js函数的参数列表，js函数参数不做处理直接拼接
     * @return String 返回构造完成后的js代码
     */
    public static String genJsCode(String function, Object... params) {
        //empty检查
        if (TextUtils.isEmpty(function)) {
            log.ew("genJsCode,function参数为空，无法构造js代码", TAG);
            return null;
        }
        //函数名
        String _function = null;
        //参数
        StringBuffer _params = new StringBuffer();
        //首先检查function内带不带参数
        int start = function.lastIndexOf("(");
        int end = function.lastIndexOf(")");
        //start和end都不等于-1，说明中间有参数,将参数拼接到_params
        if (start != -1 && end != -1) {
            _params.append(function.substring(start + 1, end));
        }
        //取函数名
        if (start != -1) {
            _function = function.substring(0, start);
        } else {
            _function = function;
        }
        //拼接入参的参数
        if (params != null && params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                //空指针判断
                if (params[i] == null || TextUtils.isEmpty(params[i].toString())) {
                    continue;
                }

                String param = params[i].toString().trim();
                //有数据，拼接逗号
                if (_params.length() != 0) {
                    _params.append(",");
                }
                //拼接参数
                _params.append(param);
            }
        }
        String js = String.format(FORMAT_JS_FUNCTION, _function, _params.toString());
        return js;
    }

    /**
     * 加载asset中的html文件
     *
     * @param webView  webview对象
     * @param htmlName html文件的路径名,开头不用加/
     */
    public static void loadAssetsHtml(WebView webView, String htmlName) {
        webView.loadUrl(String.format("file:///android_asset/%s", htmlName));
    }

    /**
     * 加载sd卡中的html文件
     *
     * @param webView  webview对象
     * @param htmlName html文件的路径名,开头不用加/
     */
    public static void loadSDHtml(WebView webView, String htmlName) {
        webView.loadUrl(String.format("content:///com.android.htmlfileprovider/sdcard/%s", htmlName));
    }

    /**
     * 加载网络上的html文件
     *
     * @param webView webview对象
     * @param url     html文件的路径名,开头不用加/
     */
    public static void loadNetHtml(WebView webView, String url) {
        webView.loadUrl(url);
    }

    /**
     * 加载html代码
     *
     * @param webView webview对象
     * @param data    html代码
     */
    public static void loadHtmlCode(WebView webView, String data) {
        webView.loadData(data, "text/html;charset=UTF-8", "UTF-8");
    }

    public static class CommonWebChromeClient extends WebChromeClient {
        OnImageSelectedListener mOnImageSelectedListener;
        ValueCallback<Uri> mUploadMsg;
        ValueCallback<Uri[]> mUploadMsgs;

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            log.w(String.format("onJsAlert==url：%s；messgae：%s；", url, message));
            return false;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            log.d(cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        //For Android >=5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            log.w("onShowFileChooser==");
            mUploadMsgs = filePathCallback;
            onImageSelected(null, filePathCallback);
            return true;
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            log.w("onShowFileChooser1==");
            mUploadMsg = uploadMsg;
            onImageSelected(uploadMsg, null);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            log.w("onShowFileChooser2==");
            mUploadMsg = uploadMsg;
            onImageSelected(uploadMsg, null);
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            log.w("openFileChooser3==");
            mUploadMsg = uploadMsg;
            onImageSelected(uploadMsg, null);
        }

        private void onImageSelected(ValueCallback<Uri> mUploadMsg, ValueCallback<Uri[]> mUploadMsgs) {
            if (mOnImageSelectedListener != null) {
                mOnImageSelectedListener.onImageSelected(mUploadMsg, mUploadMsgs);
            }
        }

        public OnImageSelectedListener getOnImageSelectedListener() {
            return mOnImageSelectedListener;
        }

        public void setOnImageSelectedListener(OnImageSelectedListener mOnImageSelectedListener) {
            this.mOnImageSelectedListener = mOnImageSelectedListener;
        }

        public interface OnImageSelectedListener {
            /**
             * 当Web控件发起图片选择时触发
             *
             * @param mUploadMsg  图片单选时,需要回调传给前台的回调对象
             * @param mUploadMsgs 图片单选时,需要回调传给前台的回调对象
             */
            void onImageSelected(ValueCallback<Uri> mUploadMsg, ValueCallback<Uri[]> mUploadMsgs);
        }
    }

    public static class CommonWebViewClient extends WebViewClient {
        Object mObject;

        /**
         * 初始化webview客户端
         *
         * @param object 必须传入一个Activity对象或者是Fragment对象,用于拨打电话
         */
        public CommonWebViewClient(Object object) throws Exception {
            this.mObject = object;
            if (!(object instanceof Activity) && (object instanceof Fragment)) {
                throw new Exception("object must instanceof Activity or Fragment");
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            log.w("shouldOverrideUrlLoading=" + request.toString());
            return super.shouldOverrideUrlLoading(view, request);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            log.w("shouldOverrideUrlLoading=" + url);
            if (url.toUpperCase().startsWith("TEL")) {
                if (mObject instanceof Activity) {
                    ((Activity) mObject).startActivity(getIntentByDial(url.substring(4)));
                } else if (mObject instanceof Fragment) {
                    ((Fragment) mObject).startActivity(getIntentByDial(url.substring(4)));
                }
                return true;
            }
            WebViewHelper.loadNetHtml(view, url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /** 意图--跳转拨打电话的界面，自动填入电话号码 */
    public static Intent getIntentByDial(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        return intent;
    }
}
