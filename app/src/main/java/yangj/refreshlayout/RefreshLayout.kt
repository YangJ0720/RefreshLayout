package yangj.refreshlayout

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.NestedScrollingParentHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller

/**
 * 正常状态
 */
private const val STATUS_NORMAL = 0
/**
 * 下拉状态
 */
private const val STATUS_DROP_DOWN = 1
/**
 * 下拉刷新状态
 */
private const val STATUS_REFRESH = 2
/**
 * 上拉状态
 */
private const val STATUS_PULL_UP = 3
/**
 * 上拉刷新状态
 */
private const val STATUS_LOADMORE = 4

/**
 * Created by YangJ on 2018/12/1.
 */
class RefreshLayout : ViewGroup, NestedScrollingParent {

    /**
     * 头部控件布局id
     */
    private var mHeaderViewId = 0
    /**
     * 头部控件
     */
    private lateinit var mHeaderView: View
    /**
     * 头部控件高度
     */
    private var mHeaderViewHeight = 0
    /**
     * 尾部控件布局id
     */
    private var mFooterViewId = 0
    /**
     * 尾部控件
     */
    private lateinit var mFooterView: View
    /**
     * 尾部控件高度
     */
    private var mFooterViewHeight = 0
    /**
     * 内容控件
     */
    private lateinit var mContentView: View
    /**
     * 内容控件高度
     */
    private var mContentViewHeight = 0

    private var mDownY = 0f
    private var mLayoutStatus = STATUS_NORMAL
    private lateinit var mNestedScrollingParentHelper: NestedScrollingParentHelper
    private lateinit var mScroller: Scroller

