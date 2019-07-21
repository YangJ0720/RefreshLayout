package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * @author YangJ
 */
abstract class FooterView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置头部控件文本内容
     * @param text 参数为文本内容
     */
    abstract fun setLabel(text: String)

    /**
     * 设置刷新状态
     * @param state 参数为RefreshLayout刷新状态，例如：上拉加载、松开刷新、正在加载
     */
    abstract fun setRefreshState(state: Int)

    abstract fun setContentView(layoutResId: Int)
}