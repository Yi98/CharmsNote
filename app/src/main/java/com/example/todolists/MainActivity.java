package com.example.todolists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRecyclerView swipeRecyclerView;
    private TaskAdapter adapter;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRecyclerView = findViewById(R.id.swipe_recyclerView);
        floatingActionButton = findViewById(R.id.fab);

        ArrayList<Task> tasks = new ArrayList<>();

        addItemsToList(tasks);

        adapter = new TaskAdapter(tasks);

        // 设置监听器。
        swipeRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        swipeRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        // set-up swipe recycler view
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        swipeRecyclerView.setAdapter(adapter);
    }

    private void addItemsToList(ArrayList items) {
        items.add(new Task("Hello"));
        items.add(new Task("boba"));
        items.add(new Task("wwd"));
    }


    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // Use either match parent or specific height
//                int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = 75;

            // right menu
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this).setBackground(R.drawable.ic_launcher_foreground)
//                            .setImage(R.drawable.ic_launcher_foreground)
                        .setText("Delete")
                        .setTextColor(Color.BLACK)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this).setBackground(R.drawable.ic_launcher_foreground)
//                            .setImage(R.drawable.ic_launcher_foreground)
                        .setText("Edit")
                        .setTextColor(Color.BLACK)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };


    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(MainActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(MainActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

}
