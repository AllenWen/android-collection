# android-collection

自定义小组件、实用代码集锦

0. **Utils(实用技巧)**

* 步长约束方法: `fun BigDecimal.step(step: BigDecimal)`
* 安全地把String转BigDecimal: `fun String?.safeBigDecimal()`
* 任意获取sublist，无需担心index溢出: `fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int)`

1. **输入过滤器**

* 数字输入过滤器，可设置最大值、最小值、小数位数
* <img src="/img/Screenshot_input_filter.png" width="35%" height="35%"/>

2. **常用图表**

* 走势图、饼图、仪表盘
* <img src="/img/Screenshot_chart.png" width="35%" height="35%"/>

3. **带后缀文本**

* 文本默认带可点击的后缀文字，可控制是否一直显示后缀
* <img src="/img/Screenshot_suffix_text_view.png" width="35%" height="35%"/>

4. **局部变暗的弹窗**

* 可以局部变暗的弹窗，上下左右均可
* <img src="/img/Screenshot_popup.png" width="35%" height="35%"/>