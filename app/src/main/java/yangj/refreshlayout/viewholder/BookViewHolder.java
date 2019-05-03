package yangj.refreshlayout.viewholder;

import android.view.View;
import android.widget.TextView;
import yangj.refreshlayout.R;
import yangj.refreshlayout.common.CommonViewHolder;

/**
 * Created by YangJ on 2016/6/24.
 */
public class BookViewHolder extends CommonViewHolder {

    public TextView mTextView;

    public BookViewHolder(View view) {
        super(view);
        mTextView = getView(R.id.textView);
    }

}
