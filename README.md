# DraggedViewPager
A View whose pages and items both can be dragged, looking like a ViewPager
<img src="https://github.com/yueban/DraggedViewPager/raw/master/DraggedViewPager.gif" width=“300" alt="Screenshot"/>

## Usage
###  Quick Start
Define in xml
``` xml
<com.bigfat.draggedviewpager.view.MDA_DraggedViewPager xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/draggedViewPager"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
Setup Controller
``` java
MDA_DraggedViewPagerController controller = new MDA_DraggedViewPagerController<Object>(itemLayoutRes) {
    @Override
    public void bindItemData(View itemView, Object o) {
        //bind data to itemView
    }
};
draggedViewPager.setController(controller);
draggedViewPager.setItemMoveDelay(200);//Default 300
draggedViewPager.setPageSwapDelay(400);//Default 300
```

### Callback
``` java
draggedViewPager.setDraggedViewPagerListener(new MDA_DraggedViewPagerListener() {
    @Override
    public void onDragStarted() {

    }

    @Override
    public void onDragEnded() {

    }

    @Override
    public void onPageSwapped(int firstPageIndex, int secondPageIndex) {

    }

    @Override
    public void onItemMoved(int oldPageIndex, int oldItemIndex, int newPageIndex, int newItemIndex) {

    }
});
```

### Control DraggedViewPager
``` java
controller.swapPages(int firstIndex, int secondIndex);
controller.addPage(int pageIndex, Page<T> page);
controller.removePage(int pageIndex);
controller.addItem(int pageIndex, int itemIndex, T item);
controller.removeItem(int pageIndex, int itemIndex);
controller.updateItem(int pageIndex, int itemIndex, T item);
controller.moveItem(int oldPageIndex, int oldItemIndex, int newPageIndex, int newItemIndex)
```

url:https://github.com/yueban/DraggedViewPager