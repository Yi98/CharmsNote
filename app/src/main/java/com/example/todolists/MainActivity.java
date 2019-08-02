package com.example.todolists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;
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
    private Toolbar toolbar;
    private ArrayList<Task> tasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRecyclerView = findViewById(R.id.swipe_recyclerView);
        floatingActionButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        addItemsToList(tasks);

        adapter = new TaskAdapter(tasks);

        // 设置监听器。
        swipeRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        swipeRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        // set-up swipe recycler view
        swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        swipeRecyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(clickListener);


        SimpleTarget simpleTarget = new SimpleTarget.Builder(this)
                .setPoint(965f, 1678f)
                .setShape(new Circle(80f)) // or RoundedRectangle()
                .setTitle("Add tasks")
                .setDescription("Add a new task to the goal")
                .setOverlayPoint(450f, 1300f)
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                        // do something
                    }
                    @Override
                    public void onEnded(SimpleTarget target) {
                        // do something
                    }
                })
                .build();

        Spotlight.with(this)
                .setOverlayColor(R.color.background)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(simpleTarget)
                .setClosedOnTouchedOutside(true)
                .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                    @Override
                    public void onStarted() {
//                        Toast.makeText(MainActivity.this, "spotlight is started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEnded() {
//                        Toast.makeText(MainActivity.this, "spotlight is ended", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();

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
            int height = 175;

            // right menu
            {
                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.ic_launcher_background)
                        .setImage(R.drawable.edit)
                        .setText("Edit")
                        .setTextColor(Color.BLACK)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。

                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.ic_launcher_foreground)
                        .setImage(R.drawable.archive)
                        .setText("Archive")
                        .setTextColor(Color.BLACK)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        }
    };


    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, final int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();

                if (menuPosition == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    final EditText edittext = new EditText(MainActivity.this);
                    edittext.setText(adapter.getItem(position).getTitle());

//            alert.setMessage("How about clean the room?");
                    alert.setTitle("Edit task");

                    alert.setView(edittext);

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String newItem = edittext.getText().toString();
                            adapter.getItem(position).setTitle(newItem);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert.show();
                }
                else if (menuPosition == 1) {
                    tasks.remove(position);
                }

                adapter.notifyDataSetChanged();

            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            final EditText edittext = new EditText(MainActivity.this);
//            alert.setMessage("How about clean the room?");
            alert.setTitle("Add a new task");

            alert.setView(edittext);

            alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String newItem = edittext.getText().toString();
                    tasks.add(new Task(newItem));
                    adapter.notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
