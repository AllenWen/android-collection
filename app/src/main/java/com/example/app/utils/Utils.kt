package com.example.app.utils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * 步长约束方法，向上向下均可，这里展示向下的
 * 1、步长注意判0; 2、此方式可以约束任意步长
 * @param step: 步长
 */
fun BigDecimal.step(step: BigDecimal): BigDecimal {
    return this.subtract(this.remainder(step))
}

/**
 * 安全地把String转BigDecimal
 */
fun String?.safeBigDecimal(): BigDecimal? {
    return if (isNullOrEmpty()) {
        null
    } else {
        try {
            BigDecimal(this)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 任意获取sublist，无需担心index溢出
 */
fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
    this.subList(fromIndex.coerceAtLeast(0), toIndex.coerceAtMost(this.size))

