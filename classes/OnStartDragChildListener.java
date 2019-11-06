import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragChildListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDragChild(RecyclerView.ViewHolder viewHolder);
}
