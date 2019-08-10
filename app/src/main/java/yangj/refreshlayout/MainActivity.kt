package yangj.refreshlayout

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import yangj.refreshlayout.adapter.BookAdapter
import yangj.refreshlayout.bean.Book
import yangj.refreshlayout.widget.FooterView
import yangj.refreshlayout.widget.HeaderView

/**
 * @author YangJ
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mList: ArrayList<Book>
    private lateinit var mAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initView()
    }

    private fun initData() {
        val count = 100
        mList = ArrayList(count)
        for (i in 0..count) {
            mList.add(Book(i.toLong(), "第${i}本书"))
        }
        mAdapter = BookAdapter(this, mList)

    }

    private fun initView() {
        // 初始化RefreshLayout
        refreshLayout.setOnRefreshListener(object : RefreshLayout.OnRefreshListener {
            override fun onRefresh(target: RefreshLayout) {
                Handler().postDelayed({
                    // 在数组最开始的位置添加一条数据
                    val position = 0
                    val currentTimeMillis = System.currentTimeMillis()
                    mList.add(position, Book(currentTimeMillis, "下拉: $currentTimeMillis"))
                    mAdapter.notifyItemInserted(position)
                    // 下拉刷新执行完毕
                    target.refreshComplete()
                }, 5000)
            }

            override fun onLoader(target: RefreshLayout) {
                Handler().postDelayed({
                    // 在数组最末尾的位置添加一条数据
                    val size = mList.size
                    val currentTimeMillis = System.currentTimeMillis()
                    mList.add(Book(currentTimeMillis, "上拉: $currentTimeMillis"))
                    mAdapter.notifyItemInserted(size)
                    // 上拉加载执行完毕
                    target.loaderComplete()
                }, 5000)
            }
        })
        // 添加header控件
        val header = HeaderView(this)
        header.setContentView(R.layout.view_header)
        refreshLayout.addHeaderView(header)
        // 添加footer控件
        val footer = FooterView(this)
        footer.setContentView(R.layout.view_footer)
        refreshLayout.addFooterView(footer)
        // 初始化RecyclerView
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

}
