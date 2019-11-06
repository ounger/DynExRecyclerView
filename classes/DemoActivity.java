import android.app.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends Activity{

    private List<Parent> parentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Generate some data. Each parent has 5 childelements*/
        for(int i = 1; i < 6; i++){
            List<Child> children = new ArrayList<>();
            for(int j = 1; j < 6; j++)
                children.add(new Child("Child " + j + " from Parent " + i));
            parentList.add(new Parent("Parent " + i, children));
        }

        ExpRecyclerView expRecyclerView = new ExpRecyclerView(
                this,
                (RecyclerView) findViewById(R.id.exp_view),
                (TextView) findViewById(R.id.empty_view),
                parentList
        );
        //expRecyclerView.disableParentsDrag();
        //expRecyclerView.disableChildrenDrag();
        //expRecyclerView.disableParentsSwipe();
        //expRecyclerView.disableChildrenSwipe();
        //expRecyclerView.disableParentsDrag();
        //expRecyclerView.disableChildrenDrag();
        //expRecyclerView.enableParentsDrag();
        //expRecyclerView.enableChildrenDrag();

        List<Child> addedChildren = new ArrayList<>();
        addedChildren.add(new Child("Dynamically added child 1"));
        Parent addedParent = new Parent("Dynamically added parent", addedChildren);
        expRecyclerView.addParent(addedParent);

        Child addedChild = new Child("Dynamically added child 2");
        expRecyclerView.addChild(addedChild);
    }
}