    constructor(context: Context?) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context?, attrs: AttributeSet?) {
        // 获取布局中设置的header和footer
        val array = context?.obtainStyledAttributes(attrs, R.styleable.RefreshLayoutStyleable)
        array?.let {
            mHeaderViewId = it.getResourceId(R.styleable.RefreshLayoutStyleable_header, 0)
            mFooterViewId = it.getResourceId(R.styleable.RefreshLayoutStyleable_footer, 0)
            it.recycle()
        }
        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        // 初始化Scroller
        mScroller = Scroller(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 判断RefreshLayout内容控件是否合法
        if (childCount > 1) {
            throw IllegalStateException("RefreshLayout can only have one child")
        }
        // 对内容控件对象赋值
        mContentView = getChildAt(0)
        // 添加header和footer
        val inflater = LayoutInflater.from(context)
        if (mHeaderViewId != 0) {
            mHeaderView = inflater.inflate(mHeaderViewId, this)
        }
        if (mFooterViewId != 0) {
            mFooterView = inflater.inflate(mFooterViewId, this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var useHeight = 0
        // 遍历childView
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val params = childView.layoutParams
            // 让childView调用自己的onMeasure测量自己的大小
            childView.measure(
                getChildMeasureSpec(
                    widthMeasureSpec,
                    childView.paddingLeft + childView.paddingRight, params.width
                ),
                getChildMeasureSpec(
                    heightMeasureSpec,
                    childView.paddingTop + childView.paddingBottom, params.height
                )
            )
            // 获取测量到的childView高度
            val childViewHeight = childView.measuredHeight
            when (childView.id) {
                R.id.header -> {
                    mHeaderViewHeight = childViewHeight
                }
                R.id.footer -> {
                    mFooterViewHeight = childViewHeight
                }
                else -> {
                    mContentViewHeight = childViewHeight
                }
            }
            useHeight += childViewHeight
        }
        // 设置RefreshLayout的宽度和高度
        setMeasuredDimension(widthMeasureSpec, useHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var useHeight = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childViewHeight = childView.measuredHeight
            when (childView.id) {
                R.id.header -> {
                    childView.layout(l, -childViewHeight, r, 0)
                }
                R.id.footer -> {
                    childView.layout(l, mContentViewHeight, r, mContentViewHeight + childViewHeight)
                    useHeight += childViewHeight
                }
                else -> {
                    childView.layout(l, 0, r, childViewHeight)
                    useHeight += childViewHeight
                }
            }
        }
    }

//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                mDownY = ev.y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val distance = mDownY - ev.y
//                if (distance > 0) {
//                    if (!mContentView.canScrollVertically(0)) {
//                        return true
//                    }
//                } else if (distance < 0) {
//                    if (!mContentView.canScrollVertically(-1)) {
//                        return true
//                    }
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                return false
//            }
//        }
//        return super.onInterceptTouchEvent(ev)
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                return true
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val distance = mDownY - event.y
//                if (distance > 0) {
//                    if (!mContentView.canScrollVertically(0)) {
//                        if (STATUS_DROP_DOWN != mLayoutStatus) {
//                            mLayoutStatus = STATUS_DROP_DOWN
//                        }
//                        scrollTo(0, distance.toInt())
//                        return true
//                    }
//                } else if (distance < 0) {
//                    if (!mContentView.canScrollVertically(-1)) {
//                        if (STATUS_PULL_UP != mLayoutStatus) {
//                            mLayoutStatus = STATUS_PULL_UP
//                        }
//                        scrollTo(0, distance.toInt())
//                        return true
//                    }
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                mLayoutStatus = STATUS_NORMAL
//                mScroller.startScroll(0, 0, 0, scrollY)
//                invalidate()
//                return true
//            }
//        }
//        return super.onTouchEvent(event)
//    }

    /**
     * 询问当前view是否支持嵌套滑动
     *
     * @return 返回true表示支持与childView进行嵌套滑动，返回false表示不支持
     */
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return true
    }

    /**
     * onStartNestedScroll返回true时，该函数会被调用
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    /**
     * 停止嵌套滑动
     */
    override fun onStopNestedScroll(child: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(child)
    }

    /**
     * 与当前view一起滑动的childView在滑动的过程中会回调该函数，用于告知childView的滑动情况
     */
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {

    }

    /**
     * 与当前view一起滑动的childView在准备滑动之前会回调该函数
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy < 0) { // 手指向下滑动
            // 对header进行显示操作
            val headerScrollY = mHeaderView.scrollY
            if (Math.abs(headerScrollY) < mHeaderViewHeight && checkCanScrollVerticallyToTop()) {
                var realY = dy
                if (Math.abs(dy + headerScrollY) > mHeaderViewHeight) {
                    realY = -(mHeaderViewHeight + headerScrollY)
                }
                scrollBy(0, realY)
                consumed[1] = realY
            } else {
                scrollBy(0, dy)
            }
            // 对footer进行隐藏操作
            val footerScrollY = mFooterView.scrollY
            if (footerScrollY in 1..mFooterViewHeight) {
                var realY = dy
                if (Math.abs(dy) > footerScrollY) {
                    realY = -footerScrollY
                }
                scrollBy(0, realY)
                consumed[1] = realY
            }
        } else if (dy > 0) { // 手指向上滑动
            // 对header进行隐藏操作
            val headerScrollY = mHeaderView.scrollY
            if (mHeaderViewHeight >= Math.abs(headerScrollY) && headerScrollY <= 0) {
                var realY = dy
                if (dy > Math.abs(headerScrollY)) {
                    realY = Math.abs(headerScrollY)
                }
                scrollBy(0, realY)
                consumed[1] = realY
            }
            // 对footer进行显示操作
            val footerScrollY = mFooterView.scrollY
            if (footerScrollY < mFooterViewHeight && checkCanScrollVerticallyToBottom()) {
                var realY = dy
                if (dy + footerScrollY > mFooterViewHeight) {
                    realY = mFooterViewHeight - footerScrollY
                }
                scrollBy(0, realY)
                consumed[1] = realY
            }
        }
    }

    /**
     * 与当前view一起滑动的childView在fling状态下会回调该函数
     */
    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        println("onNestedFling")
        return false
    }

    /**
     * 与当前view一起滑动的childView在准备fling状态之前会回调该函数
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        println("onNestedPreFling")
        return false
    }

    /**
     * 获取嵌套滑动的轴
     */
    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, 0)
            postInvalidate()
        }
    }

    /**
     * 判断内容控件是否滚动到顶部
     */
    fun checkCanScrollVerticallyToTop(): Boolean {
        return !mContentView.canScrollVertically(-1)
    }

    /**
     * 判断内容控件是否滚动到底部
     */
    fun checkCanScrollVerticallyToBottom(): Boolean {
        return !mContentView.canScrollVertically(1)
    }

}