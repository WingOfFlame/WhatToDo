package com.justinhu.whattodo.activity;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.justinhu.whattodo.MyFabMenu;
import com.justinhu.whattodo.R;
import com.justinhu.whattodo.TaskListAdapter;
import com.justinhu.whattodo.TaskSelector;
import com.justinhu.whattodo.db.CategoryDBHelper;
import com.justinhu.whattodo.db.TaskDbHelper;
import com.justinhu.whattodo.fragment.TaskDialog;
import com.justinhu.whattodo.model.Category;
import com.justinhu.whattodo.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskDialog.NewTaskDialogListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    public static final String CURRENT_TASK = "currentTask";
    private static final String TAG = "MainActivity";
    private static final int ONGOING_TASK_NOTIFICATION = 0;
    List<Task> mDataCopy;
    TaskDbHelper mTaskDbHelper;
    String[] filterValue;

    //TextView mTitle;
    Spinner filter;
    ListView taskList;
    TaskListAdapter mtaskListAdapter;
    View bottomSheetShadow;
    CoordinatorLayout ongoingTask;
    TextView taskName;
    ImageView taskCategory;
    RatingBar taskPriority;
    TextView taskCount;
    TextView taskDeadline;
    Button taskAbort;
    Button taskDid;
    Button randomSelect;
    Task currentTask = null;
    SharedPreferences storage;
    private Cursor oldCursor;
    private BottomSheetBehavior bottomSheetBehavior;
    private FragmentManager fragmentManager;
    private MyFabMenu fabMenu;
    private FloatingActionButton fabAddTask;
    private FloatingActionButton fabAddGoal;
    private FloatingActionButton fabAddHabbit;
    private int lastFilter = 0;
    private boolean needRefresh = false;
    private CategoryDBHelper mCategoryDBHelper;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        initConfig();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        filter = (Spinner) toolbar.findViewById(R.id.toolbar_filter);
        taskList = (ListView) findViewById(R.id.taskList);
        fabMenu = (MyFabMenu) findViewById(R.id.fab_menu);
        fabAddTask = fabMenu.getMenuButton();
        fabAddGoal = (FloatingActionButton) findViewById(R.id.fab_goal);
        fabAddHabbit = (FloatingActionButton) findViewById(R.id.fab_habbit);
        randomSelect = (Button) findViewById(R.id.select);
        bottomSheetShadow = findViewById(R.id.shadow);
        ongoingTask = (CoordinatorLayout) findViewById(R.id.view_ongoing_task);
        taskName = (TextView) findViewById(R.id.task_name);
        taskCategory = (ImageView) findViewById(R.id.task_category);
        taskPriority = (RatingBar) findViewById(R.id.task_priority);
        taskCount = (TextView) findViewById(R.id.task_count);
        taskDeadline = (TextView) findViewById(R.id.task_deadline);
        taskAbort = (Button) findViewById(R.id.button_abort);
        taskDid = (Button) findViewById(R.id.button_did);

        /* https://guides.codepath.com/android/Using-the-App-Toolbar */
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            Log.w(TAG, "SupportActionBar is null");
        }
        //mTitle.setText(R.string.toolbar_title);
        filterValue = getResources().getStringArray(R.array.toolbar_filter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.filter_item, filterValue);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);
        filter.setOnItemSelectedListener(this);

        View emptyView = findViewById(R.id.list_empty);
        //Empty view is set here
        taskList.setEmptyView(emptyView);
        mtaskListAdapter = new TaskListAdapter(this);
        taskList.setAdapter(mtaskListAdapter);
        taskList.setMultiChoiceModeListener(new ModeCallback());
        taskList.setOnItemClickListener(this);

        fragmentManager = getSupportFragmentManager();
        //fabMenu.setOnClickListener(this);
        ViewCompat.setElevation(fabMenu, 5);
        fabAddTask.setOnClickListener(this);
        fabAddHabbit.setOnClickListener(this);
        fabAddGoal.setOnClickListener(this);

        randomSelect.setOnClickListener(this);
        randomSelect.setVisibility(View.GONE);

        //ongoingTask.setVisibility(View.GONE);
        bottomSheetShadow.setVisibility(View.INVISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(ongoingTask);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0) {
                    fabAddTask.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                    fabMenu.getMenuIconView().animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                    fabMenu.animate().translationY(height / 2 * slideOffset);
                }
            }
        });
        taskAbort.setOnClickListener(this);
        taskDid.setOnClickListener(this);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();

        // Restore preferences
        storage = getPreferences(MODE_PRIVATE);
        String currentTaskStr = storage.getString(CURRENT_TASK, null);
        if (currentTaskStr != null) {
            Gson g = new Gson();
            currentTask = g.fromJson(currentTaskStr, Task.class);
            updateBottomSheet();
        }

    }

    void initConfig() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        Task.useCategory = sharedPref.getBoolean(SettingsActivity.KEY_PREF_CATEGORY, false);

        mTaskDbHelper = TaskDbHelper.getInstance(MainActivity.this);
        mCategoryDBHelper = CategoryDBHelper.getInstance(MainActivity.this);

        Cursor cursor = mCategoryDBHelper.getCategory();
        List<Category> data = mCategoryDBHelper.getCategoryList(this, cursor);
        Category.updateLookupTable(data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Task.useCategory = sharedPref.getBoolean(SettingsActivity.KEY_PREF_CATEGORY, false);
        if (needRefresh) {
            mtaskListAdapter.addFromList(mDataCopy, lastFilter);
            needRefresh = false;
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        needRefresh = true;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mTaskDbHelper.close();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_save) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskSaveClick(Task newTask) {
        if (currentTask != null && newTask.getId() == currentTask.getId()) {
            currentTask = newTask;
            updateBottomSheet();
        }
        mTaskDbHelper.saveNewTask(newTask);
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void onTaskDeleteClick(int id) {
        mTaskDbHelper.deleteTask(id);
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void onTaskAcceptClick(Task acceptedTask) {
        randomSelect.setVisibility(View.GONE);
        currentTask = acceptedTask;
        updateBottomSheet();

        SharedPreferences.Editor prefsEditor = storage.edit();
        Gson g = new Gson();
        String currentTaskStr = g.toJson(currentTask);
        prefsEditor.putString(CURRENT_TASK, currentTaskStr);
        prefsEditor.apply();
        createTaskNotification();
    }

    @Override
    public void onTaskDidClick(int id) {
        onTaskDid();
        clearCurrentTask();
    }

    @Override
    public void onTaskAbortClick(int id) {
        clearCurrentTask();
    }

    private void updateBottomSheet() {
        if (currentTask == null) {
            Log.e(TAG, "Empty currentTask, but updateBottomSheet called");
        }
        taskName.setText(currentTask.name);
        //Category c = Category.lookupTable.get(currentTask.category);
        //taskCategory.setImageResource(c.getIconId());
        //taskCategory.setColorFilter(Color.parseColor(c.color));
        taskPriority.setRating(currentTask.priority);
        String countLabel;
        if (currentTask.trackable) {
            countLabel = getResources().getQuantityString(R.plurals.label_count_down, currentTask.countDown, currentTask.countDown);
        } else {
            countLabel = getResources().getQuantityString(R.plurals.label_count_up, currentTask.countUp, currentTask.countUp);
        }
        taskCount.setText(countLabel);
        String deadlineLabel = getResources().getString(R.string.label_due, currentTask.getDeadline());
        taskDeadline.setText(deadlineLabel);


        bottomSheetShadow.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        Log.i(TAG, "onCreateLoader");
        return new AsyncTaskLoader<Cursor>(MainActivity.this) {
            @Override
            public Cursor loadInBackground() {
                Log.i(TAG, "Getting Cursor");
                return mTaskDbHelper.getTasksCursor();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        /*
        If we use instant rerun from Android Studio,
        onLoaderReset is not called when destroying activity,
        so the cursor would be an closed old one
        */
        Log.i(TAG, "onLoadFinished");

        copyData(cursor);
        int added = mtaskListAdapter.addFromList(mDataCopy, lastFilter);
        if (added > 0) {
            randomSelect.setVisibility(View.VISIBLE);
        } else {
            randomSelect.setVisibility(View.GONE);
        }
        oldCursor = cursor;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "Loader reset");
        oldCursor.close();
    }

    @Override
    public void onClick(View v) {
        if (v == randomSelect) {
            try {
                Task task = TaskSelector.selectTask(mDataCopy);
                Bundle args = new Bundle();
                args.putInt(TaskDialog.ARGS_KEY_MODE, TaskDialog.TASK_DIALOG_MODE_VIEW);
                args.putSerializable(TaskDialog.ARGS_KEY_TASK, task);
                spawnTaskDialog(args);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(MainActivity.this, "No valid task to be selected", Toast.LENGTH_LONG).show();
            }

        } else if (v == taskAbort || v == taskDid) {
            if (v == taskDid) {
                onTaskDid();
            }
            clearCurrentTask();
        } else if (v == fabAddTask) {
            if (fabMenu.isOpened()) {
                // We will change the icon when the menu opens, here we want to change to the previous icon
                Toast.makeText(MainActivity.this, "TASK clicked", Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putInt(TaskDialog.ARGS_KEY_MODE, TaskDialog.TASK_DIALOG_MODE_NEW);
                spawnTaskDialog(args);
                fabMenu.close(false);
            } else {
                // Since it is closed, let's set our new icon and then open the menu
                //fabMenu.getMenuIconView().setImageDrawable(drawableManager.getDrawable(getActivity(), R.drawable.ic_edit_24dp));
                fabMenu.open(true);
            }
        } else if (v == fabAddGoal) {
            Toast.makeText(MainActivity.this, "GOAL clicked", Toast.LENGTH_SHORT).show();
            fabMenu.close(false);
        } else if (v == fabAddHabbit) {
            Toast.makeText(MainActivity.this, "HABBIT clicked", Toast.LENGTH_SHORT).show();
            fabMenu.close(false);
        }

    }

    private void clearCurrentTask() {
        currentTask = null;
        bottomSheetShadow.setVisibility(View.INVISIBLE);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        randomSelect.setVisibility(View.VISIBLE);

        SharedPreferences.Editor prefsEditor = storage.edit();
        prefsEditor.remove(CURRENT_TASK);
        prefsEditor.apply();
        mNotificationManager.cancel(ONGOING_TASK_NOTIFICATION);
    }

    private void onTaskDid() {
        if (currentTask.trackable) {
            if (currentTask.countDown > 1) {
                currentTask.countDown -= 1;
                onTaskSaveClick(currentTask);
            } else {
                onTaskDeleteClick(currentTask.getId());
            }
        } else {
            currentTask.incrementCount();
            onTaskSaveClick(currentTask);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        Task selected = (Task) parent.getAdapter().getItem(position);
        if (currentTask != null) {
            if (currentTask.getId() == selected.getId()) {
                args.putBoolean(TaskDialog.ARGS_KEY_THIS_WORKING, true);
            } else {
                args.putBoolean(TaskDialog.ARGS_KEY_OTHER_WORKING, true);
            }
        }
        args.putInt(TaskDialog.ARGS_KEY_MODE, TaskDialog.TASK_DIALOG_MODE_VIEW);
        args.putSerializable(TaskDialog.ARGS_KEY_TASK, selected);
        spawnTaskDialog(args);
    }

    private void spawnTaskDialog(Bundle args) {

        DialogFragment dialog = new TaskDialog();
        dialog.setArguments(args);
        dialog.setRetainInstance(true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, dialog)
                .addToBackStack(null).commit();
    }

    private void copyData(Cursor cursor) {
        cursor.moveToPosition(-1);
        List<Task> copy = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String category = cursor.getString(2);
            int priority = cursor.getInt(3);
            boolean trackable = cursor.getInt(4) == 1;
            int countDown = cursor.getInt(5);
            int countUp = cursor.getInt(6);
            String deadline = cursor.getString(7);
            /*Task task = new Task(name,
                    category,
                    priority,
                    trackable,
                    countDown,
                    countUp,
                    deadline);*/
            //task.setId(cursor.getInt(0));
            //copy.add(task);
        }

        mDataCopy = copy;
    }

    private void onMultiTaskDelete(ArrayList<String> toDelete) {
        mTaskDbHelper.deleteMultiTask(toDelete);
        getSupportLoaderManager().restartLoader(0, null, MainActivity.this).forceLoad();
    }


    /* filter spinner */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != lastFilter) {
            lastFilter = position;
            int added = mtaskListAdapter.addFromList(mDataCopy, lastFilter);
            if (added > 0) {
                randomSelect.setVisibility(View.VISIBLE);
            } else {
                randomSelect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* livtview longclick*/
    private class ModeCallback implements ListView.MultiChoiceModeListener {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.task_select_menu, menu);
            mode.setTitle(R.string.select_task);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            SparseBooleanArray checkedItems = taskList.getCheckedItemPositions();
            if (checkedItems != null) {
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) {
                        taskList.setItemChecked(checkedItems.keyAt(i), false);
                    }
                }
            }
            taskList.clearChoices();
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    SparseBooleanArray checkedItems = taskList.getCheckedItemPositions();
                    ArrayList<String> toDelete = new ArrayList<>();
                    if (checkedItems != null) {
                        for (int i = 0; i < checkedItems.size(); i++) {
                            if (checkedItems.valueAt(i)) {
                                Task selecteditem = mtaskListAdapter.getItem(checkedItems.keyAt(i));
                                if (selecteditem != null) {
                                    // Remove selected items following the
                                    toDelete.add(String.valueOf(selecteditem.getId()));
                                } else {
                                    Log.w(TAG, String.format("Deleting selected item at position %d but adapter returns null", checkedItems.keyAt(i)));
                                }

                            }
                        }
                    }
                    onMultiTaskDelete(toDelete);
                    Toast.makeText(MainActivity.this, "Deleted " + taskList.getCheckedItemCount() +
                            " tasks", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            View view = getViewByPosition(position, taskList);
            if (checked) {
                view.setBackgroundColor(Color.LTGRAY);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            final int checkedCount = taskList.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + checkedCount + " items selected");
                    break;
            }
        }

        View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }

    }

    private void createTaskNotification(){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_live_help_black_24dp)
                        .setContentTitle("Task in progress")
                        .setContentText(currentTask.name)
                .setAutoCancel(false).setOngoing(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
// mId allows you to update the notification later on.
        mNotificationManager.notify(ONGOING_TASK_NOTIFICATION, mBuilder.build());
    }
}
