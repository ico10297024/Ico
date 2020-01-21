package ico.ico.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by admin on 2014/12/22 0022.
 */
public class DialogUtil {

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static ProgressDialog createProgress(Context context, String title, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog;
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static ProgressDialog createProgress(Context context, String title, String message, boolean indeterminate, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static ProgressDialog createProgress(Context context, String title, String message, int theme) {
        ProgressDialog dialog = new ProgressDialog(context, theme);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog;
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, String title, int message, int no, DialogInterface.OnClickListener nOnClick, int yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(no, nOnClick);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }


    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, int title, int message, int no, DialogInterface.OnClickListener nOnClick, int yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(no, nOnClick);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, int title, int message, int yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }


    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, int title, String message, int yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, String title, int message, int yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, String title, String message, String no, DialogInterface.OnClickListener nOnClick, String yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(no, nOnClick);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }

    /**
     * 创建一个默认的进度条对话框，默认是圆形进度条
     *
     * @param context
     * @param title
     * @param message
     */
    public static AlertDialog createAlert(Context context, String title, String message, String yes, DialogInterface.OnClickListener yOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yes, yOnClick);
        return builder.create();
    }

    /**
     * 显示对话框
     *
     * @param dialog
     * @return
     */
    public static void showDialog(Dialog dialog) {
        if ((dialog == null) || (dialog.isShowing())) {
            return;
        }
        dialog.show();
    }

    /**
     * 关闭一个对话框
     *
     * @param dialog
     * @return
     */
    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
