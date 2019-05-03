package yangj.refreshlayout.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by YangJ on 2016/6/24.
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    protected <T extends View> T getView(int resId) {
        return (T) itemView.findViewById(resId);
    }

}
