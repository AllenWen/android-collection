package com.example.app.utils

import java.math.BigDecimal

/**
 * 步长约束方法，向上向下均可，这里展示向下的
 * 1、步长注意判0; 2、此方式可以约束任意步长
 * @param step: 步长
 */
fun BigDecimal.step(step: BigDecimal): BigDecimal {
    return this.subtract(this.remainder(step))
}



