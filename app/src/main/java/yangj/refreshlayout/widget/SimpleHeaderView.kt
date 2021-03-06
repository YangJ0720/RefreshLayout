package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import yangj.refreshlayout.R
import yangj.refreshlayout.RefreshLayout

/**
 * @author YangJ
 */
class SimpleHeaderView : HeaderView {

    private lateinit var mTextView: TextView

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_simple_header, this)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        //
        mTextView = view.findViewById(R.id.textView)
    }

    override fun setRefreshState(state: Int) {
        when (state) {
            RefreshLayout.STATE_NORMAL -> setLabel(R.string.refresh_header)
            RefreshLayout.STATE_HEADER -> setLabel(R.string.refresh_header)
            RefreshLayout.STATE_PENDING -> setLabel(R.string.pending)
            RefreshLayout.STATE_LOADING -> setLabel(R.string.loading)
        }
    }

    override fun notifyHeaderScrollChanged(distance: Int) {

    }

    private fun setLabel(resId: Int) {
        mTextView.setText(resId)
    }
}