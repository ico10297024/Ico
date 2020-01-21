/*
 * 作者		刘国山 (刘国山)
 * 开发环境	WindowsXp MyEclipse6.5 JDK1.6.0_22
 * 开发日期	2011-4-15
 */
package ico.ico.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @Description
 * @Author Lewis(lgs @ yitong.com.cn) 2014年8月13日 上午9:56:18
 * @Class ValidateTools
 * Copyright (c) 2014 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 */
public abstract class ValidateTools {

    // 正则,匹配URL
    private static final String REGEX_URL = "http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";

    // 正则,匹配全汉字
    private static final String REGEX_CHINESE_ONLY = "[\\u4e00-\\u9fa5]+";

    // 正则,匹配a-z(英文小写)
    private static final String REGEX_LOWERCASE_ONLY = "[a-z]+";

    // 正则,匹配A-Z(英文大写)
    private static final String REGEX_UPPERCASE_ONLY = "[A-Z]+";

    // 正则,匹配a-z A-Z(英文大写或小写)
    private static final String REGEX_LOWER_OR_UPPER_CASE = "[A-Za-z]+";

    // 正则,匹配0-9(数字)
    private static final String REGEX_NUM_ONLY = "[0-9]+";
    // 正则,匹配0-9和点(数字)
    private static final String REGEX_NUM_ONLYANDDIAN = "[0-9.]+";

    // 正则,匹配0和非0开头的数字
    private static final String REGEX_ZERO_OR_NOTSTARTWITHZERO = "(0|[1-9][0-9]*)";

    // 正则,只能由a-z A-Z 0-9 组成
    private static final String REGEX_AZ_AZ_NUM_DOWNLINE = "[a-zA-Z0-9]+";

    // 正则,匹配必须是英文字母开头,只能由a-z A-Z 0-9 和 下划线"_"组成
    private static final String REGEX_AZ_AZ_NUM_DOWNLINE_STARTWITHENGLISH = "[a-zA-Z][a-zA-Z0-9_]+";

    // 正则,验证是否含有特殊的字符(^%&',;=?$\")
    private static final String REGEX_HAVE_SOECIAL_CHAR = "[^^%&'=$\\x22]+";

    // 正则,匹配邮箱
    private static final String REGEX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    // 正则,匹配手机号
    private static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";


    private static boolean matcher(String string, String regex) {
        if (string == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        return m.matches();
    }

    //==============================================================

    /**
     * 是否为Url 是:返回true 否:返回false
     */
    public static boolean isUrl(String string) {
        return matcher(string, REGEX_URL);
    }

    /**
     * 验证是否只为汉字 是:返回true 否:返回false
     */
    public static boolean isChineseOnly(String string) {
        return matcher(string, REGEX_CHINESE_ONLY);
    }

    /**
     * 验证是否只为英文小写(a-z) 是:返回true 否:返回false
     */
    public static boolean isLowerCaseOnly(String string) {
        return matcher(string, REGEX_LOWERCASE_ONLY);
    }

    /**
     * 验证是否只为英文大写(A-Z) 是:返回true 否:返回false
     */
    public static boolean isUpperCaseOnly(String string) {
        return matcher(string, REGEX_UPPERCASE_ONLY);
    }

    /**
     * 验证是否只为英文(A-Z a-z) 是:返回true 否:返回false
     */
    public static boolean isLowerOrUpperCase(String string) {
        return matcher(string, REGEX_LOWER_OR_UPPER_CASE);
    }

    /**
     * 验证是否为Email 是:返回true 否:返回false
     */
    public static boolean isEmail(String string) {
        return matcher(string, REGEX_EMAIL);
    }

    /**
     * 验证是否为身份证(匹配身份证 15~18位 可以判断出生年月是否符合要求) 是:返回true 否:返回false
     */
    public static boolean isIDCard(String string) {
        return IDCardValidateTool.validateCard(string);
    }

    /**
     * 验证是否只为数字(0~9) 是:返回true 否:返回false
     */
    public static boolean isNumOnly(String string) {
        return matcher(string, REGEX_NUM_ONLY);
    }

    /**
     * 验证版本号输入是否规则 是:返回true 否:返回false
     */
    public static boolean isNumDianOnly(String string) {
        return matcher(string, REGEX_NUM_ONLYANDDIAN);
    }

    /**
     * 验证是否为0或者不为0开头的数字 是:返回true 否:返回false
     */
    public static boolean isZeroOrNotStartWithZeroNum(String string) {
        return matcher(string, REGEX_ZERO_OR_NOTSTARTWITHZERO);
    }

    /**
     * 验证只能由A-Z a-z 0-9组成
     */
    public static boolean isAzazNum(String string) {
        return matcher(string, REGEX_AZ_AZ_NUM_DOWNLINE);
    }

    /**
     * 验证是否为由A-Z a-z 0-9 和 下划线"_"组成并且是字母开头的字符串 是:返回true 否:返回false
     */
    public static boolean isAzazNumDownLineAndStartWithEnglish(String string) {
        return matcher(string, REGEX_AZ_AZ_NUM_DOWNLINE_STARTWITHENGLISH);
    }

    /**
     * 验证是否含有特殊的字符(^%&',;=?$\") 是:返回true 否:返回false
     */
    public static boolean isHasSoecialChar(String string) {
        return matcher(string, REGEX_HAVE_SOECIAL_CHAR);
    }

    /**
     * 验证是否匹配手机号 是:返回true 否:返回false
     */
    public static boolean isMobile(String string) {
        return matcher(string, REGEX_MOBILE);
    }

}
