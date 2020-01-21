package ico.ico.helper;

/**
 * 类似StringHelper，主要对数值相关类型的数据进行处理
 */
public class NumberHelper {
    /** 判断字符串是否是集合中的某一个值 */
    public static boolean in(int value, int... eq) {
        for (int i = 0; i < eq.length; i++) {
            if (value == eq[i]) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否是集合中的某一个值 */
    public static boolean in(long value, long... eq) {
        for (int i = 0; i < eq.length; i++) {
            if (value == eq[i]) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否是集合中的某一个值 */
    public static boolean in(double value, double... eq) {
        for (int i = 0; i < eq.length; i++) {
            if (value == eq[i]) {
                return true;
            }
        }
        return false;
    }

    /** 判断字符串是否是集合中的某一个值 */
    public static boolean in(float value, float... eq) {
        for (int i = 0; i < eq.length; i++) {
            if (value == eq[i]) {
                return true;
            }
        }
        return false;
    }
}
