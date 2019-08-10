package yangj.refreshlayout.widget

/**
 * 功能描述
 * @author YangJ
 * @since 2019/8/10
 */
interface Special {

    fun setRefreshState(state: Int)

    fun notifyHeaderScrollChanged(distance: Int)

    fun notifyFooterScrollChanged(distance: Int)
}