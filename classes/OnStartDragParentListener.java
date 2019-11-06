import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragParentListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDragParent(RecyclerView.ViewHolder viewHolder);
}
