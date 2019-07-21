package yangj.refreshlayout

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import yangj.refreshlayout.widget.FooterView
import yangj.refreshlayout.widget.HeaderView
import kotlin.math.abs

/**
 * @author YangJ
 */
class RefreshLayout : ViewGroup, NestedScrollingParent {

    companion object {
        // 正常状态
        const val STATE_NORMAL = 0
        // 下拉刷新状态
        const val STATE_HEADER = 1
        // 上拉加载状态
        const val STATE_FOOTER = 2
        // TAG
        private const val TAG = "RefreshLayout"
    }

    private var mHeaderViewId: Int = 0
    private var mFooterViewId: Int = 0
    // 下拉刷新布局
    private lateinit var mHeaderView: HeaderView
    // 下拉刷新布局高度
    private var mHeaderViewHeight = 0
    // 内容控件布局
    private lateinit var mContentView: View
    // 内容控件布局高度
    private var mContentViewHeight = 0
    // 上拉加载布局
    private lateinit var mFooterView: FooterView
    // 上拉加载布局高度
    private var mFooterViewHeight = 0
    // 滑动控制器
    private lateinit var mScroller: Scroller
    // 刷新状态
    private var mState = STATE_NORMAL
    // 下拉刷新、上拉加载事件监听
    private var mListener: OnRefreshListener? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)
        typedArray?.let { array ->
            mHeaderViewId = array.getResourceId(R.styleable.RefreshLayout_header, R.layout.view_header)
            mFooterViewId = array.getResourceId(R.styleable.RefreshLayout_footer, R.layout.view_footer)
            array.recycle()
        }
        mScroller = Scroller(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 判断RefreshLayout内容控件是否合法
        if (childCount > 1) {
            throw IllegalStateException("RefreshLayout can only have one child")
        }
        // 获取内容控件
        mContentView = getChildAt(0)
        // 将header和footer添加到布局容器
        mHeaderView = HeaderView(context)
        mHeaderView.setContentView(mHeaderViewId)
        addView(mHeaderView)
        mFooterView = FooterView(context)
        mFooterView.setContentView(mFooterViewId)
        addView(mFooterView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 遍历子控件并测量它们的高度
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val params = childView.layoutParams
            val widthPadding = childView.paddingLeft + childView.paddingRight
            val heightPadding = childView.paddingTop + childView.paddingBottom
            childView.measure(
                getChildMeasureSpec(widthMeasureSpec, widthPadding, params.width),
                getChildMeasureSpec(heightMeasureSpec, heightPadding, params.height)
            )
            // 获取测量到的childView高度
            val measuredHeight = childView.measuredHeight
            when (childView) {
                is HeaderView -> mHeaderViewHeight = measuredHeight
                is FooterView -> mFooterViewHeight = measuredHeight
                else -> mContentViewHeight = measuredHeight
            }
        }
        setMeasuredDimension(widthMeasureSpec, mContentViewHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val measuredHeight = childView.measuredHeight
            when (childView) {
                is HeaderView -> childView.layout(l, -measuredHeight, r, 0)
                is FooterView -> childView.layout(l, mContentViewHeight, r, mContentViewHeight + measuredHeight)
                else -> childView.layout(l, 0, r, measuredHeight)
            }
        }
    }

    private fun onStateByHeaderRefresh(): Boolean {
        val result = abs(scrollY) >= mHeaderViewHeight * 2
        if (result) {
            mHeaderView.setLabel(resources.getString(R.string.loading))
        }
        return result
    }

    private fun onStateByFooterRefresh(): Boolean {
        val result = scrollY >= mFooterViewHeight * 2
        if (result) {
            mFooterView.setLabel(resources.getString(R.string.loading))
        }
        return result
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 对ACTION_UP事件进行处理，用于判断是否满足下拉刷新、上拉加载条件以及平滑处理滑动
        if (MotionEvent.ACTION_UP == ev?.action) {
            if (scrollY == 0) {
                return super.dispatchTouchEvent(ev)
            }
            var dy = -scrollY
            // 判断滑动距离是否达到下拉刷新、上拉加载条件
            if (scrollY < 0 && onStateByHeaderRefresh()) { // 手势向下滑动
                dy = -(scrollY + mHeaderViewHeight)
                // 设置为下拉状态
                if (STATE_HEADER == mState) return super.dispatchTouchEvent(ev)
                mState = STATE_HEADER
                mListener?.onRefresh(this)
            } else if (scrollY > 0 && onStateByFooterRefresh()) { // 手势向上滑动
                dy = -(scrollY - mFooterViewHeight)
                // 设置为上拉状态
                if (STATE_FOOTER == mState) return super.dispatchTouchEvent(ev)
                mState = STATE_FOOTER
                mListener?.onLoader(this)
            }
            mScroller.startScroll(0, scrollY, 0, dy, 500)
            invalidate()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            postInvalidate()
        }
    }

    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    /**
     * 下拉刷新执行完毕时，需要调用这个方法重置RefreshLayout的状态
     */
    fun refreshComplete() {
        mState = STATE_NORMAL
        mScroller.startScroll(0, scrollY, 0, -scrollY, 500)
        invalidate()
        //
        mHeaderView.setRefreshState(mState)
    }

    /**
     * 上拉加载执行完毕时，需要调用这个方法重置RefreshLayout的状态
     */
    fun loaderComplete() {
        mState = STATE_NORMAL
        mScroller.startScroll(0, scrollY, 0, -scrollY, 500)
        invalidate()
        //
        mFooterView.setRefreshState(mState)
    }

    /**
     * 下拉刷新、上拉加载事件监听
     */
    interface OnRefreshListener {
        /**
         * 下拉刷新回调函数
         */
        fun onRefresh(target: RefreshLayout)

        /**
         * 上拉加载回调函数
         */
        fun onLoader(target: RefreshLayout)
    }

    /**
     * 判断内容控件是否滚动到顶部
     */
    private fun checkCanScrollVerticallyToTop(): Boolean {
        return !mContentView.canScrollVertically(-1)
    }

    /**
     * 判断内容控件是否滚动到底部
     */
    private fun checkCanScrollVerticallyToBottom(): Boolean {
        return !mContentView.canScrollVertically(1)
    }

    /**
     * 子控件通过调用startNestedScroll方法回调父控件的该方法
     * <p>子控件通过调用父控件的该方法来确定父控件是否接受嵌套滑动信息</p>
     * @param nestedScrollAxes 参数表示子控件滑动方向
     * @return 返回true表示父控件接受嵌套滑动信息，返回false表示不接受
     */
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        Log.i(TAG, "onStartNestedScroll")
        // 如果控件刷新状态是正常状态，并且子控件滑动方向为纵向，就接受嵌套滑动并返回true；否则不接受嵌套滑动并返回false
        return STATE_NORMAL == mState && ViewCompat.SCROLL_AXIS_VERTICAL == nestedScrollAxes
    }

    /**
     * 如果父控件接受嵌套滑动信息，那么该方法会被调用
     * <p>该方法可以让父控件针对嵌套滑动做一些前期工作</p>
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        Log.i(TAG, "onNestedScrollAccepted")
        super.onNestedScrollAccepted(child, target, axes)
    }

    /**
     * 关键方法，用于接收子控件嵌套滑动之前的距离
     * <p>该方法可以用于父控件优先响应嵌套滑动，消耗部分或全部滑动距离</p>
     * @param dx 参数为x轴滑动距离
     * @param dy 参数为y轴滑动距离
     *
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        Log.i(TAG, "onNestedPreScroll")
        if (dy < 0) { // 手指向下滑动
            if (checkCanScrollVerticallyToTop()) { // 内容控件不可以向下滑动
                val realY = dy / 2
                scrollBy(0, realY)
                consumed[1] = realY
            } else if (scrollY > 0) { // 向下滑动隐藏footer
                var realY = dy
                if (abs(dy) > scrollY) {
                    realY = -scrollY
                }
                scrollBy(0, realY)
                consumed[1] = realY
            }
        } else if (dy > 0) { // 手指向上滑动
            if (checkCanScrollVerticallyToBottom()) { // 内容控件不可以向上滑动
                val realY = dy / 2
                scrollBy(0, realY)
                consumed[1] = realY
            } else if (scrollY < 0) { // 向上滑动隐藏header
                var realY = dy
                if (realY > abs(scrollY)) {
                    realY = abs(scrollY)
                }
                scrollBy(0, realY)
                consumed[1] = realY
            }
        }
    }

    /**
     * 关键方法，用于接收子控件嵌套滑动之后的距离
     * <p>该方法可以用于父控件选择是否处理剩余的嵌套滑动距离</p>
     */
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        Log.i(TAG, "onNestedScroll")
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    /**
     * 子控件通过调用stopNestedScroll方法回调父控件的该方法
     * <p>用来处理一些收尾工作</p>
     */
    override fun onStopNestedScroll(child: View) {
        Log.i(TAG, "onStopNestedScroll")
        super.onStopNestedScroll(child)
    }

    /**
     * 该方法返回嵌套滑动的方向
     */
    override fun getNestedScrollAxes(): Int {
        Log.i(TAG, "getNestedScrollAxes")
        return super.getNestedScrollAxes()
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.i(TAG, "onNestedPreFling")
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.i(TAG, "onNestedFling")
        return false
    }
}