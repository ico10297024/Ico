package ico.ico.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ico.ico.util.Common;
import ico.ico.util.log;

public class StringHelper {
    /** 判断字符串是否为null或者"" */
    public static boolean isEmpty(String value) {
        return null == value || "".equals(value.trim());
    }

    /** 判断字符串是否为null，""，或者长度大于0全部有空格组成 */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
            return true;
        for (int i = 0; i < strLen; i++)
            if (!Character.isWhitespace(str.charAt(i)))
                return false;

        return true;
    }

    /** !{@link #isEmpty(String)} */
    public static boolean isNotEmpty(String value) {
        return null != value && !"".equals(value.trim());
    }

    /** !{@link #isBlank(String)} */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /** 判断字符串是否是集合中的某一个值 */
    public static boolean in(String value, String... eq) {
        if (value == null || eq == null) {
            return false;
        }
        for (int i = 0; i < eq.length; i++) {
            if (value.equals(eq[i])) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否是集合中的某一个值,忽略大小写 */
    public static boolean inIgnoreCase(String value, String... eq) {
        if (value == null || eq == null) {
            return false;
        }
        for (int i = 0; i < eq.length; i++) {
            if (value.equalsIgnoreCase(eq[i])) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否包含集合中的某一个值 */
    public static boolean contains(String value, String... eq) {
        if (value == null || eq == null) {
            return false;
        }
        for (int i = 0; i < eq.length; i++) {
            if (value.contains(eq[i])) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否以给定集合中的字符串作结尾 */
    public static boolean startsWith(String value, String... eq) {
        if (value == null || eq == null) {
            return false;
        }
        for (int i = 0; i < eq.length; i++) {
            if (value.startsWith(eq[i])) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否以给定集合中的字符串作结尾 */
    public static boolean endsWith(String value, String... eq) {
        if (value == null || eq == null) {
            return false;
        }
        for (int i = 0; i < eq.length; i++) {
            if (value.endsWith(eq[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将字符串转化为MD5
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encodeByMd5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());
        return Common.bytes2Int16("", digest);
    }

    /**
     * 将多个字节数组进行拼接
     *
     * @param buffer
     * @return
     */
    public static byte[] fit(byte[]... buffer) {
        //计算数据总长度
        int length = 0;
        for (int i = 0; i < buffer.length; i++) {
            length += buffer[i].length;
        }
        byte[] datas = new byte[length];
        //依次进行copy
        int sp = 0;//起始存放位置
        for (int i = 0; i < buffer.length; i++) {
            System.arraycopy(buffer[i], 0, datas, sp, buffer[i].length);
            sp += buffer[i].length;
        }
        return datas;
    }

    /**
     * 将一个字符串根据指定长度进行分割
     * <p>
     * 为了解耦合，这个函数有其他分支，以下列出分支
     * <p>
     * {@link log#split(String, int)}
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

    /** 将字符串中第一个匹配到的数值进行格式化，添加，和保留小数点两位（不足两位+0） */
    public static String formatAmount(String str) {
        //通过正则表达式把数字部分截取出来，处理完成后通过替换的方式再放回去
        Pattern pattern = Pattern.compile("(\\d+[.]{1}\\d+)|([.]{1}\\d+)|(\\d+)");
        Matcher mat = pattern.matcher(str);
        if (!mat.find()) {
            return str;
        }
        String matchStr = mat.group();
        //通过.进行分隔
        String[] splitMatchStr = matchStr.split("\\.");
        String zheng = "";//整数部分
        String xiaoshu = "";//小数部分
        /* 处理整数部分 */
        if (splitMatchStr[0].length() <= 3) {
            zheng = splitMatchStr[0];
        } else {
            int yu = splitMatchStr[0].length() % 3;
            if (yu == 0) {
                zheng = insert(splitMatchStr[0], ",", 3);
            } else {
                zheng = splitMatchStr[0].substring(0, yu) + "," + insert(splitMatchStr[0].substring(yu, splitMatchStr[0].length()), ",", 3);
            }
        }
        if (StringHelper.isBlank(zheng)) {
            zheng = "0";
        }
        /* 处理小数部分 */
        if (splitMatchStr.length == 1) {
            xiaoshu = "00";
        } else {
            if (splitMatchStr[1].length() <= 2) {
                xiaoshu = splitMatchStr[1];
            } else {
                xiaoshu = splitMatchStr[1].substring(0, 2);
            }
        }
        if (xiaoshu.length() == 1) {
            xiaoshu += "0";
        }
        String result = zheng + "." + xiaoshu;
        return str.replace(matchStr, result);
    }

    /** 将比例字符串中匹配到的所有数值进行格式化，只保留两位小数点（补0）,mapRate表示是否需要在每一个数值后面智能添加百分号 */
    public static String formatRateAll(String str, boolean mapRate) {
        if (StringHelper.isContainChinese(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        //通过正则表达式把数字部分截取出来，处理完成后通过替换的方式再放回去
        Pattern pattern = Pattern.compile("((\\d+[.]{1}\\d+)|([.]{1}\\d+)|(\\d+)){1}[%]?");
        Matcher mat = pattern.matcher(str);
        //记录偏差值
        int offset = 0;
        while (mat.find()) {
            String matchStr = mat.group();
            String result = StringHelper.formatRate(matchStr);
            if (mapRate) {
                result = StringHelper.mapRate(result);
            }
            sb.replace(mat.start() + offset, mat.end() + offset, result);
            offset += result.length() - matchStr.length();
        }
        return sb.toString();
    }

    /** 提取数字 */
    public static String pickNumber(String origin) {
        StringBuilder sb = new StringBuilder();
        //通过正则表达式把数字部分截取出来，处理完成后通过替换的方式再放回去
        Pattern pattern = Pattern.compile("\\d+");
        Matcher mat = pattern.matcher(origin);
        while (mat.find()) {
            String matchStr = mat.group();
            sb.append(matchStr);
        }
        return sb.toString();
    }

    /** 将比例字符串中第一个匹配到的数值进行格式化，只保留两位小数点（补0） */
    public static String formatRate(String str) {
        //通过正则表达式把数字部分截取出来，处理完成后通过替换的方式再放回去
        Pattern pattern = Pattern.compile("(\\d+[.]{1}\\d+)|([.]{1}\\d+)|(\\d+)");
        Matcher mat = pattern.matcher(str);
        if (!mat.find()) {
            return str;
        }
        String matchStr = mat.group();
        //通过.进行分隔
        String[] splitMatchStr = matchStr.split("\\.");
        String zheng = "";//整数部分
        String xiaoshu = "";//小数部分
        if (splitMatchStr[0].length() == 0) {
            zheng = "0";
        } else {
            zheng = splitMatchStr[0];
        }
        /* 处理小数部分 */
        if (splitMatchStr.length == 1) {
            xiaoshu = "00";
        } else {
            if (splitMatchStr[1].length() < 2) {
                xiaoshu = splitMatchStr[1] + "0";
            } else {
                xiaoshu = splitMatchStr[1].substring(0, 2);
            }
        }
        String result = zheng + "." + xiaoshu;
        return str.replace(matchStr, result);
    }

    /** 对字符串末尾进行检查，没有百分号添加百分号后返回 */
    public static String mapRate(String origin) {
        if (!origin.endsWith("%")) {
            return origin + "%";
        }
        return origin;
    }

    /** 对字符串末尾进行检查，若无则添加 */
    public static String mapEnd(String origin, String str) {
        if (!origin.endsWith(str)) {
            return origin.concat(str);
        }
        return origin;
    }

    /**
     * 每隔几个字符插入一个指定字符
     *
     * @param src      原字符串
     * @param insert   要插入的字符串
     * @param interval 间隔字符数量
     * @return
     */
    public static String insert(String src, String insert, int interval) {
        StringBuffer s1 = new StringBuffer(src);
        int index;
        for (index = interval; index < s1.length(); index += (interval + 1)) {
            s1.insert(index, insert);
        }
        return s1.toString();
    }

    /**
     * 将一个字符串数组根据某个字符串连接
     * <p>
     * 为了解耦合，这个函数有其他分支，以下列出分支
     * <p>
     * {@link log#join(String, String...)}
     *
     * @param str   要插入的字符串
     * @param texts 要被拼接的字符串数组,如果传入null或者空数组，则将返回空字符串
     * @return
     */
    public static String join(String str, String... texts) {
        //java1.8可以通过String.join来实现，不过效率上比本方法要慢
//        try {
//            Class.forName("java.util.StringJoiner");
//            log.w("===1111");
//            return String.join(str,texts);
//        } catch (ClassNotFoundException e) {
//            log.w("===2222");
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
//        }
    }

    /**
     * 将一个字符串集合根据某个字符串连接
     *
     * @param str   连接的字符串
     * @param texts 用于拼接的字符串集合，使用list
     * @return String 拼接后的字符串
     */
    public static String join(String str, List<String> texts) {
        StringBuilder sb = new StringBuilder();
        if (texts instanceof RandomAccess) {
            for (int i = 0; i < texts.size(); i++) {
                String tmp = texts.get(i);
                sb.append(tmp);
                if (i < texts.size() - 1) {
                    sb.append(str);
                }
            }
        } else {
            for (String tmp : texts) {
                sb.append(tmp);
                sb.append(str);
            }
            sb.delete(sb.length() - str.length(), sb.length());
        }
        return sb.toString();
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 过滤掉中文
     *
     * @param str 待过滤中文的字符串
     * @return 过滤掉中文后字符串
     */
    public static String filterChinese(String str) {
        // 用于返回结果
        String result = str;
        boolean flag = isContainChinese(str);
        if (flag) {// 包含中文
            // 用于拼接过滤中文后的字符
            StringBuffer sb = new StringBuffer();
            // 用于校验是否为中文
            boolean flag2 = false;
            // 用于临时存储单字符
            char chinese = 0;
            // 5.去除掉文件名中的中文
            // 将字符串转换成char[]
            char[] charArray = str.toCharArray();
            // 过滤到中文及中文字符
            for (int i = 0; i < charArray.length; i++) {
                chinese = charArray[i];
                flag2 = isChinese(chinese);
                if (!flag2) {// 不是中日韩文字及标点符号
                    sb.append(chinese);
                }
            }
            result = sb.toString();
        }
        return result;
    }


    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证字符串内容是否包含下列非法字符<br>
     * `~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆
     *
     * @param content 字符串内容
     * @return 't'代表不包含非法字符，otherwise代表包含非法字符。
     */
    public static char validateLegalString(String content) {
        String illegal = "`~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆";
        char isLegalChar = 't';
        L1:
        for (int i = 0; i < content.length(); i++) {
            for (int j = 0; j < illegal.length(); j++) {
                if (content.charAt(i) == illegal.charAt(j)) {
                    isLegalChar = content.charAt(i);
                    break L1;
                }
            }
        }
        return isLegalChar;
    }

    /**
     * 验证是否是汉字或者0-9、a-z、A-Z
     *
     * @param c 被验证的char
     * @return true代表符合条件
     */
    public static boolean isRightChar(char c) {
        return isChinese(c) || isWord(c);
    }

    /**
     * 校验某个字符是否是a-z、A-Z、_、0-9
     *
     * @param c 被校验的字符
     * @return true代表符合条件
     */
    public static boolean isWord(char c) {
        String regEx = "[\\w]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher("" + c);
        return m.matches();
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 校验String是否全是中文
     *
     * @param name 被校验的字符串
     * @return true代表全是汉字
     */
    public static boolean isChineseAll(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }
}
