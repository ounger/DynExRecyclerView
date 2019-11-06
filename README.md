# DynExRecyclerView
Dynamic Expandable RecyclerView for Android

## Features
- You can use **Drag & Drop** for reordering parent and child items
- You can **Swipe to Dismiss** for parent and child items
- You can **customize** parent and child **layouts**
- You can **dynamically add new items**
- You can **dynamically enable or disable** *Drag & Drop* and *Swipe to Dismiss* for parent and/or child items 
- Easily add new rules for adding and reordering items

## Implementation
Just copy the classes, the layouts and the content of the .txt files to your project.

## Usage
Start checking out **DemoActivity.java**. Snippet:

```java
/*Generate some data. 5 parents. Each parent has 5 childelements.*/
for(int i = 1; i < 6; i++){
      List<Child> children = new ArrayList<>();
      for(int j = 1; j < 6; j++)
          children.add(new Child("Child " + j + " from Parent " + i));
      parentList.add(new Parent("Parent " + i, children));
}

/*Setup the recyclerview with the generated data.*/
ExpRecyclerView expRecyclerView = new ExpRecyclerView(
      this,
      (RecyclerView) findViewById(R.id.exp_view),
      (TextView) findViewById(R.id.empty_view), //(optional param) if no items are in the list show a TextView "list is empty"
      parentList
);
```
If you dont need the EmptyTextView use the other constructor: 
