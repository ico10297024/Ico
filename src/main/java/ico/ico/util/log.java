package ico.ico.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ico.ico.ico.BaseApplication;

/**
 * Created by admin on 2015/4/21.
 */
public class log {
    final static String COMMON_TAG = "ico_";
    /**
     * 日志等级,从e到v依次为1到5，若输出全关则设置0
     * out等同i，err等同e
     */
    final static int LEVEL = 5;
    /**
     * 每次日志输入的最大长度,如果太大将分段输出
     */
    final static int MAX_SIZE = 200;
    //用于存储错误日志的保存地址
    public static String LOG;

    static {
        LOG = BaseApplication.getInstance().getDir("error", android.content.Context.MODE_APPEND).getAbsolutePath() + "/error.log";
    }

    public static void v(String msg, String... tags) {
        if (LEVEL < 5 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + "v_" + join("_", tags);
        if (msg.length() <= MAX_SIZE) {
            Log.v(tag, msg + "");
            return;
        }
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.v(tag, data.get(i));
        }
    }

    public static void d(String msg, String... tags) {
        if (LEVEL < 4 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + "d_" + join("_", tags);
        if (msg.length() <= MAX_SIZE) {
            Log.d(tag, msg + "");
            return;
        }
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.d(tag, data.get(i));
        }
    }

    public static void i(String msg, String... tags) {
        if (LEVEL < 3 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + "i_" + join("_", tags);
        if (msg.length() <= MAX_SIZE) {
            Log.i(tag, msg + "");
            return;
        }
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.i(tag, data.get(i));
        }
    }

    public static void w(String msg, String... tags) {
        if (LEVEL < 2 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + "w_" + join("_", tags);
        if (msg.length() <= MAX_SIZE) {
            Log.w(tag, msg + "");
            return;
        }
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.w(tag, data.get(i));
        }
    }

    public static void e(String msg, String... tags) {
        if (LEVEL < 1 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + "e_" + join("_", tags);
        if (msg.length() <= MAX_SIZE) {
            Log.e(tag, msg + "");
            return;
        }
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.e(tag, data.get(i));
        }
    }

