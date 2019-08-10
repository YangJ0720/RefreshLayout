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
 * 功能描述 一个具有缩放功能的footer
 * @author YangJ
 * @since 2019/8/10
 */
class ScaleFooterView : FooterView {

    private lateinit var mTextView: TextView

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        setContentView()
    }

    override fun setRefreshState(state: Int) {
        when (state) {
            RefreshLayout.STATE_NORMAL -> mTextView.setText(R.string.refresh_footer)
            RefreshLayout.STATE_FOOTER -> mTextView.setText(R.string.refresh_footer)
            RefreshLayout.STATE_PENDING -> mTextView.setText(R.string.pending)
            RefreshLayout.STATE_LOADING -> mTextView.setText(R.string.loading)
        }
    }

    override fun notifyFooterScrollChanged(distance: Int) {
        val childView = getChildAt(0)
        childView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, abs(distance), Gravity.CENTER
        )
    }

    private fun setContentView() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_scale_footer, this)
        view.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        mTextView = view.findViewById(R.id.textView)
    }
}