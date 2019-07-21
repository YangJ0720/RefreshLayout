package yangj.refreshlayout.common;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by YangJ on 2016/6/24.
 */
public abstract class CommonAdapter<M, V> extends RecyclerView.Adapter<CommonViewHolder> {

    /**
     * RecyclerView数据源
     */
    private List<M> mList;

    /**
     * RecyclerView.Adapter基类
     *
     * @param list          参数为数据源
     */
    public CommonAdapter(List<M> list) {
        mList = list;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createItemViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        bindItemViewHolder((V) holder, mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 创建normal类型的item
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract CommonViewHolder createItemViewHolder(ViewGroup parent, int viewType);

    /**
     * 将数据绑定到normal类型的item
     *
     * @param holder
     * @param item
     */
    protected abstract void bindItemViewHolder(V holder, M item);

}
