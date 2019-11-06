import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.MotionEventCompat;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import java.util.Collections;
import java.util.List;

public class ExpandableAdapter extends ExpandableRecyclerAdapter<ExpParentViewHolder, ExpChildViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private LayoutInflater inflater;
    public List<Parent> parentItemList;

    /*States*/
    public static final int WAIT = 0;
    public static final int SELECTED = 1;
    public static final int ON_MOVE_PARENT = 2;
    public static final int ON_MOVE_CHILD = 3;
    public static final int SWIPE = 4;

    /*Default-state*/
    public int state = WAIT;

    private int parentFromPos;
    private int childFromPos;
    private int endPosition;

    public Child selectedChild;

    /*Very important for consistency! Prevents problems which occur by multiple actions performed simultaneously or in rapid succession.*/
    public boolean blocked = false;

    private OnStartDragParentListener onStartDragParentListener;
    private OnStartDragChildListener onStartDragChildListener;
    private OnItemDismissListener onItemDismissListener;

    private boolean showParentDragArea;
    private boolean showChildDragArea;

    public ExpandableAdapter(Context context, List<Parent> parentItemList, OnStartDragParentListener onStartDragParentListener,
                             OnStartDragChildListener onStartDragChildListener, OnItemDismissListener onItemDismissListener,
                             boolean showParentDragArea, boolean showChildDragArea) {
        super(parentItemList);
        this.parentItemList = parentItemList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onStartDragParentListener = onStartDragParentListener;
        this.onStartDragChildListener = onStartDragChildListener;
        this.onItemDismissListener = onItemDismissListener;
        this.showParentDragArea = showParentDragArea;
        this.showChildDragArea = showChildDragArea;

    }

    /*Element swiped*/
    @Override
    public void onItemDismiss(int position) {
        state = SWIPE;
        Object dismissListItem = getListItem(position);
        int parentPosition = getParentPositionOfPosition(position);
        if(dismissListItem instanceof ParentWrapper){   //Parent
            if(parentItemList.get(parentPosition).getChildItemList() != null){
                for(int i = 0; i < parentItemList.get(parentPosition).getChildItemList().size(); i++){
                    parentItemList.get(parentPosition).getChildItemList().remove(i);
                    notifyChildItemRemoved(parentPosition, i);
                }
            }
            parentItemList.remove(parentPosition);
            notifyParentItemRemoved(parentPosition);

        }else{  //Child
            int cCount = getChildPositionOfPosition(position);

            parentItemList.get(parentPosition).getChildItemList().remove(cCount);
            notifyChildItemRemoved(parentPosition, cCount);
            notifyDataSetChanged();

            if(parentItemList.get(parentPosition).getChildItemList().size() == 0){
                parentItemList.remove(parentPosition);
                notifyParentItemRemoved(parentPosition);
            }

        }

        //Check if list is empty
        onItemDismissListener.onItemDismiss(parentItemList);

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        Object moveListItem = getListItem(fromPosition);

        if(moveListItem instanceof ParentWrapper) {

            moveParents(fromPosition, toPosition);

        }else{  //Kind
            moveChildren(fromPosition, toPosition);

        }

        return true;
    }

    public void childMoveEnd(){
        ParentWrapper pw = getParentWrapperOfPosition(endPosition);
        if(!(endPosition < 1)){
            if(pw != null){
                /*Useful for modifications:
                Parent of the startposition
                Parent fromParent = parentItemList.get(parentFromPos);
                Parent of the endposition
                Parent toParent = parentItemList.get(getParentPositionOfPosition(endPosition));*/

                if(pw.isExpanded()){
                    //Remove child from old parent
                    parentItemList.get(parentFromPos).getChildItemList().remove(childFromPos);

                    //Add child to new parent
                    parentItemList.get(getParentPositionOfPosition(endPosition)).getChildItemList().add(getChildPositionOfPosition(endPosition), selectedChild);

                    notifyDataSetChanged();

                    //If the old parent is empty -> remove it
                    if(parentItemList.get(parentFromPos).getChildItemList().size() == 0){
                        parentItemList.remove(parentFromPos);
                        notifyParentItemRemoved(parentFromPos);
                    }

                }else{ //Collapsed
                    //Remove child from old parent
                    parentItemList.get(parentFromPos).getChildItemList().remove(childFromPos);

                    //Add child to new parent
                    parentItemList.get(getParentPositionOfPosition(endPosition)).getChildItemList().add(selectedChild);

                    /*Remove the childrow*/
                    mItemList.remove(endPosition);
                    notifyDataSetChanged();

                    //If the old parent is empty -> remove it
                    if(parentItemList.get(parentFromPos).getChildItemList().size() == 0){
                        parentItemList.remove(parentFromPos);
                        notifyParentItemRemoved(parentFromPos);
                    }
                }

            }
        }

        selectedChild = null;

    }

    /*If a drag-event takes place, this method gets called multiple times until the element gets dropped*/
    public void moveChildren(int fromPosition, int toPosition){
        /*Store the startposition and some other informations when a drag begins*/
        if(state == SELECTED) {
            state = ON_MOVE_CHILD;
            parentFromPos = getParentPositionOfPosition(fromPosition);
            childFromPos = getChildPositionOfPosition(fromPosition);
            selectedChild = new Child(parentItemList.get(parentFromPos).getChildItemList().get(childFromPos).getText());
        }

        /*Swap the elements.
         "> 0" - condition so that a child element cant be dropped in first position --> Prevent child-elements without parents */
        if(toPosition > 0){
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mItemList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mItemList, i, i - 1);
                }
            }

            notifyItemMoved(fromPosition, toPosition);

            /*Overwrites itself until the element drops*/
            endPosition = toPosition;
        }

    }

    /*Like moveChildren*/
    public void moveParents(int fromPosition, int toPosition){
        if(state == SELECTED){
            state = ON_MOVE_PARENT;
            //Collapse all parents for easier dragging
            for(int i = 0; i < parentItemList.size(); i++){
                collapseParent(i);
            }
        }
        fromPosition = getParentPositionOfPosition(fromPosition);
        toPosition = getParentPositionOfPosition(toPosition);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItemList, i, i + 1);
                Parent p1 = parentItemList.get(i);
                Parent p2 = parentItemList.get(i + 1);
                parentItemList.set(i, p2);
                parentItemList.set(i + 1, p1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItemList, i, i - 1);
                Parent p1 = parentItemList.get(i);
                Parent p2 = parentItemList.get(i - 1);
                parentItemList.set(i, p2);
                parentItemList.set(i - 1, p1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    /*Determines the position of the parent. Considering only the visible elements.*/
    public int getParentPositionOfPosition(int pos){
        int pCount = -1;
        for(int i = 0; i <= pos; i++){
            Object listItem = getListItem(i);
            if(listItem instanceof ParentWrapper){
                pCount++;
            }
        }
        return pCount;
    }

    /*Determines the position of the child. Considering only the visible elements.*/
    public int getChildPositionOfPosition(int pos){
        int cCount = -1;
        for(int i = 0; i <= pos; i++){
            Object listItem = getListItem(i);
            if(listItem instanceof ParentWrapper){
                cCount = -1;
            }else{
                cCount++;
            }
        }
        return cCount;
    }

    public ParentWrapper getParentWrapperOfPosition(int pos){
        ParentWrapper lastParent = null;
        for(int i = 0; i <= pos; i++){
            Object listItem = getListItem(i);
            if(listItem instanceof ParentWrapper){
                lastParent = (ParentWrapper) listItem;
            }
        }
        return lastParent;
    }

    /*Define the layout for the parent-elements*/
    @Override
    public ExpParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.exp_parent, viewGroup, false);
        return new ExpParentViewHolder(context, view, this, showParentDragArea);
    }

    /*Define the layout for the child-elements*/
    @Override
    public ExpChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.exp_child, viewGroup, false);
        return new ExpChildViewHolder(context, view, this, showChildDragArea);
    }

    /*Set up a parent*/
    @Override
    public void onBindParentViewHolder(final ExpParentViewHolder expParentViewHolder, int i, ParentListItem parentListItem) {
        Parent parent = (Parent) parentListItem;

        expParentViewHolder.parentTv.setText(parent.getText());

        expParentViewHolder.dragArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    onStartDragParentListener.onStartDragParent(expParentViewHolder);
                }
                return false;
            }
        });


    }

    /*Set up a child*/
    @Override
    public void onBindChildViewHolder(final ExpChildViewHolder expChildViewHolder, int i, Object childObject) {
        Child child = (Child) childObject;

        expChildViewHolder.childTv.setText(child.getText());

        expChildViewHolder.dragArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    onStartDragChildListener.onStartDragChild(expChildViewHolder);
                }
                return false;
            }
        });

    }

    boolean isParentAt(int position){
        Object dismissListItem = getListItem(position);
        if(dismissListItem instanceof ParentWrapper){
            return true;
        }
        else{
            return false;
        }
    }

    void showParentDragArea(boolean b){
        showParentDragArea = b;
        notifyDataSetChanged();
    }

    void showChildDragArea(boolean b){
        showChildDragArea = b;
        notifyDataSetChanged();
    }

}




