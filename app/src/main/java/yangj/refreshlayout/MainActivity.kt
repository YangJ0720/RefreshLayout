package yangj.refreshlayout

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import yangj.refreshlayout.adapter.BookAdapter
import yangj.refreshlayout.bean.Book

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
        refreshLayout.setOnRefreshListener(object :RefreshLayout.OnRefreshListener{
            override fun onRefresh(target: RefreshLayout) {
                Toast.makeText(this@MainActivity, R.string.refresh_header, Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    target.refreshComplete()
                }, 5000)
            }

            override fun onLoader(target: RefreshLayout) {
                Toast.makeText(this@MainActivity, R.string.refresh_footer, Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    target.loaderComplete()
                }, 5000)
            }

        })
        // 初始化RecyclerView
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

}
