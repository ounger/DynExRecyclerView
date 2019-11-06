import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class ExpChildViewHolder extends ChildViewHolder implements ItemTouchHelperViewHolder {

    private ExpandableAdapter expAdapter;
    private Context context;

    TextView childTv;
    LinearLayout dragArea, contentArea;

    public ExpChildViewHolder(Context c, View itemView, ExpandableAdapter ea, boolean showDragArea) {
        super(itemView);
        this.context = c;
        this.childTv = (TextView) itemView.findViewById(R.id.childTv);
        this.dragArea = (LinearLayout) itemView.findViewById(R.id.dragArea);
        if(showDragArea) dragArea.setVisibility(View.VISIBLE);
        else dragArea.setVisibility(View.GONE);
        this.contentArea = (LinearLayout) itemView.findViewById(R.id.content);
        this.expAdapter = ea;

    }

    @Override
    public void onItemSelected() {
        if(expAdapter != null){
            expAdapter.blocked = true;
            expAdapter.state = ExpandableAdapter.SELECTED;

            dragArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark_drag));
            contentArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_drag));

        }
    }

    @Override
    public void onItemClear() {
        if(expAdapter != null){
            if(expAdapter.state == ExpandableAdapter.ON_MOVE_CHILD){

                /*Troubleshooting*/
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        expAdapter.childMoveEnd();
                    }
                };
                handler.post(r);

                expAdapter.state = ExpandableAdapter.WAIT;
            }

            dragArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            contentArea.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            expAdapter.blocked = false;

        }
    }
}
