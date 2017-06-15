package com.service.boot.utils.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class NumberUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberUtils.class);

    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        try {
            String trimmed = StringUtils.trimAllWhitespace(text);
            if (Byte.class == targetClass) {
                return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
            } else if (Short.class == targetClass) {
                return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
            } else if (Integer.class == targetClass) {
                return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
            } else if (Long.class == targetClass) {
                return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
            } else if (BigInteger.class == targetClass) {
                return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
            } else if (Float.class == targetClass) {
                return (T) Float.valueOf(trimmed);
            } else if (Double.class == targetClass) {
                return (T) Double.valueOf(trimmed);
            } else if (BigDecimal.class == targetClass || Number.class == targetClass) {
                return (T) new BigDecimal(trimmed);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("数字转换异常", e);
        }
        return null;
    }

    public static boolean isNumber(String string) {
        if (string == null || "".equals(string.trim())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(string).matches();
    }

    public static boolean isDouble(String string) {
        if (string == null || "".equals(string.trim())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(string).matches();
    }

    public static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    public static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }
}
