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
ExpRecyclerView expRecyclerView = new ExpRecyclerView(
      this,
      (RecyclerView) findViewById(R.id.exp_view),
      (TextView) findViewById(R.id.empty_view),
      parentList
);
```

