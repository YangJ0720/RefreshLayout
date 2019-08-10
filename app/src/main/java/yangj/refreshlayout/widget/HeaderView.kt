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
open class HeaderView : FrameLayout, Special {

    private var mTvLabel: TextView? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {

    }

    /**
     * 设置头部控件文本内容
     * @param text 参数为文本内容
     */
    fun setLabel(text: String) {
        mTvLabel?.text = text
    }

    /**
     * 设置刷新状态
     * @param state 参数为RefreshLayout刷新状态，例如：下拉刷新、松开刷新、正在加载
     */
    override fun setRefreshState(state: Int) {
        when(state) {
            RefreshLayout.STATE_NORMAL -> setLabel(resources.getString(R.string.refresh_header))
            RefreshLayout.STATE_HEADER -> setLabel(resources.getString(R.string.loading))
            RefreshLayout.STATE_PENDING -> setLabel(resources.getString(R.string.pending))
        }
    }

    override fun notifyHeaderScrollChanged(distance: Int) {

    }

    override fun notifyFooterScrollChanged(distance: Int) {

    }

    open fun setContentView(layoutResId: Int) {
        val view = LayoutInflater.from(context).inflate(layoutResId, this)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        //
        mTvLabel = view.findViewById(R.id.tvLabel)
    }

}