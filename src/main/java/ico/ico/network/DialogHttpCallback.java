package ico.ico.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import ico.ico.ico.R;
import rx.Subscription;

/**
 * 所有网络请求回调处理
 * 开发者需继承该class，然后添加自己的方法，然后通过Dispatcher转发到自己的方法中进行对应url的处理
 */
public class DialogHttpCallback implements IHttpCallback {
    ProgressDialog mProDialog;
    Subscription mSubscription;
    Toast mToast;

    /**
     * 弹出一个默认的progress，上面有取消按钮
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public ProgressDialog showProDialog(Context context, String title, String message) {
        if (title == null) {
            title = context.getResources().getString(R.string.ico_progress_default_title);
        }
        if (message == null) {
            message = context.getResources().getString(R.string.ico_progress_default_message);
        }
        if (mProDialog == null || !mProDialog.isShowing()) {
            mProDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
            mProDialog.setTitle(title);
            mProDialog.setMessage(message);
            mProDialog.setButton(context.getResources().getString(R.string.ico_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mSubscription != null) {
                        mSubscription.unsubscribe();
                        mSubscription = null;
                    }
                    dismissProDialog();
                }
            });
            mProDialog.setCancelable(false);
            if (!mProDialog.isShowing()) {
                mProDialog.show();
            }
        }
        return mProDialog;
    }


    public void dismissProDialog() {
        if (mProDialog != null && mProDialog.isShowing()) {
            mProDialog.dismiss();
            mProDialog = null;
        }
    }

    /**
     * 准备开始请求时，回调该方法
     *
     * @param context
     */
    @Override
    public void onReady(Context context) {
        if (mProDialog == null || !mProDialog.isShowing()) {
            showProDialog(context, null, null);
        }
    }


    /**
     * 请求结束并且所有回调全部完成后回调该方法
     *
     * @param context
     */
    @Override
    public void onFinish(Context context) {
        if ((context instanceof Activity) && (((Activity) context).isFinishing())) {
            return;
        }
        dismissProDialog();
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