package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * @author YangJ
 */
open class HeaderView: LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置头部控件文本内容
     * @param text 参数为文本内容
     */
    fun setLabel(text: String) {

    }

    /**
     * 设置刷新状态
     * @param state 参数为RefreshLayout刷新状态，例如：下拉刷新、松开刷新、正在加载
     */
    fun setRefreshState(state: Int) {

    }

    fun setContentView(layoutResId: Int) {
        val view = LayoutInflater.from(context).inflate(layoutResId, this)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

}