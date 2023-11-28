package com.engine.utils;

import com.alibaba.fastjson.JSONException;

public final class JsonValidator {

    /**
     * 数组指针
     */
    private static int index;
    /**
     * 字符串
     */
    private static String value;
    /**
     * 指针当前字符
     */
    private static char curchar;

    /**
     * 工具类非公有构造函数
     */
    private JsonValidator() {

    }

    /**
     * <B>方法名称：</B>JSON格式校验<BR>
     * <B>概要说明：</B>JSON 格式 在 [ ] { } , 字符前后可加注释，以// #开头、以\n \r 结尾，/* *\/ 三种注释格式
     * Array [value .... value] Object {string : value,.....string:value}
     * string不可包含双引号以及控制字符 value 形式有 1.string 2.number 数字格式小数点后可不跟数字 e/E和+/-
     * 后必须有数字 3/4.array/object格式与自身相同 5.true/false/null array 中可以存在 [,,,]
     * object中不可存在{,,}或{"num1","num2":,} 由于只需正向遍历一遍字符数组，所以校验速率非常之快！
     * <p>
     * 支持8000层+JSON嵌套,9000会引发stackoverflow 【String test =
     * "{TOKEN,TOKEN,TOKEN,TOKEN}"; for (int i = 0; i < 8000; i++) { test =
     * test.replaceAll("TOKEN",
     * "\"TEST\":{TOKEN,\"中小客流量\":\"9650\",\"大客车流量\":\"280\",\"小货车流量\":\"663\",\"中货车流量\":\"354\",\"大货车流量\":\"329\",\"特大货流量\":\"554\",\"集装箱流量\":\"261\",\"摩托车流量\":\"0\",\"拖拉机流量\":\"0\",\"客车流量\":\"9930\",\"货车流量\":\"2161\",\"机动车流量\":\"12091\"}");
     * } test = test.replaceAll("TOKEN",
     * "\"TEST\":{\"中小客流量\":\"9650\",\"大客车流量\":\"280\",\"小货车流量\":\"663\",\"中货车流量\":\"354\",\"大货车流量\":\"329\",\"特大货流量\":\"554\",\"集装箱流量\":\"261\",\"摩托车流量\":\"0\",\"拖拉机流量\":\"0\",\"客车流量\":\"9930\",\"货车流量\":\"2161\",\"机动车流量\":\"12091\"}");】
     * <p>
     * string.copyOf()上限限制校验大小，本地测试上限约330M数据，校验时间1196ms【 StringBuffer test = new
     * StringBuffer("{"); for (int i = 0; i < 622500; i++) { test.append(
     * "\"TEST\":{\"中小客流量\":\"9650\",\"大客车流量\":\"280\",\"小货车流量\":\"663\",\"中货车流量\":\"354\",\"大货车流量\":\"329\",\"特大货流量\":\"554\",\"集装箱流量\":\"261\",\"摩托车流量\":\"0\",\"拖拉机流量\":\"0\",\"客车流量\":\"9930\",\"货车流量\":\"2161\",\"机动车流量\":\"12091\"},");
     * } test.append(
     * "\"TEST\":{\"中小客流量\":\"9650\",\"大客车流量\":\"280\",\"小货车流量\":\"663\",\"中货车流量\":\"354\",\"大货车流量\":\"329\",\"特大货流量\":\"554\",\"集装箱流量\":\"261\",\"摩托车流量\":\"0\",\"拖拉机流量\":\"0\",\"客车流量\":\"9930\",\"货车流量\":\"2161\",\"机动车流量\":\"12091\"}}");】
     * <BR>
     *
     * @param rawValue 字符串参数
     * @return boolean 是否是JSON
     */
    public static boolean isJSON(String rawValue) {
        try {
            index = 0;
            value = rawValue;
            switch (nextClean()) {
                case '[':
                    if (nextClean() == ']') {
                        return true;
                    }
                    back();
                    return validateArray();
                case '{':
                    if (nextClean() == '}') {
                        return true;
                    }
                    back();
                    return validateObject();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <B>方法名称：</B>遍历至下一个有效实义字符<BR>
     * <B>概要说明：</B>使用此方法会跳过接下来的注释，若遇到注释外的控制字符将会抛错判定不是JSON格式<BR>
     * *
     *
     * @return char 下一个有效实义字符 char<=' ' char!=127
     * @throws JSONException 自定义JSON异常
     */
    public static char nextClean() throws JSONException {
        skipComment:
        do {
            next();
            if (curchar == '/') { // 跳过//类型与/*类型注释 遇回车或者null为注释内容结束
                switch (next()) {
                    case 47: // '/'
                        do {
                            curchar = next();
                        } while (curchar != '\n' && curchar != '\r' && curchar != 0);
                        continue;
                    case 42: // '*'
                        do {
                            do {
                                next();
                                if (curchar == 0) {
                                    throw syntaxError("Unclosed comment");
                                }
                            } while (curchar != '*');
                            if (next() == '/') {
                                continue skipComment;
                            }
                            back();
                        } while (true);
                }
                back();
                return '/';
            }
            if (curchar != '#') { //跳过#类型注释 遇回车或者null为注释内容结束
                break;
            }
            do {
                next();
            } while (curchar != '\n' && curchar != '\r' && curchar != 0);
        } while (true);
        if (curchar != 0 && (curchar <= ' ' || curchar == 127)) {
            throw syntaxError("JSON can not contain control character!");
        }
        return curchar;
    }

    /**
     * <B>方法名称：</B>查看下一个字符<BR>
     * <B>概要说明：</B>查看下一个字符，若index不在字符范围内将返回null
     * 若字符不在0-133内也将会返回空，否则移动指针返回下一个字符<BR>
     * *
     *
     * @return char 下一个字符
     */
    public static char next() {
        if (index < 0 || index >= value.length()) {
            return '\0';
        }
        curchar = value.charAt(index);
        if (curchar <= 0) {
            return '\0';
        } else {
            index++;
            return curchar;
        }
    }

    /**
     * <B>方法名称：</B>回退上一个字符<BR>
     * <B>概要说明：</B>将指针移至上一个字符，回退一位<BR>
     * *
     */
    public static void back() { //异常在next中进行返回null
        index--;
    }

    /**
     * <B>方法名称：</B>抛出自定义JSON异常<BR>
     * <B>概要说明：</B><BR>
     * *
     *
     * @param message 异常自定义信息
     * @return JSONException 自定义JSON异常
     */
    public static JSONException syntaxError(String message) {
        return new JSONException((new StringBuilder(String.valueOf(message))).toString());
    }

    /**
     * <B>方法名称：</B>校验JSONArray格式<BR>
     * <B>概要说明：</B><BR>
     * *
     *
     * @return boolean 是否是JSONArray
     * @throws JSONException 自定义JSON异常
     */
    public static boolean validateArray() throws JSONException {
        do {
            //入口为合法 [ array 起点
            nextClean(); //下一位有效字符，跳过注释
            if (curchar == ']') { //空array 直接闭合返回
                return true;
            } else if (curchar == ',') { //null
                continue;
            } else if (curchar == '"') { //String
                validateString();
            } else if (curchar == '-' || (curchar >= 48 && curchar <= 57)) { // number
                validateNumber();
            } else if (curchar == '{') { // object
                if (!validateObject()) { //递归校验
                    return false;
                }
            } else if (curchar == '[') { // array
                if (!validateArray()) { //递归校验
                    return false;
                }
            } else if (curchar == 't' || curchar == 'f' || curchar == 'n') { // boolean and JSONNull
                validateBooleanAndNull();
            } else {
                return false;
            }
            switch (nextClean()) {
                case ',':
                    continue;
                case ']':
                    return true;
                default:
                    return false;
            }
        } while (true);
    }

    /**
     * <B>方法名称：</B>校验JSONObject格式<BR>
     * <B>概要说明：</B>逻辑和Array基本一样<BR>
     * *
     *
     * @return boolean 是否是JSONObject
     * @throws JSONException 自定义JSON异常
     */
    public static boolean validateObject() throws JSONException {
        do {
            nextClean();
            if (curchar == '}') {
                return true;
            } else if (curchar == '"') { //String
                validateString();
            } else {
                return false;
            }
            if (nextClean() != ':') {
                return false;
            }
            nextClean();
            if (curchar == ',') { //null
                throw syntaxError("Missing value");
            } else if (curchar == '"') { //String
                validateString();
            } else if (curchar == '-' || (curchar >= 48 && curchar <= 57)) { // number
                validateNumber();
            } else if (curchar == '{') { // object
                if (!validateObject()) {
                    return false;
                }
            } else if (curchar == '[') { // array
                if (!validateArray()) {
                    return false;
                }
            } else if (curchar == 't' || curchar == 'f' || curchar == 'n') { // boolean and JSONNull
                validateBooleanAndNull();
            } else {
                return false;
            }
            switch (nextClean()) {
                case ',':
                    continue;
                case '}':
                    return true;
                default:
                    return false;
            }
        } while (true);
    }

    /**
     * <B>方法名称：</B>校验JSON String格式<BR>
     * <B>概要说明：</B><BR>
     * *
     *
     * @throws JSONException 自定义JSON异常
     */
    public static void validateString() throws JSONException {
        do {
            curchar = next(); //JSON对字符串中的转义项有严格规定
            if (curchar == '\\') {
                if ("\"\\/bfnrtu".indexOf(next()) < 0) {
                    throw syntaxError("Invalid escape string");
                }
                if (curchar == 'u') { //校验unicode格式 后跟4位16进制 0-9 a-f A-F
                    for (int i = 0; i < 4; i++) {
                        next();
                        if (curchar < 48 || (curchar > 57 && curchar < 65) || (curchar > 70 && curchar < 97)
                                || curchar > 102) {
                            throw syntaxError("Invalid hexadecimal digits");
                        }
                    }
                }
            }
        } while (curchar >= ' ' && curchar != '"' && curchar != 127);
        if (curchar == 0) { //仅正常闭合双引号可通过
            throw syntaxError("Unclosed quot");
        } else if (curchar != '"') {
            throw syntaxError("Invalid string");
        }
    }

    /**
     * <B>方法名称：</B>校验JSON Number格式<BR>
     * <B>概要说明：</B><BR>
     * *
     *
     * @throws JSONException 自定义JSON异常
     */
    public static void validateNumber() throws JSONException {
        if (curchar == '-') { //可选负号
            curchar = next();
        }
        if (curchar > 48 && curchar <= 57) { //整数部分
            do {
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        } else if (curchar == 48) {
            curchar = next();
        } else {
            throw syntaxError("Invalid number");
        }
        if (curchar == '.') { //小数部分
            do { //.后可不跟数字 如 5. 为合法数字
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        }
        if (curchar == 'e' || curchar == 'E') { //科学计数部分
            curchar = next();
            if (curchar == '+' || curchar == '-') {
                curchar = next();
            }
            if (curchar < 48 || curchar > 57) {
                throw syntaxError("Invalid number");
            }
            do {
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        }
        back(); //指针移至数字值最后一位，取下一位即判断是,或者],或者是合法注释
    }

    /**
     * <B>方法名称：</B>校验JSON boolean/null格式<BR>
     * <B>概要说明：</B><BR>
     * *
     *
     * @throws JSONException 自定义JSON异常
     */
    public static void validateBooleanAndNull() throws JSONException {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(curchar);
            curchar = next();
        } while (curchar >= ' ' && ",]#/".indexOf(curchar) < 0 && curchar != 127);
        if (!"null".equals(sb.toString()) && !"true".equals(sb.toString()) && !"false".equals(sb.toString())) {
            throw syntaxError("Invalid boolean/null");
        }
        back();
    }
}
