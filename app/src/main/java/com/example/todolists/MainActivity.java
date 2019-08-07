package com.example.todolists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.transition.TransitionManager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private ExpandingList expandingList;
    private boolean editing = false;
    private ScrollView scrollView;
    private TaskDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(MainActivity.this);


        List<Task> tasks = dbHelper.getAllNotes();

        floatingActionButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        floatingActionButton.setOnClickListener(clickListener);

        setupSpotlight();

        expandingList = findViewById(R.id.expanding_list_main);
        scrollView = findViewById(R.id.scrollView);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                // il is new scrollY, i3 is old scrollY
                if (i1 > i3) {
                    floatingActionButton.hide();
                }
                else if (i1< i3) {
                    floatingActionButton.show();
                }

            }
        });


        createItems();

        for (int i=0; i<dbHelper.getNotesCount(); i++) {
            ArrayList<String> subtasks = dbHelper.convertStringToArray(tasks.get(i).getSubtasks());

            addItem(tasks.get(i).getNote(), subtasks, tasks.get(i).getColor(), R.drawable.pen);
        }
    }


    private void createItems() {
        ArrayList<String> homeworks = new ArrayList<>();
        homeworks.add("Computer System");
        homeworks.add("Intro to programming");
        homeworks.add("Add a new sub-task");

        ArrayList<String> grocery = new ArrayList<>();
        grocery.add("Banana");
        grocery.add("Eggs");
        grocery.add("Add a new sub-task");


        addItem("Homework", homeworks, R.color.color1, R.drawable.pen);
        addItem("Grocery", grocery, R.color.color3, R.drawable.pen);
    }


    private void addItem(String title, ArrayList<String> subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);
        List<Task> tasks = dbHelper.getAllNotes();

//        final TextView oriTitle = item.findViewById(R.id.title);

//        final EditText editTitle = item.findViewById(R.id.editText);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);


            // update title code
//            ((TextView) item.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    oriTitle.setVisibility(View.GONE);
//                    editTitle.setVisibility(View.VISIBLE);
//
//                    editTitle.setText(oriTitle.getText());
//
//                    editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                        @Override
//                        public void onFocusChange(View view, boolean b) {
//                            if (!b) {
//                                oriTitle.setText(editTitle.getText());
//                                oriTitle.setVisibility(View.VISIBLE);
//                                editTitle.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//
//                }
//            });

            TextView hiddennId = item.findViewById(R.id.hiddenDbId);

            for (int i=0; i<dbHelper.getNotesCount(); i++) {
                if (title.equalsIgnoreCase(tasks.get(i).getNote())) {
                    hiddennId.setText(Integer.toString(tasks.get(i).getId()));
                }
                else {
                    hiddennId.setText("None");
                }
            }


            //We can create items in batch.
            item.createSubItems(subItems.size());
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                if (i == item.getSubItemsCount() - 1) {
                    configureSubItem(item, view, subItems.get(i), true);
                } else {
                    configureSubItem(item, view, subItems.get(i), false);
                }
                //Let's set some values in
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

    private void configureSubItem(final ExpandingItem item, final View view, String subTitle, boolean isLastItem) {
        final TextView tv = view.findViewById(R.id.sub_title);

        tv.setText(subTitle);

        if (tv.getText() == "Add a new sub-task") {
            ImageView removeSubImg = view.findViewById(R.id.remove_sub_item);
            removeSubImg.setImageDrawable(getResources().getDrawable(R.drawable.add_black));
        }

        if (isLastItem) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInsertDialog(new OnItemCreated() {
                        @Override
                        public void itemCreated(String title) {
                            View newSubItem = item.createSubItem(item.getSubItemsCount() - 1);
                            configureSubItem(item, newSubItem, title, false);
                        }
                    }, view);
                }
            });
        }
        else {
            view.setOnClickListener(new View.OnClickListener() {
                ImageView removeSub = view.findViewById(R.id.remove_sub_item);

                @Override
                public void onClick(View v) {
                    if (tv.getPaintFlags() == 1297) {
                        removeSub.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                        tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    else {
                        removeSub.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            });
        }
    }

    private void showInsertDialog(final OnItemCreated positive, final View view) {
        final EditText text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(text);
        builder.setTitle("Add a sub-item");


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positive.itemCreated(text.getText().toString());

                ViewParent parent = view.getParent();
                ViewParent grandparent = ((ViewGroup) parent).getParent();
                View temp = (View) grandparent;

                TextView hiddenId = temp.findViewById(R.id.hiddenDbId);

                int id = Integer.parseInt(hiddenId.getText().toString());

                // TODO: 6/8/2019 update task get position from list

                Log.d("hlb", "lol");


                Task oldTask = dbHelper.getNote(id);



                ArrayList<String> subTasks = dbHelper.convertStringToArray(oldTask.getSubtasks());
                subTasks.add(text.getText().toString());
                String concatTasks = dbHelper.convertArrayToString(subTasks);


                Task updatedTask = new Task(1, oldTask.getNote(), oldTask.getColor(), concatTasks);
                dbHelper.updateNote(updatedTask);
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
            if (editing) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("You are in edit mode!");
                builder.setMessage("Adding item can only be done in safe mode.");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return;
            }

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

                            ArrayList<String> starter = new ArrayList<>();
                            starter.add("Add a new sub-task");

                            long id = dbHelper.insertNote(newItem, colorId, starter);

                            addItem(newItem, starter, colorId, R.drawable.pen);
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
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.rotate);

        //noinspection SimplifiableIfStatement
        if (!editing) {
            for (int i = 0; i < expandingList.getItemsCount(); i++) {
                final View view = expandingList.getItemByIndex(i);
                ImageView deleteImg = view.findViewById(R.id.remove_item);

                for (int j = 0; j <= ((ExpandingItem) view).getSubItemsCount() - 2; j++) {
                    final View subView = ((ExpandingItem) view).getSubItemView(j);
                    final ExpandingItem subItem = ((ExpandingItem) view);

                    ImageView subImg = subView.findViewById(R.id.remove_sub_item);
                    subImg.setImageDrawable(getResources().getDrawable(R.drawable.clear_black));
                    subImg.startAnimation(animShake);

                    subView.setOnClickListener(null);

                    subImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            subItem.removeSubItem(subView);
                        }
                    });
                }


                deleteImg.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.done_black);

                deleteImg.startAnimation(animShake);

            }

            editing = true;
            Toast.makeText(MainActivity.this, "Edit mode", Toast.LENGTH_LONG).show();
            return true;

        } else if (editing) {
            for (int i = 0; i < expandingList.getItemsCount(); i++) {
                View view = expandingList.getItemByIndex(i);
                ImageView deleteImg = view.findViewById(R.id.remove_item);

                for (int j = 0; j < ((ExpandingItem) view).getSubItemsCount() - 1; j++) {
                    View subView = ((ExpandingItem) view).getSubItemView(j);
                    final ImageView subImg = subView.findViewById(R.id.remove_sub_item);

                    final TextView tv = subView.findViewById(R.id.sub_title);

                    if (tv.getPaintFlags() == 1297) {
                        subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                    }
                    else {
                        subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                    }

                    subImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (tv.getPaintFlags() == 1297) {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                            }
                            else {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }
                    });

                    subView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (tv.getPaintFlags() == 1297) {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                            }
                            else {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }
                    });

                    subImg.clearAnimation();

                }

                deleteImg.clearAnimation();
                deleteImg.setVisibility(View.GONE);
                item.setIcon(R.drawable.pen_black);
            }

            editing = false;
            Toast.makeText(MainActivity.this, "Safe mode", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
