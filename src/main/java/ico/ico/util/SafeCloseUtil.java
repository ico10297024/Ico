package ico.ico.util;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * 安全关闭句柄工具类
 */
public class SafeCloseUtil {

    private static final String TAG = SafeCloseUtil.class.getSimpleName();

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                log.e(e.toString(), e, TAG);
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                log.e(e.toString(), e, TAG);
            }
        }
    }

    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                log.e(e.toString(), e, TAG);
            }
        }
    }

    public static void close(Writer osw) {
        if (osw != null) {
            try {
                osw.flush();
                osw.close();
            } catch (Exception e) {
                log.e(e.toString(), e, TAG);
            }
        }
    }

    public static void close(AssetManager am) {
        if (am != null) {
            am.close();
        }
    }
}
