package yangj.refreshlayout

import android.content.Context
import android.graphics.Canvas
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
        // 就绪状态，当手指上下拖拽距离满足下拉刷新、上拉加载的达成距离时，可以使用该状态值
        const val STATE_PENDING = 4
        // 重置header和footer状态至隐藏执行的默认时长
        const val RESET_DURATION = 500
        // 检查向上滚动为负
        const val SCROLL_DIRECTION_TOP = -1
        // 检查向下滚动为正
        const val SCROLL_DIRECTION_BOTTOM = 1
        // TAG
        private const val TAG = "RefreshLayout"
    }

    private var mHeaderViewId: Int = 0
    private var mFooterViewId: Int = 0
    // 下拉刷新布局
    private var mHeaderView: HeaderView? = null
    // 下拉刷新布局高度
    private var mHeaderViewHeight = 0
    private var mHeaderViewPendingHeight = 0
    // 内容控件布局
    private lateinit var mContentView: View
    // 内容控件布局高度
    private var mContentViewHeight = 0
    // 上拉加载布局
    private var mFooterView: FooterView? = null
    // 上拉加载布局高度
    private var mFooterViewHeight = 0
    private var mFooterViewPendingHeight = 0
    // 滑动控制器
    private lateinit var mScroller: Scroller
    // 刷新状态
    private var mState = STATE_NORMAL
    // 下拉刷新、上拉加载事件监听
    private var mListener: OnRefreshListener? = null
    // 重置header和footer状态至隐藏执行的时长
    private var mResetDuration = RESET_DURATION

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)
        typedArray?.let { array ->
            mHeaderViewId = array.getResourceId(R.styleable.RefreshLayout_header, 0)
            mFooterViewId = array.getResourceId(R.styleable.RefreshLayout_footer, 0)
            mResetDuration = array.getInt(R.styleable.RefreshLayout_reset_duration, RESET_DURATION)
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
        if (mHeaderViewId != 0) {
            val headerView = HeaderView(context)
            headerView.setContentView(mHeaderViewId)
            addHeaderView(headerView)
        }
        if (mFooterViewId != 0) {
            val footerView = FooterView(context)
            footerView.setContentView(mFooterViewId)
            addFooterView(footerView)
        }
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
        if (mHeaderViewPendingHeight == 0) {
            mHeaderViewPendingHeight = mHeaderViewHeight
        }
        if (mFooterViewPendingHeight == 0) {
            mFooterViewPendingHeight = mFooterViewHeight
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

    override fun onDraw(canvas: Canvas?) {
        if (scrollY == 0) {
            mState = STATE_NORMAL
            return
        }
        if (STATE_HEADER == mState || scrollY < 0) {
            mHeaderView!!.notifyHeaderScrollChanged(scrollY)
        } else if (STATE_FOOTER == mState || scrollY > 0) {
            mFooterView!!.notifyFooterScrollChanged(scrollY)
        }
    }

    /**
     * 下拉刷新达成条件，这里定义为下拉距离大于等于header高度的2倍
     */
    private fun onStateByHeaderRefresh(): Boolean {
//        return abs(scrollY) >= mHeaderViewHeight * 2
        return abs(scrollY) >= mHeaderViewPendingHeight * 2
    }

    /**
     * 上拉加载达成条件，这里定义为上拉距离大于等于footer高度的2倍
     */
    private fun onStateByFooterRefresh(): Boolean {
//        return scrollY >= mFooterViewHeight * 2
        return scrollY >= mFooterViewPendingHeight * 2
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 对ACTION_UP事件进行处理，用于判断是否满足下拉刷新、上拉加载条件以及平滑处理滑动
        if (MotionEvent.ACTION_UP == ev?.action) {
            if (scrollY == 0 || STATE_NORMAL != mState) {
                return super.dispatchTouchEvent(ev)
            }
            var dy = -scrollY
            // 判断滑动距离是否达到下拉刷新、上拉加载条件
            if (scrollY < 0 && onStateByHeaderRefresh() && hasHeaderView()) { // 手势向下滑动
                // 兼容缩放header高度，需要保存原始高度
                mHeaderViewHeight = mHeaderViewPendingHeight
                //
                dy = -(scrollY + mHeaderViewHeight)
                // 设置为下拉状态
                if (STATE_HEADER == mState) return super.dispatchTouchEvent(ev)
                mState = STATE_HEADER
                mHeaderView!!.setRefreshState(mState)
                mListener?.onRefresh(this)
            } else if (scrollY > 0 && onStateByFooterRefresh() && hasFooterView()) { // 手势向上滑动
                // 兼容缩放footer高度，需要保存原始高度
                mFooterViewHeight = mFooterViewPendingHeight
                //
                dy = -(scrollY - mFooterViewHeight)
                // 设置为上拉状态
                if (STATE_FOOTER == mState) return super.dispatchTouchEvent(ev)
                mState = STATE_FOOTER
                mFooterView!!.setRefreshState(mState)
                mListener?.onLoader(this)
            }
            mScroller.startScroll(0, scrollY, 0, dy, mResetDuration)
            invalidate()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            invalidate()
        }
    }

    /**
     * 添加header头部控件
     */
    fun addHeaderView(headerView: HeaderView) {
        if (hasHeaderView()) {
            removeHeaderView()
        }
        this.mHeaderView = headerView
        addView(headerView)
    }

    /**
     * 移除header头部控件
     */
    private fun removeHeaderView() {
        removeView(mHeaderView)
        mHeaderView = null
    }

    /**
     * 添加footer尾部控件
     */
    fun addFooterView(footerView: FooterView) {
        if (hasFooterView()) {
            removeFooterView()
        }
        this.mFooterView = footerView
        addView(footerView)
    }

    /**
     * 移除footer尾部控件
     */
    private fun removeFooterView() {
        removeView(mFooterView)
        mFooterView = null
    }

    /**
     * 设置下拉刷新、上拉加载事件监听器
     */
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    /**
     * 是否设置可以下拉刷新
     */
    private fun hasHeaderView(): Boolean {
        return mHeaderView != null
    }

    /**
     * 是否设置可以上拉加载
     */
    private fun hasFooterView(): Boolean {
        return mFooterView != null
    }

    /**
     * 手势滑动阻尼系数，这里是滑动距离的一半
     */
    private fun convertToDamping(value: Int): Int {
        return value / 2
    }

    /**
     * 下拉刷新执行完毕时，需要调用这个方法重置RefreshLayout的状态
     */
    fun refreshComplete() {
        mState = STATE_NORMAL
//        mState = STATE_HEADER
        mScroller.startScroll(0, scrollY, 0, -scrollY, mResetDuration)
        invalidate()
        //
        mHeaderView!!.setRefreshState(mState)
    }

    /**
     * 上拉加载执行完毕时，需要调用这个方法重置RefreshLayout的状态
     */
    fun loaderComplete() {
        mState = STATE_NORMAL
//        mState = STATE_FOOTER
        mScroller.startScroll(0, scrollY, 0, -scrollY, mResetDuration)
        invalidate()
        //
        mFooterView!!.setRefreshState(mState)
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
     * 判断垂直滑动是否到达边界
     * @param direction 参数为滑动方向，检查向上滚动为负，检查向下滚动为正
     * <p>SCROLL_DIRECTION_TOP</p>
     * <p>SCROLL_DIRECTION_BOTTOM</p>
     */
    private fun checkCanScrollVerticallyByDirection(direction: Int): Boolean {
        return !mContentView.canScrollVertically(direction)
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
            if (hasHeaderView() && checkCanScrollVerticallyByDirection(SCROLL_DIRECTION_TOP)) { // 内容控件不可以向下滑动
                // 如果满足下拉刷新达成条件，修改header文本内容为松开刷新
                if (onStateByHeaderRefresh()) {
//                    mState = STATE_PENDING
//                    mHeaderView!!.setRefreshState(mState)
                    mHeaderView!!.setRefreshState(STATE_PENDING)
                } else {
                    // mState = STATE_HEADER
                }
                // 设置RefreshLayout嵌套滑动
                val realY = convertToDamping(dy)
                scrollBy(0, realY)
                consumed[1] = realY
                //
                mHeaderView!!.notifyHeaderScrollChanged(scrollY)
            } else if (scrollY > 0) { // 向下滑动隐藏footer
                // 如果不满足上拉加载达成条件，修改footer文本内容为上拉加载
                if (hasFooterView() && !onStateByFooterRefresh()) {
                    mState = STATE_NORMAL
                    mFooterView!!.setRefreshState(mState)
//                    mFooterView!!.setRefreshState(STATE_NORMAL)
                }
                // 设置RefreshLayout嵌套滑动
                var realY = dy
                if (abs(dy) > scrollY) {
                    realY = -scrollY
                }
                scrollBy(0, convertToDamping(realY))
                consumed[1] = realY
                //
                mHeaderView?.notifyFooterScrollChanged(scrollY)
            }
        } else if (dy > 0) { // 手指向上滑动
            if (hasFooterView() && checkCanScrollVerticallyByDirection(SCROLL_DIRECTION_BOTTOM)) { // 内容控件不可以向上滑动
                // 如果满足上拉加载达成条件，修改footer文本内容为松开刷新
                if (onStateByFooterRefresh()) {
//                    mState = STATE_PENDING
//                    mFooterView!!.setRefreshState(mState)
                    mFooterView!!.setRefreshState(STATE_PENDING)
                } else {
//                    mState = STATE_FOOTER
                }
                // 设置RefreshLayout嵌套滑动
                val realY = convertToDamping(dy)
                scrollBy(0, realY)
                consumed[1] = realY
                //
                mFooterView!!.notifyFooterScrollChanged(scrollY)
            } else if (scrollY < 0) { // 向上滑动隐藏header
                // 如果不满足下拉刷新达成条件，修改header文本内容为下拉刷新
                if (hasHeaderView() && !onStateByHeaderRefresh()) {
                    mState = STATE_NORMAL
                    mHeaderView!!.setRefreshState(mState)
//                    mHeaderView!!.setRefreshState(STATE_NORMAL)
                }
                // 设置RefreshLayout嵌套滑动
                var realY = dy
                if (realY > abs(scrollY)) {
                    realY = abs(scrollY)
                }
                scrollBy(0, convertToDamping(realY))
                consumed[1] = realY
                //
                mHeaderView?.notifyHeaderScrollChanged(scrollY)
            }
        }
    }

    /**
     * 关键方法，用于接收子控件嵌套滑动之后的距离
     * <p>该方法可以用于父控件选择是否处理剩余的嵌套滑动距离</p>
     */
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        Log.i(TAG, "onNestedScroll")
    }

    /**
     * 子控件通过调用stopNestedScroll方法回调父控件的该方法
     * <p>用来处理一些收尾工作</p>
     */
    override fun onStopNestedScroll(child: View) {
        Log.i(TAG, "onStopNestedScroll")
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