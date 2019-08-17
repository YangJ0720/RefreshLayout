package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import yangj.refreshlayout.R
import yangj.refreshlayout.RefreshLayout

/**
 * @author YangJ
 */
abstract class HeaderView : FrameLayout, Special {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置刷新状态
     * @param state 参数为RefreshLayout刷新状态，例如：下拉刷新、松开刷新、正在加载
     */
    override fun setRefreshState(state: Int) {

    }

    abstract fun notifyHeaderScrollChanged(distance: Int)

}