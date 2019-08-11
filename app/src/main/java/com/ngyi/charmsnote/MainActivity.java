package com.ngyi.charmsnote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private ExpandingList expandingList;
    private boolean editing = false;
    private TaskDbHelper dbHelper;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.ngyi.todolists", MODE_PRIVATE);

        dbHelper = new TaskDbHelper(MainActivity.this);


        List<Task> tasks = dbHelper.getAllTasks();

        floatingActionButton = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        floatingActionButton.setOnClickListener(clickListener);

        expandingList = findViewById(R.id.expanding_list_main);
        ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                // il is new scrollY, i3 is old scrollY
                if (i1 > i3) {
                    floatingActionButton.hide();
                }
                else if (i1< i3  && !editing) {
                    floatingActionButton.show();
                }

            }
        });


        for (int i=0; i<dbHelper.getTasksCount(); i++) {
            ArrayList<String> subtasks = TaskDbHelper.convertStringToArray(tasks.get(i).getSubtasks());

            addItem(tasks.get(i).getTask(), subtasks, tasks.get(i).getColor(), R.drawable.whatshot);
        }
    }


    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        int[] location = new int[2];
        floatingActionButton.getLocationOnScreen(location);
        int width = location[0];
        int height = location[1];

        if (prefs.getBoolean("firstrun", true)) {
            setupSpotlight(width, height);
            prefs.edit().putBoolean("firstrun", false).apply();
        }


    }


    private void addItem(String title, ArrayList<String> subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);
        List<Task> tasks = dbHelper.getAllTasks();

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

            TextView hiddenId = item.findViewById(R.id.hiddenDbId);


            for (int i=0; i<dbHelper.getTasksCount(); i++) {
                if (title.equalsIgnoreCase(tasks.get(i).getTask())) {
                    hiddenId.setText(Integer.toString(tasks.get(i).getId()));
                    break;
                }
            }


            //We can create items in batch.
            item.createSubItems(subItems.size());
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                if (i == item.getSubItemsCount() - 1) {
                    configureSubItem(item, view, subItems.get(i), true, item.getSubItemsCount()-1);
                } else {
                    configureSubItem(item, view, subItems.get(i), false, i);
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

                            TextView hiddenId = item.findViewById(R.id.hiddenDbId);
                            int id = Integer.parseInt(hiddenId.getText().toString());

                            final Task deleteTask = dbHelper.getTask(id);
                            dbHelper.deleteTask(deleteTask);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                }
            });
        }

        if (item != null) {
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

    }


    private void configureSubItem(final ExpandingItem item, final View view, String subTitle, boolean isLastItem, final int index) {
        final TextView tv = view.findViewById(R.id.sub_title);
        ImageView removeSub = view.findViewById(R.id.remove_sub_item);

        ViewParent parent = view.getParent();
        ViewParent grandparent = ((ViewGroup) parent).getParent();
        View temp = (View) grandparent;

        tv.setText(subTitle);

        TextView hiddenId = temp.findViewById(R.id.hiddenDbId);
        int id = Integer.parseInt(hiddenId.getText().toString());

        Task currentTask = dbHelper.getTask(id);
        ArrayList<String> statusList = TaskDbHelper.convertStringToArray(currentTask.getStatus());


        if (statusList.get(index).equalsIgnoreCase("false")) {
            removeSub.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.checkbox_black));
            tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
        else if (statusList.get(index).equalsIgnoreCase("true")){
            removeSub.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.checkbox_complete_black));
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


        if (isLastItem) {
            removeSub.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.add_black));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editing) {
                        showInsertDialog(new OnItemCreated() {
                            @Override
                            public void itemCreated(String title) {
                                View newSubItem = item.createSubItem(item.getSubItemsCount() - 1);
                                configureSubItem(item, newSubItem, title, false, item.getSubItemsCount()-2);
                            }
                        }, view);
                    }
                }
            });
        }
        else {
            view.setOnClickListener(new View.OnClickListener() {
                ImageView removeSub = view.findViewById(R.id.remove_sub_item);
                boolean checked = false;

                @Override
                public void onClick(View v) {
                    if (tv.getPaintFlags() == 1297) {
                        removeSub.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                        tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        checked = false;
                    }
                    else {
                        removeSub.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        checked = true;
                    }


                    ViewParent parent = view.getParent();
                    ViewParent grandparent = ((ViewGroup) parent).getParent();
                    View temp = (View) grandparent;

                    TextView hiddenId = temp.findViewById(R.id.hiddenDbId);
                    int id = Integer.parseInt(hiddenId.getText().toString());

                    Task oldTask = dbHelper.getTask(id);

                    ArrayList<String> subTasks = TaskDbHelper.convertStringToArray(oldTask.getSubtasks());
                    ArrayList<String> subStatus = TaskDbHelper.convertStringToArray(oldTask.getStatus());

                        if (subTasks.get(index).equalsIgnoreCase(tv.getText().toString()) && checked) {
                            subStatus.set(index, "true");
                        }
                        else if (subTasks.get(index).equalsIgnoreCase(tv.getText().toString()) && !checked) {
                            subStatus.set(index, "false");
                        }

                    String concatStatus = TaskDbHelper.convertArrayToString(subStatus);

                    Task updatedTask = new Task(oldTask.getId(), oldTask.getTask(), oldTask.getColor(), oldTask.getSubtasks(), concatStatus);
                    dbHelper.updateTask(updatedTask);
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

                Task oldTask = dbHelper.getTask(id);

                ArrayList<String> subTasks = TaskDbHelper.convertStringToArray(oldTask.getSubtasks());
                ArrayList<String> subStatus = TaskDbHelper.convertStringToArray(oldTask.getStatus());

                subTasks.add(subTasks.size()-1, text.getText().toString());
                subStatus.add(subStatus.size()-1, "false");

                String concatTasks = TaskDbHelper.convertArrayToString(subTasks);
                String concatStatus = TaskDbHelper.convertArrayToString(subStatus);

                Task updatedTask = new Task(id, oldTask.getTask(), oldTask.getColor(), concatTasks, concatStatus);
                dbHelper.updateTask(updatedTask);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    // Disable ok button
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });
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
                                    colorId = R.color.color1;
                            }


                            ArrayList<String> starterSub = new ArrayList<>();
                            ArrayList<String> starterStatus = new ArrayList<>();

                            starterSub.add("Add a new sub-task");
                            starterStatus.add("null");


                            long id = dbHelper.insertTask(newItem, colorId, starterSub, starterStatus);

                            addItem(newItem, starterSub, colorId, R.drawable.whatshot);
                        }

                        @Override
                        public void onCancel(){
                            // put code
                            return;
                        }
                    });

                    colorPicker.show();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            final AlertDialog dialog = alert.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    // Check if edittext is empty
                    if (TextUtils.isEmpty(s)) {
                        // Disable ok button
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    } else {
                        // Something into edit text. Enable the button.
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                }
            });

        }
    };


    private void setupSpotlight(int width, int height) {
        SimpleTarget simpleTarget = null;

        SimpleTarget simpleTargetPotrait = new SimpleTarget.Builder(this)
                .setPoint(width + 73, height + 73)
                .setShape(new Circle(90f)) // or RoundedRectangle()
                .setTitle("Add a new task")
//                .setDescription("You can add more tasks later")
                .setOverlayPoint(width - 550f, height - 50f)
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

        SimpleTarget simpleTargetLandscape = new SimpleTarget.Builder(this)
                .setPoint(width - 3, height + 73)
                .setShape(new Circle(90f)) // or RoundedRectangle()
                .setTitle("Add a new task")
//                .setDescription("You can add more tasks later")
                .setOverlayPoint(width - 600f, height - 50f)
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

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            simpleTarget = simpleTargetPotrait;
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            simpleTarget = simpleTargetLandscape;
        }

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
        ExpandingList expandingList = findViewById(R.id.expanding_list_main);
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.rotate);
        FloatingActionButton fab = findViewById(R.id.fab);

        //noinspection SimplifiableIfStatement
        if (!editing) {
            item.setIcon(R.drawable.done_black);
            fab.hide();

            for (int i = 0; i < expandingList.getItemsCount(); i++) {
                final View view = expandingList.getItemByIndex(i);
                ImageView deleteImg = view.findViewById(R.id.remove_item);


                for (int j = 0; j <= ((ExpandingItem) view).getSubItemsCount() - 1; j++) {
                    final int currentIndex = j;
                    final View subView = ((ExpandingItem) view).getSubItemView(j);
                    final ExpandingItem subItem = ((ExpandingItem) view);

                    ImageView subImg = subView.findViewById(R.id.remove_sub_item);

                    if (j == ((ExpandingItem) view).getSubItemsCount() - 1) {
                        TextView subtitle = subView.findViewById(R.id.sub_title);
                        subImg.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.disabled));

                        subtitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.disabled));
                        break;
                    }

                    subImg.setImageDrawable(getResources().getDrawable(R.drawable.clear_black));
                    subImg.startAnimation(animShake);

                    subView.setOnClickListener(null);

                    subImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ViewParent parent = subView.getParent();
                            ViewParent grandparent = ((ViewGroup) parent).getParent();
                            View temp = (View) grandparent;

                            TextView hiddenId = temp.findViewById(R.id.hiddenDbId);
                            TextView subtask = subView.findViewById(R.id.sub_title);

                            String subtitle =  subtask.getText().toString();
                            int id = Integer.parseInt(hiddenId.getText().toString());

                            Task oldTask = dbHelper.getTask(id);

                            ArrayList<String> subTasks = TaskDbHelper.convertStringToArray(oldTask.getSubtasks());
                            ArrayList<String> subStatus = TaskDbHelper.convertStringToArray(oldTask.getStatus());

                            subTasks.remove(currentIndex);
                            subStatus.remove(currentIndex);

                            String concatTasks = TaskDbHelper.convertArrayToString(subTasks);
                            String concatStatus = TaskDbHelper.convertArrayToString(subStatus);

                            Task updatedTask = new Task(oldTask.getId(), oldTask.getTask(), oldTask.getColor(), concatTasks, concatStatus);

                            dbHelper.updateTask(updatedTask);
                            subItem.removeSubItem(subView);
                        }
                    });
                }


                deleteImg.setVisibility(View.VISIBLE);
                deleteImg.startAnimation(animShake);
            }

            editing = true;
            return true;

        } else if (editing) {

            fab.show();
            item.setIcon(R.drawable.pen_black);

            for (int i = 0; i < expandingList.getItemsCount(); i++) {
                View view = expandingList.getItemByIndex(i);
                ImageView deleteImg = view.findViewById(R.id.remove_item);

                for (int j = 0; j <= ((ExpandingItem) view).getSubItemsCount() - 1; j++) {
                    final int currentIndex = j;

                    final View subView = ((ExpandingItem) view).getSubItemView(j);
                    final ImageView subImg = subView.findViewById(R.id.remove_sub_item);

                    final TextView tv = subView.findViewById(R.id.sub_title);

                    if (j == ((ExpandingItem) view).getSubItemsCount() - 1) {
                        subImg.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black));
                        tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                        break;
                    }


                    if (tv.getPaintFlags() == 1297) {
                        subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                    }
                    else {
                        subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                    }

                    subImg.setOnClickListener(null);

                    subImg.setOnClickListener(new View.OnClickListener() {
                        boolean checked = false;

                        @Override
                        public void onClick(View view) {
                            if (tv.getPaintFlags() == 1297) {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                checked = false;
                            }
                            else {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                checked = true;
                            }

                            // get subitem view
                            ViewParent parentOuter = view.getParent();
                            ViewParent grandparentOuter = ((ViewGroup) parentOuter).getParent();
                            View tempOuter = (View) grandparentOuter;

                            // get item view
                            ViewParent parent = tempOuter.getParent();
                            ViewParent grandparent = ((ViewGroup) parent).getParent();
                            View temp = (View) grandparent;

                            TextView hiddenId = temp.findViewById(R.id.hiddenDbId);
                            int id = Integer.parseInt(hiddenId.getText().toString());

                            Task oldTask = dbHelper.getTask(id);

                            ArrayList<String> subTasks = TaskDbHelper.convertStringToArray(oldTask.getSubtasks());
                            ArrayList<String> subStatus = TaskDbHelper.convertStringToArray(oldTask.getStatus());

                            if (subTasks.get(currentIndex).equalsIgnoreCase(tv.getText().toString()) && checked) {
                                subStatus.set(currentIndex, "true");
                            }
                            else if (subTasks.get(currentIndex).equalsIgnoreCase(tv.getText().toString()) && !checked) {
                                subStatus.set(currentIndex, "false");
                            }

                            String concatStatus = TaskDbHelper.convertArrayToString(subStatus);

                            Task updatedTask = new Task(oldTask.getId(), oldTask.getTask(), oldTask.getColor(), oldTask.getSubtasks(), concatStatus);
                            dbHelper.updateTask(updatedTask);
                        }
                    });

                    subView.setOnClickListener(new View.OnClickListener() {
                        boolean checked = false;

                        @Override
                        public void onClick(View view) {
                            if (tv.getPaintFlags() == 1297) {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_black));
                                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                checked = false;
                            }
                            else {
                                subImg.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_complete_black));
                                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                checked = true;
                            }

                            // TODO: 9/8/2019 update here
                            ViewParent parent = view.getParent();
                            ViewParent grandparent = ((ViewGroup) parent).getParent();
                            View temp = (View) grandparent;

                            TextView hiddenId = temp.findViewById(R.id.hiddenDbId);
                            int id = Integer.parseInt(hiddenId.getText().toString());

                            Task oldTask = dbHelper.getTask(id);

                            ArrayList<String> subTasks = TaskDbHelper.convertStringToArray(oldTask.getSubtasks());
                            ArrayList<String> subStatus = TaskDbHelper.convertStringToArray(oldTask.getStatus());

                            if (subTasks.get(currentIndex).equalsIgnoreCase(tv.getText().toString()) && checked) {
                                subStatus.set(currentIndex, "true");
                            }
                            else if (subTasks.get(currentIndex).equalsIgnoreCase(tv.getText().toString()) && !checked) {
                                subStatus.set(currentIndex, "false");
                            }

                            String concatStatus = TaskDbHelper.convertArrayToString(subStatus);

                            Task updatedTask = new Task(oldTask.getId(), oldTask.getTask(), oldTask.getColor(), oldTask.getSubtasks(), concatStatus);
                            dbHelper.updateTask(updatedTask);
                        }
                    });

                    subImg.clearAnimation();

                }

                deleteImg.clearAnimation();
                deleteImg.setVisibility(View.GONE);
            }

            editing = false;
        }

        return super.onOptionsItemSelected(item);
    }
}
