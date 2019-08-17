package yangj.refreshlayout.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
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

    private var mState = RefreshLayout.STATE_NORMAL

    private lateinit var mImageView: ImageView
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
        if (mState == state) return
        // 更改header状态
        when (state) {
            RefreshLayout.STATE_NORMAL -> {
                setLabel(R.string.refresh_header)
                setRotation(180.0f, 0.0f)
            }
            RefreshLayout.STATE_HEADER -> {
                setLabel(R.string.refresh_header)
                setRotation(180.0f, 0.0f)
            }
            RefreshLayout.STATE_PENDING -> {
                setLabel(R.string.pending)
                setRotation(0.0f, 180.0f)
            }
            RefreshLayout.STATE_LOADING -> setLabel(R.string.loading)
        }
        // 记录刷新状态
        mState = state
    }

    override fun notifyHeaderScrollChanged(distance: Int) {
        val childView = getChildAt(0)
        childView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, abs(distance), Gravity.CENTER
        )
        requestLayout()
    }

    private fun setContentView() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_scale_header, this)
        view.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        mImageView = view.findViewById(R.id.imageView)
        mTextView = view.findViewById(R.id.textView)
    }

    private fun setRotation(start: Float, stop: Float) {
        val animator = mImageView.animation
        animator?.cancel()
        val animatorNew = ObjectAnimator.ofFloat(mImageView, "rotation", start, stop)
        animatorNew.duration = 200
        animatorNew.start()
    }
}