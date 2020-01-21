package ico.ico.ico;

import android.content.Intent;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ico.ico.constant.ActionConstant;
import ico.ico.util.log;

/**
 * Created by admin on 2015/5/6 0006.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public BaseApplication app;
    public Thread.UncaughtExceptionHandler mDefaultHandler;

    public CrashHandler(BaseApplication app) {
        this.app = app;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (TextUtils.isEmpty(log.LOG)) {
            mDefaultHandler.uncaughtException(thread, ex);
            return;
        }
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //添加崩溃时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date());
        //组织文本
        String text = "\n" + currentDate + "   " + app.getPackageName() + "发生崩溃，错误信息如下:\n" + info;
        try {
            log.writeFile(text, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        app.sendBroadcast(new Intent(ActionConstant.BROADCAST_APP_EXIT));
        try {
            mDefaultHandler.uncaughtException(thread, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
