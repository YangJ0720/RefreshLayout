package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import yangj.refreshlayout.R
import yangj.refreshlayout.RefreshLayout
import kotlin.math.abs

/**
 * 功能描述 一个具有缩放功能的header
 * @author YangJ
 * @since 2019/8/10
 */
class ScaleHeaderView : HeaderView {

    private lateinit var mTextView: TextView

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        setContentView()
    }

    private fun setLabel(resId: Int) {
        mTextView.setText(resId)
    }

    override fun setRefreshState(state: Int) {
        when(state) {
            RefreshLayout.STATE_NORMAL -> setLabel(R.string.refresh_header)
            RefreshLayout.STATE_HEADER -> setLabel(R.string.refresh_header)
            RefreshLayout.STATE_PENDING -> setLabel(R.string.pending)
            RefreshLayout.STATE_LOADING -> setLabel(R.string.loading)
        }
    }

    override fun notifyHeaderScrollChanged(distance: Int) {
        val childView = getChildAt(0)
        childView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, abs(distance), Gravity.CENTER
        )
        println("notifyHeaderScrollChanged -> distance = $distance")
    }

    private fun setContentView() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_scale_header, this)
        view.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        mTextView = view.findViewById(R.id.textView)
    }
}