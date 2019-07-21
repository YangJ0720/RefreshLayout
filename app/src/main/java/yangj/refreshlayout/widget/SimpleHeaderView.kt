package yangj.refreshlayout.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import yangj.refreshlayout.R

/**
 * @author YangJ
 */
class SimpleHeaderView : HeaderView {

    private var mTvLabel: TextView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setLabel(text: String) {
        if (mTvLabel == null) {
            mTvLabel = mView?.findViewById<TextView>(R.id.tvLabel)
        }
        mTvLabel?.text = text
    }

}