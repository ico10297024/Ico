package ico.ico.ico;

import android.app.Dialog;
import android.app.Service;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.LinkedHashMap;


public abstract class BaseService extends Service {
    public final static String DEFAULT_DIALOG = "default";
    public BaseService mService;
    public BaseApplication mApp;
    public LinkedHashMap<String, Dialog> mDialogs = new LinkedHashMap<>();
    public Toast mToast;
    private Handler mHandler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        mApp = (BaseApplication) mService.getApplication();
    }


    /**
     * 弹出土司
     *
     * @param stringResId
     */
    public void showToast(@StringRes final int stringResId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = getResources().getString(stringResId);
                if (mToast != null) {
                    mToast.setText(content);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(mService, content, Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }


    /**
     * 弹出土司
     *
     * @param text
     */
    public void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = TextUtils.isEmpty(text) ?/* getResources().getString(R.string.ico_application_error)*/"程序出错，请稍候再试!" : text;
                if (mToast != null) {
                    mToast.setText(content);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(mService, content, Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    /**
     * 弹出土司
     *
     * @param text
     */
    public void showToasts(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CharSequence content = TextUtils.isEmpty(text) ? /*getResources().getString(R.string.ico_application_error) */"程序出错，请稍候再试!" : text;
                Toast toast = Toast.makeText(mService, content, Toast.LENGTH_LONG);
                if (mToast == null) {
                    mToast = toast;
                }
                toast.show();
            }
        });
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog) {
        showDialog(_dialog, DEFAULT_DIALOG);
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog() {
        dismissDialog(DEFAULT_DIALOG);
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog, String key) {
        dismissDialog(key);
        mDialogs.put(key, _dialog);
        _dialog.show();
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog(String key) {
        Dialog dialog = mDialogs.remove(key);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
