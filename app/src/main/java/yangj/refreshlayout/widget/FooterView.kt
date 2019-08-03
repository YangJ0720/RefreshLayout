package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import yangj.refreshlayout.R
import yangj.refreshlayout.RefreshLayout

/**
 * @author YangJ
 */
open class FooterView : LinearLayout {

    protected var mView: View? = null
    private var mTvLabel: TextView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置头部控件文本内容
     * @param text 参数为文本内容
     */
    open fun setLabel(text: String) {
        mTvLabel?.text = text
    }

    /**
     * 设置刷新状态
     * @param state 参数为RefreshLayout刷新状态，例如：上拉加载、松开刷新、正在加载
     */
    fun setRefreshState(state: Int) {
        when (state) {
            RefreshLayout.STATE_NORMAL -> setLabel(resources.getString(R.string.refresh_footer))
            RefreshLayout.STATE_FOOTER -> setLabel(resources.getString(R.string.loading))
            RefreshLayout.STATE_PENDING -> setLabel(resources.getString(R.string.pending))
        }
    }

    fun setContentView(layoutResId: Int) {
        val view = LayoutInflater.from(context).inflate(layoutResId, this)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        //
        mTvLabel = view.findViewById(R.id.tvLabel)
        mView = view
    }
}