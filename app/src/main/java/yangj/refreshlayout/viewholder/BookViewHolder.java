package yangj.refreshlayout.viewholder;

import android.view.View;
import android.widget.TextView;
import yangj.refreshlayout.R;
import yangj.refreshlayout.common.CommonViewHolder;

/**
 * Created by YangJ on 2016/6/24.
 */
public class BookViewHolder extends CommonViewHolder {

    public TextView mTvId;
    public TextView mTvName;

    public BookViewHolder(View view) {
        super(view);
        mTvId = getView(R.id.tvId);
        mTvName = getView(R.id.tvName);
    }

}
