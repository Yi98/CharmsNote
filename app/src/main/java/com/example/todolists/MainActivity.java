package com.example.todolists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {

    private TaskAdapter adapter;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private ExpandingList expandingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSpotlight();

        expandingList = findViewById(R.id.expanding_list_main);

        createItems();

        floatingActionButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        floatingActionButton.setOnClickListener(clickListener);
    }


    private void createItems() {
        addItem("Homework", new String[]{"Computer System", "Intro to programming"}, R.color.color1, R.drawable.pen);
        addItem("Grocery", new String[]{"Banana", "Eggs", "Chicken Breast"}, R.color.color1, R.drawable.pen);
    }


    private void addItem(String title, String[] subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);

            //We can create items in batch.
            item.createSubItems(subItems.length);
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems[i]);
            }

            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.toggleExpanded();
                }
            });

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure you want to remove?");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            expandingList.removeItem(item);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                }
            });
        }

        item.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
            ImageView addSubImg = item.findViewById(R.id.add_more_sub_items);

            @Override
            public void itemCollapseStateChanged(boolean expanded) {
                if (item.isExpanded()) {
                    addSubImg.animate().rotation(180).start();
                }
                else {
                    addSubImg.animate().rotation(0).start();
                }
            }
        });
    }

    private void configureSubItem(final ExpandingItem item, final View view, String subTitle) {
        final TextView tv = view.findViewById(R.id.sub_title);

        tv.setText(subTitle);

        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.getPaintFlags() == 1297) {
                    item.removeSubItem(view);
                }
                else {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
    }

    private void showInsertDialog(final OnItemCreated positive, View view) {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(text);
        builder.setTitle("Add a sub-item");


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                positive.itemCreated(text.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.show();
    }

    interface OnItemCreated {
        void itemCreated(String title);
    }


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
                    final String newItem = edittext.getText().toString();

                    ColorPicker colorPicker = new ColorPicker(MainActivity.this);
                    colorPicker.show();
                    colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                        @Override
                        public void onChooseColor(int position, int color) {
                            int colorId;

                            switch (position) {
                                case 0:
                                    colorId = R.color.color1;
                                    break;
                                case 1:
                                    colorId = R.color.color2;
                                    break;
                                case 2:
                                    colorId = R.color.color3;
                                    break;
                                case 3:
                                    colorId = R.color.color4;
                                    break;
                                case 4:
                                    colorId = R.color.color5;
                                    break;
                                case 5:
                                    colorId = R.color.color6;
                                    break;
                                case 6:
                                    colorId = R.color.color7;
                                    break;
                                case 7:
                                    colorId = R.color.color8;
                                    break;
                                case 8:
                                    colorId = R.color.color9;
                                    break;
                                case 9:
                                    colorId = R.color.color10;
                                    break;
                                case 10:
                                    colorId = R.color.color11;
                                    break;
                                case 11:
                                    colorId = R.color.color12;
                                    break;
                                case 12:
                                    colorId = R.color.color13;
                                    break;
                                case 13:
                                    colorId = R.color.color14;
                                    break;
                                case 14:
                                    colorId = R.color.color15;
                                    break;
                                default:
                                    colorId = R.color.white;
                            }

                            addItem(newItem, new String[]{"Add a new sub-task"}, colorId, R.drawable.pen);
                        }

                        @Override
                        public void onCancel(){
                            // put code
                        }
                    });

                    // add color sheet


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


    private void setupSpotlight() {
        SimpleTarget simpleTarget = new SimpleTarget.Builder(this)
                .setPoint(965f, 1678f)
                .setShape(new Circle(90f)) // or RoundedRectangle()
                .setTitle("Add new task")
                .setDescription("You can add more tasks later")
                .setOverlayPoint(400f, 1300f)
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

        ExpandingList expandingList = findViewById(R.id.expanding_list_main);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {

            for (int i = 0; i < expandingList.getItemsCount(); i++) {
                View view = expandingList.getItemByIndex(i);
                ImageView deleteImg = view.findViewById(R.id.remove_item);
                deleteImg.setVisibility(View.VISIBLE);
            }

            Toast.makeText(MainActivity.this, "Edit mode", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