    public static void e(String msg, Exception e, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        if ("".equals(msg) && e == null) {
            return;
        }
        String tag = COMMON_TAG + "e_" + join("_", tags);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.e(tag, data.get(i), e);
        }
    }

    public static void out(String msg, String... tags) {
        if (LEVEL < 3 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        System.out.println(tag + "," + msg);
    }

    public static void err(String msg, String... tags) {
        if (LEVEL < 1 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        System.err.println(tag + "," + msg);
    }

    public static void vv(String[] msgs, String... tags) {
        if (LEVEL < 5 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + "v_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.v(tag, data.get(i));
        }
    }

    public static void dd(String[] msgs, String... tags) {
        if (LEVEL < 4 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + "d_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.d(tag, data.get(i));
        }
    }

    public static void ii(String[] msgs, String... tags) {
        if (LEVEL < 3 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + "i_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.i(tag, data.get(i));
        }
    }

    public static void ww(String[] msgs, String... tags) {
        if (LEVEL < 2 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + "w_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.w(tag, data.get(i));
        }
    }

    public static void ee(String[] msgs, String... tags) {
        if (LEVEL < 1 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + "e_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.e(tag, data.get(i));
        }
    }

    public static void ee(String[] msgs, Exception e, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        if ((msgs == null || msgs.length == 0) && e == null) {
            return;
        }
        String tag = COMMON_TAG + "e_" + join("_", tags);
        String msg = join("_", msgs);
        List<String> data = split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.e(tag, data.get(i));
        }
    }

    public static void outt(String[] msgs, String... tags) {
        if (LEVEL < 3 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        String msg = join("_", msgs);
        System.out.println(tag + "," + msg);
    }

    public static void errr(String[] msgs, String... tags) {
        if (LEVEL < 1 || msgs == null || msgs.length == 0) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        String msg = join("_", msgs);
        System.err.println(tag + "," + msg);
    }


    public static void ew(String msg, String... tags) {
        if (LEVEL < 1 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        Log.e(tag, msg);
        //获取当前时间并格式化，不适用DateUtil是为了降低耦合性
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String currentDate = sdf.format(new Date());
        //将错误信息写入错误日志中
        final String text = String.format("%s   %s  %s\n", currentDate, tag, msg);
        new IcoThread() {
            @Override
            public void run() {
                try {
                    writeFile(text, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void ew(String msg, Exception e, String... tags) {
        if (LEVEL < 1 || TextUtils.isEmpty(msg)) {
            return;
        }
        String tag = COMMON_TAG + join("_", tags);
        Log.e(tag, msg);
        //获取当前时间并格式化，不适用DateUtil是为了降低耦合性
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String currentDate = sdf.format(new Date());
        //将错误信息写入错误日志中
        final String text = String.format("%s   %s  %s\n", currentDate, tag, msg);
        new IcoThread() {
            @Override
            public void run() {
                try {
                    writeFile(text, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 读取错误日志中的文本，用于开发时进行查看
     */
    public static void print() {
        //读取错误日志数据，只是为了开发方便
        try {
            List<Byte> list = readFile(new File(LOG));
            String str = bytes2Str(list);
            log.i(str, "error.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向错误日志中写入数据
     *
     * @param text
     * @param isAppend
     */
    public static void writeFile(String text, boolean isAppend) throws IOException {
        File file = new File(LOG);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file, isAppend);
            if (isAppend) {
                fw.write(text + "\n");
            } else {
                fw.write(text + "\n");
            }
            fw.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
        log.out("已写入" + LOG);
    }

    /**
     * 向错误日志中写入数据
     *
     * @param text
     * @param isAppend
     */
    public static void writeFile(String text, Exception e, boolean isAppend) throws IOException {
        File file = new File(LOG);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(file, isAppend);
            if (isAppend) {
                fw.write(text + "\n");
            } else {
                fw.write(text + "\n");
            }
            fw.flush();
            pw = new PrintWriter(fw);
            e.printStackTrace(pw);
        } catch (FileNotFoundException e1) {
            throw e1;
        } finally {
            SafeCloseUtil.close(fw);
            SafeCloseUtil.close(pw);

        }
        log.out("已写入" + LOG);
    }

    /**
     * 将一个字符串数组根据某个字符串连接
     *
     * @param str   要插入的字符串
     * @param texts 要被拼接的字符串数组,如果传入null或者空数组，则将返回空字符串
     * @return
     */
    public static String join(String str, String... texts) {
        if (texts == null || texts.length == 0) return "";
        if (texts.length == 1) return texts[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            String tmp = texts[i];
            sb.append(tmp);
            if (i < texts.length - 1) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串根据指定长度进行分割
     *
     * @param str  要分割的字符串，如果传入的是个null值，则将拼接 空字符串 添加到集合中进行返回
     * @param size 指定的长度，分割的每个部分保证不大于这个长度
     * @return List(String) 返回一个集合，集合必定不为空并且至少有一个数据
     */
    @NonNull
    public static List<String> split(String str, int size) {
        List<String> data = new ArrayList<>();
        if (TextUtils.isEmpty(str) || str.length() <= size) {
            data.add(str + "");
            return data;
        }
        while (true) {
            if (str.length() > size) {
                data.add(str.substring(0, size));
            } else {
                data.add(str);
                break;
            }
            str = str.substring(size);
        }
        return data;
    }

    /**
     * 读取文件
     *
     * @param file
     */
    public static List<Byte> readFile(File file) throws IOException {
        List<Byte> list = new ArrayList<Byte>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            while (true) {
                byte[] buffer = new byte[1024 * 4 * 4];
                int len = fileInputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                for (int i = 0; i < len; i++) {
                    List<Byte> _list = Arrays.asList(buffer[i]);
                    list.add(_list.get(0));
                }
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return list;
    }

    /**
     * 将byte集合转换为字符串
     *
     * @param list
     * @return
     * @throws {@link UnsupportedEncodingException}
     */
    public static String bytes2Str(List<Byte> list) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[(list.size() > Integer.MAX_VALUE ? Integer.MAX_VALUE : list.size())];
        for (int i = 0, j = 0; i < list.size(); i++, j++) {
            buffer[j] = list.get(i);
            if (j == buffer.length - 1) {
                sb.append(new String(buffer, "UTF-8"));
                buffer = new byte[(list.size() > Integer.MAX_VALUE ? Integer.MAX_VALUE : list.size())];
                j = -1;
            }
        }
        String str = sb.toString();
        return str;
    }
}
