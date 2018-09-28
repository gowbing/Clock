package com.miracle.clock.utils.normal;

import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数校验工具类
 *
 * @author liyl <liyl@njhhsoft.com>
 *         <p/>
 *         2013-9-4 上午10:00:53
 */
public class ValidationUtil {

    /**
     * E-Mail正则表达式
     */
    private static String emailRegex = "^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$";

    /**
     * URL正则表达式
     */
    private static String urlRegex = "^(https?|s?ftp):\\/\\/(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?$";

    /**
     * 验证手机号码的正则表达式
     */
    private static String mobileNumberRegex = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    /**
     * 中文、数字、字母、下划线
     */
    private static String chineseRegex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]$";


    /**
     * 校验是否为正确的email地址
     *
     * @param email 邮箱地址
     * @return
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(emailRegex, email);
    }

    /**
     * 校验是否为正确的手机号码
     *
     * @param String 手机号码
     * @return
     */
    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 校验是否为正确的URL地址
     *
     * @param url 标准URL地址
     * @return
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(urlRegex, url);
    }

    /**
     * 校验文本长度是否大于等于最小长度值
     *
     * @param str       待校验文本
     * @param minLength 最小长度
     * @return
     */
    public static boolean checkMinLength(String str, int minLength) {
        if (StringUtils.notEmpty(str) && str.length() >= minLength) {
            return true;
        }
        return false;
    }

    /**
     * 校验文本长度是否大于等于最小长度值
     *
     * @param editText  文本编辑视图
     * @param minLength 最小长度
     * @return
     */
    public static boolean checkMinLength(EditText editText, int minLength) {
        if (null != editText) {
            String str = editText.getText().toString();
            return checkMinLength(str, minLength);
        }
        return false;
    }

    /**
     * 校验文本长度是否大于等于最小长度值
     *
     * @param textView  文本视图
     * @param minLength 最小长度
     * @return
     */
    public static boolean checkMinLength(TextView textView, int minLength) {
        if (null != textView) {
            String str = textView.getText().toString();
            return checkMinLength(str, minLength);
        }
        return false;
    }

    /**
     * 检查用户名有效性
     *
     * @param account
     * @return
     */
    public static boolean checkValidaAccount(String account) {
        if (StringUtils.notEmpty(account)) {
            return Pattern.matches(chineseRegex, account);
        }
        return false;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        boolean success = false;
        String input = "liyl@njhhsoft.com";
        if (checkValidaAccount(input)) {
            success = true;
        }

        if (success) {
            System.out.println("校验成功");
        } else {
            System.out.println("校验失败");
        }

    }


}
