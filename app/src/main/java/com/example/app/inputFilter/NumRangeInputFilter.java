package com.example.app.inputFilter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * 数字输入过滤器，可设置最大值、最小值、小数位数
 */
public class NumRangeInputFilter implements InputFilter {
    private static final String REGEX = "([0-9]|\\.)*";
    private static final String POINTER = ".";
    private static final String ZERO_ZERO = "00";

    private double maxValue = 1.0;
    private double minValue = 0.0;
    private int precision = 4;

    /**
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source长度-1
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend   原内容终点坐标，一般为dest长度-1
     * @return 输入内容
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();
        // 删除
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        // 拼成字符串
        String temp = destText.substring(0, dstart) + sourceText.substring(start, end) + destText.substring(dend, dest.length());
        // 纯数字加小数点
        if (!temp.matches(REGEX)) {
            return "";
        }
        // 小数点的情况
        if (temp.contains(POINTER)) {
            if (temp.startsWith(POINTER)) {
                return "";
            }
            // 不止一个小数点
            if (temp.indexOf(POINTER) != temp.lastIndexOf(POINTER)) {
                return "";
            }
        }
        double tempSum = Double.parseDouble(temp);
        if (tempSum > maxValue) {
            // 超出最大值
            return "";
        }
        String[] minSplit = String.valueOf(BigDecimal.valueOf(minValue).toPlainString()).split("\\.");
        int minInt = Integer.valueOf(minSplit[0]);
        double minDecimal = Double.valueOf("0." + minSplit[1]);

        if (temp.contains(POINTER)) {
            String[] tempSplit = temp.split("\\.");
            int tempInteger = Integer.valueOf(tempSplit[0]);
            if (tempInteger < minInt) {
                return "";
            }
            if (tempSplit.length > 1) {
                double tempDecimal = Double.valueOf("0." + tempSplit[1]);
                if (tempInteger == minInt && tempSplit[1].length() == minSplit[1].length() && tempDecimal < minDecimal) {
                    return "";
                }
            }
        }
        // 有小数点的情况下
        if (temp.contains(POINTER)) {
            // 限制小数位
            if (!temp.endsWith(POINTER) && temp.split("\\.")[1].length() > precision) {
                return "";
            }
        } else if (temp.startsWith(POINTER) || temp.startsWith(ZERO_ZERO)) {
            // 首位只能有一个0
            return "";
        }

        return source;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
