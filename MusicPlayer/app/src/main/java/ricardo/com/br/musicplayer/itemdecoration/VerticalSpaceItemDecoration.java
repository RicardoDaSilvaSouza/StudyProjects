package ricardo.com.br.musicplayer.itemdecoration;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Ricardo on 01/01/2016.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration{

    public static final Integer VERTICAL_ITEM_SPACE = 3;

    private final Integer verticalSpaceHeight;

    public VerticalSpaceItemDecoration(Integer verticalSpaceHeight){
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1){
            outRect.bottom = verticalSpaceHeight;
        }
    }
}
