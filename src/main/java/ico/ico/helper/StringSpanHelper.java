package ico.ico.helper;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Android可以通过SpannableStringBuilder来对一段文字中的某一部分做一些变换，让不同部分的文字样式不一样
 */
public class StringSpanHelper {

    /**
     * 设置指定字符串的文字大小变换
     *
     * @param proportion 将指定部分的文字大小缩放proportion
     * @param str        指定部分文字的字符串
     * @param origin     原始数据
     * @return
     */
    public static SpannableStringBuilder relativeAll(float proportion, String str, String origin) {
        SpannableStringBuilder INCOME_RATE = new SpannableStringBuilder(origin);
        for (int i = 0; true; ) {
            int index = origin.indexOf(str, i);
            if (index == -1) {
                break;
            }
            INCOME_RATE.setSpan(new RelativeSizeSpan(proportion), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            i = index + str.length();
        }
        return INCOME_RATE;
    }

    /**
     * 设置指定字符串的文字大小变换，可以对SpannableStringBuilder做二次变换
     *
     * @param proportion 将指定部分的文字大小缩放proportion
     * @param str        指定部分文字的字符串
     * @param originSpan 传入span对象，对其做二次变换后再返回
     * @return
     */
    public static SpannableStringBuilder relativeAll(float proportion, String str, SpannableStringBuilder originSpan) {
        String origin = originSpan.toString();
        for (int i = 0; true; ) {
            int index = origin.indexOf(str, i);
            if (index == -1) {
                break;
            }
            originSpan.setSpan(new RelativeSizeSpan(proportion), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            i = index + str.length();
        }
        return originSpan;
    }


    /**
     * 设置指定字符串的文字大小变换
     *
     * @param color  将指定部分的文字颜色改为指定颜色
     * @param str    指定部分文字的字符串
     * @param origin 原始数据
     * @return
     */
    public static SpannableStringBuilder foregroundColorAll(int color, String str, String origin) {
        SpannableStringBuilder INCOME_RATE = new SpannableStringBuilder(origin);
        for (int i = 0; true; ) {
            int index = origin.indexOf(str, i);
            if (index == -1) {
                break;
            }
            INCOME_RATE.setSpan(new ForegroundColorSpan(color), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            i = index + str.length();
        }
        return INCOME_RATE;
    }

    /**
     * 设置指定字符串的文字大小变换，可以对SpannableStringBuilder做二次变换
     *
     * @param color      将指定部分的文字颜色改为指定颜色
     * @param str        指定部分文字的字符串
     * @param originSpan 传入span对象，对其做二次变换后再返回
     * @return
     */
    public static SpannableStringBuilder foregroundColorAll(int color, String str, SpannableStringBuilder originSpan) {
        String origin = originSpan.toString();
        for (int i = 0; true; ) {
            int index = origin.indexOf(str, i);
            if (index == -1) {
                break;
            }
            originSpan.setSpan(new ForegroundColorSpan(color), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            i = index + str.length();
        }
        return originSpan;
    }

    /**
     * 设置指定字符串的文字大小变换
     *
     * @param color  将指定部分的文字颜色改为指定颜色
     * @param str    指定部分文字的字符串
     * @param origin 原始数据
     * @return
     */
    public static SpannableStringBuilder foregroundColorFirst(int color, String str, String origin) {
        SpannableStringBuilder INCOME_RATE = new SpannableStringBuilder(origin);
        int index = origin.indexOf(str);
        if (index == -1) {
            return INCOME_RATE;
        }
        INCOME_RATE.setSpan(new ForegroundColorSpan(color), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return INCOME_RATE;
    }

    /**
     * 设置指定字符串的文字大小变换，可以对SpannableStringBuilder做二次变换
     *
     * @param color      将指定部分的文字颜色改为指定颜色
     * @param str        指定部分文字的字符串
     * @param originSpan 传入span对象，对其做二次变换后再返回
     * @return
     */
    public static SpannableStringBuilder foregroundColorFirst(int color, String str, SpannableStringBuilder originSpan) {
        String origin = originSpan.toString();
        int index = origin.indexOf(str);
        if (index == -1) {
            return originSpan;
        }
        originSpan.setSpan(new ForegroundColorSpan(color), index, index + str.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return originSpan;
    }
}
