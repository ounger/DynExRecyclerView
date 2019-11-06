import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class ExpParentViewHolder extends ParentViewHolder implements ItemTouchHelperViewHolder {

    /*Important for arrow-rotation*/
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public ImageView parentDropDownArrow;
    public TextView parentTv;
    private ExpandableAdapter expAdapter;
    private Context context;
    public LinearLayout dragArea, contentArea, expButtonArea;

    public ExpParentViewHolder(Context c, View itemView, ExpandableAdapter ea, boolean showDragArea) {
        super(itemView);
        this.context = c;
        parentTv = (TextView) itemView.findViewById(R.id.parentTv);
        parentDropDownArrow = (ImageView) itemView.findViewById(R.id.parent_list_item_expand_arrow);
        parentDropDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });
        dragArea = (LinearLayout) itemView.findViewById(R.id.dragArea);
        if(showDragArea) dragArea.setVisibility(View.VISIBLE);
        else dragArea.setVisibility(View.GONE);
        contentArea = (LinearLayout) itemView.findViewById(R.id.content);
        expButtonArea = (LinearLayout) itemView.findViewById(R.id.expandButton);
        this.expAdapter = ea;
    }

    @Override
    public void onItemSelected() {
        if(expAdapter != null){
            expAdapter.blocked = true;
            expAdapter.state = ExpandableAdapter.SELECTED;

            dragArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark_drag));
            contentArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_drag));
            expButtonArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_drag));

        }
    }

    @Override
    public void onItemClear() {
        if(expAdapter != null){
            expAdapter.state = ExpandableAdapter.WAIT;

            dragArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
            contentArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            expButtonArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            expAdapter.blocked = false;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        if (expanded) {
            parentDropDownArrow.setRotation(ROTATED_POSITION);
        } else {
            parentDropDownArrow.setRotation(INITIAL_POSITION);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
        rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
        rotateAnimation.setFillAfter(true);
        parentDropDownArrow.startAnimation(rotateAnimation);

    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }
}
