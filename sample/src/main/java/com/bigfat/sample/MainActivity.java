package com.bigfat.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigfat.draggedviewpager.model.Page;
import com.bigfat.draggedviewpager.utils.MDA_DraggedViewPagerController;
import com.bigfat.draggedviewpager.utils.MDA_DraggedViewPagerListener;
import com.bigfat.draggedviewpager.view.MDA_DraggedViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private MDA_DraggedViewPager draggedViewPager;
    private MDA_DraggedViewPagerController<Item> controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draggedViewPager = (MDA_DraggedViewPager) findViewById(R.id.draggedViewPager);
        controller = new SimpleController(generatePageList(), R.layout.item);
        draggedViewPager.setController(controller);
        draggedViewPager.setItemMoveDelay(200);
        draggedViewPager.setPageSwapDelay(400);
        draggedViewPager.setDraggedViewPagerListener(new MDA_DraggedViewPagerListener() {
            @Override
            public void onDragStarted() {
                Log.i(TAG, "onDragStarted");
            }

            @Override
            public void onDragEnded() {
                Log.i(TAG, "onDragEnded");
            }

            @Override
            public void onPageSwapped(int firstPageIndex, int secondPageIndex) {
                Log.i(TAG, "onPageSwapped [firstPageIndex:" + firstPageIndex + ", secondPageIndex:" + secondPageIndex + "]");
            }

            @Override
            public void onItemMoved(int oldPageIndex, int oldItemIndex, int newPageIndex, int newItemIndex) {
                Log.i(TAG, "onPageSwapped [oldPageIndex:" + oldPageIndex + ", oldItemIndex:" + oldItemIndex + ", newPageIndex:" + newPageIndex + ", newItemIndex:" + newItemIndex + "]");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_page:
                controller.addPage(0, new Page<>("add", generateItemList("add-", 5)));
                break;

            case R.id.action_remove_page:
                controller.removePage(0);
                break;

            case R.id.action_swap_page:
                controller.swapPages(0, 1);
                break;

            case R.id.action_add_item:
                controller.addItem(0, 0, new Item(R.drawable.abc_btn_radio_to_on_mtrl_000, "add"));
                break;

            case R.id.action_remove_item:
                controller.removeItem(0, 0);
                break;

            case R.id.action_update_item:
                controller.updateItem(0, 0, new Item(R.drawable.abc_btn_radio_to_on_mtrl_000, "update"));
                break;

            case R.id.action_move_item:
                controller.moveItem(0, 0, 1, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Page<Item>> generatePageList() {
        ArrayList<Page<Item>> data = new ArrayList<>();

        data.add(new Page<>("页面1", generateItemList("1-", 20)));
        data.add(new Page<>("页面2", generateItemList("2-", 10)));
        data.add(new Page<>("页面3", generateItemList("3-", 35)));
        data.add(new Page<>("页面4", generateItemList("4-", 50)));

        return data;
    }

    private ArrayList<Item> generateItemList(String title, int size) {
        ArrayList<Item> itemList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Item item = new Item(R.mipmap.ic_launcher, title + i);
            itemList.add(item);
        }
        return itemList;
    }

    private class SimpleController extends MDA_DraggedViewPagerController<Item> {
        public SimpleController(ArrayList<Page<Item>> data, int itemLayoutRes) {
            super(data, itemLayoutRes);
        }

        @Override
        public void bindItemData(View itemView, Item item) {
            ImageView img = (ImageView) itemView.findViewById(R.id.img_item);
            TextView tv = (TextView) itemView.findViewById(R.id.tv_item);
            img.setImageResource(item.getImgRes());
            tv.setText(item.getText());
        }
    }
}