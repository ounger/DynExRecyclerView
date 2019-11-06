import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback{

    private final ExpandableAdapter adapter;
    private boolean parentsSwipeable;
    private boolean childrenSwipeable;

    public SimpleItemTouchHelperCallback(Context context, ExpandableAdapter adapter, boolean parentsSwipeable, boolean childrenSwipeable) {
        this.adapter = adapter;
        this.parentsSwipeable = parentsSwipeable;
        this.childrenSwipeable = childrenSwipeable;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 25;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = createSwipeFlags(viewHolder.getAdapterPosition());
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    private int createSwipeFlags(int position) {
        if(adapter.isParentAt(position) && parentsSwipeable)
            return ItemTouchHelper.START | ItemTouchHelper.END;
        else if(adapter.isParentAt(position) && !parentsSwipeable)
            return 0;
        else if(childrenSwipeable)
            return ItemTouchHelper.START | ItemTouchHelper.END;
        else
            return 0;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() { return parentsSwipeable || childrenSwipeable; }

    void enableParentsSwipe(){
        parentsSwipeable = true;
    }

    void enableChildrenSwipe(){
        childrenSwipeable = true;
    }

    void disableParentsSwipe(){
        parentsSwipeable = false;
    }

    void disableChildrenSwipe(){
        childrenSwipeable = false;
    }


}
