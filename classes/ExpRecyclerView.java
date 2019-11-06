import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpRecyclerView implements OnStartDragParentListener, OnStartDragChildListener, OnItemDismissListener{

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ExpandableAdapter expAdapter;
    private ItemTouchHelper touchHelper;
    private SimpleItemTouchHelperCallback callback;

    private boolean parentsDraggable = true;
    private boolean childrenDraggable = true;
    private boolean parentsSwipeable = true;
    private boolean childrenSwipeable = true;

    ExpRecyclerView(Context context, RecyclerView recyclerView, List<Parent> parentList){
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        expAdapter = new ExpandableAdapter(context, parentList, this, this, this, parentsDraggable, childrenDraggable);

        recyclerView.setAdapter(expAdapter);
        recyclerView.setHasFixedSize(false);

        checkListOrEmptyText(parentList);

        /*The touchhelper will concern about the events that happen on the list --> Drag and swipe */
        callback = new SimpleItemTouchHelperCallback(context, expAdapter, parentsSwipeable, childrenSwipeable);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    ExpRecyclerView(Context context, RecyclerView recyclerView, TextView emptyTextView, List<Parent> parentList){
        this(context, recyclerView, parentList);
        this.emptyTextView = emptyTextView;
    }

    @Override
    public void onStartDragParent(RecyclerView.ViewHolder viewHolder) {
        if(!expAdapter.blocked && parentsDraggable){
            touchHelper.startDrag(viewHolder);
        }
    }

    @Override
    public void onStartDragChild(RecyclerView.ViewHolder viewHolder) {
        if(!expAdapter.blocked && childrenDraggable){
            touchHelper.startDrag(viewHolder);
        }
    }

    @Override
    public void onItemDismiss(List<Parent> parentList) {
        checkListOrEmptyText(parentList);
    }

    private void checkListOrEmptyText(List list){
        if (list.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            if(hasEmptyTextView()) emptyTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            if(hasEmptyTextView()) emptyTextView.setVisibility(View.GONE);
        }
    }

    private boolean hasEmptyTextView(){
        return emptyTextView != null;
    }

    //TODO MODIFY THIS e.g. addParent(Parent parent, int where)
    void addParent(Parent parent){
        if(parent.getChildItemList().size() > 0){
            expAdapter.parentItemList.add(parent);
            expAdapter.notifyParentItemInserted(expAdapter.parentItemList.size() - 1);
        }
        else{
            //ERROR CHILD MISSING
        }
    }

    //TODO MODIFY THIS e.g. addChild(Child child, int where)
    void addChild(Child child){
        Parent parent = expAdapter.parentItemList.get(expAdapter.parentItemList.size() - 1);
        parent.getChildItemList().add(child);
        expAdapter.notifyChildItemInserted(expAdapter.parentItemList.indexOf(parent), parent.getChildItemList().size() - 1);
        expAdapter.notifyDataSetChanged();
    }

    void enableParentsDrag(){
        parentsDraggable = true;
        expAdapter.showParentDragArea(true);
    }

    void enableChildrenDrag(){
        childrenDraggable = true;
        expAdapter.showChildDragArea(true);
    }

    void disableParentsDrag(){
        parentsDraggable = false;
        expAdapter.showParentDragArea(false);
    }

    void disableChildrenDrag(){
        childrenDraggable = false;
        expAdapter.showChildDragArea(false);
    }

    void enableParentsSwipe(){
        parentsSwipeable = true;
        callback.enableParentsSwipe();
    }

    void enableChildrenSwipe(){
        childrenSwipeable = true;
        callback.enableChildrenSwipe();
    }

    void disableParentsSwipe(){
        parentsSwipeable = false;
        callback.disableParentsSwipe();
    }

    void disableChildrenSwipe(){
        childrenSwipeable = false;
        callback.disableChildrenSwipe();
    }

    public boolean isParentsDragEnabled(){
        return parentsDraggable;
    }

    public boolean isChildrenDragEnabled(){
        return childrenDraggable;
    }

    public boolean isParentsSwipeEnabled(){
        return parentsSwipeable;
    }

    public boolean isChildrenSwipeEnabled(){
        return childrenSwipeable;
    }

}
