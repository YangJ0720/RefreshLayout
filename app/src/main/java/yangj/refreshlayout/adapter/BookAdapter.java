package yangj.refreshlayout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import yangj.refreshlayout.R;
import yangj.refreshlayout.bean.Book;
import yangj.refreshlayout.common.CommonAdapter;
import yangj.refreshlayout.common.CommonViewHolder;
import yangj.refreshlayout.viewholder.BookViewHolder;

import java.util.List;

/**
 * @author YangJ
 * Created by YangJ on 2016/6/24.
 */
public class BookAdapter extends CommonAdapter<Book, BookViewHolder> {

    private LayoutInflater mInflater;

    public BookAdapter(Context context, List list) {
        super(list);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected CommonViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        return new BookViewHolder(mInflater.inflate(R.layout.item, parent, false));
    }

    @Override
    protected void bindItemViewHolder(final BookViewHolder holder, Book item) {
        holder.mTvId.setText(String.valueOf(item.id));
        holder.mTvName.setText(item.name);
    }

}
