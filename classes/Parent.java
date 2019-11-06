import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Parent implements ParentListItem {

    private String text;
    private List<Child> children;

    public Parent(String text, List<Child> children){
        this.text = text;
        this.children = children;
    }

    @Override
    public List<Child> getChildItemList() {
        return children;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
