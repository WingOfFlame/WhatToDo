package com.justinhu.whattodo;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskDialogFragment.NewTaskDialogListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener {
    List<TaskContract> mDataCopy;
    TaskDbHelper mDbHelper;
    private Cursor oldCursor;

    TextView mTitle;
    ListView taskList;
    TaskListAdapter mAdapter;
    View bottomSheetShadow;
    CoordinatorLayout ongoingTask;
    private BottomSheetBehavior bottomSheetBehavior;
    TextView taskName;
    ImageView taskCategory;
    RatingBar taskPriority;
    TextView taskCount;
    TextView taskDeadline;
    Button taskAbort;
    Button taskDid;

    Button randomSelect;

    TaskContract currentTask = null;

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;


    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mDbHelper = TaskDbHelper.getInstance(MainActivity.this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        taskList = (ListView) findViewById(R.id.taskList);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        mTitle.setText(R.string.toolbar_title);

        mAdapter = new TaskListAdapter(this);
        taskList.setAdapter(mAdapter);
        taskList.setMultiChoiceModeListener(new ModeCallback());
        taskList.setOnItemClickListener(this);

        fragmentManager = getSupportFragmentManager();
        fab.setOnClickListener(this);

        randomSelect.setOnClickListener(this);

        //ongoingTask.setVisibility(View.GONE);
        bottomSheetShadow.setVisibility(View.INVISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(ongoingTask);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if(slideOffset >=0){
                    fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                }
            }
        });
        taskAbort.setOnClickListener(this);
        taskDid.setOnClickListener(this);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        Log.i(TAG, "fit system windows " + ongoingTask.getFitsSystemWindows());


    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mDbHelper.close();
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
            return true;
        } else if (id == R.id.action_save) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskSaveClick(TaskContract newTask) {
        mDbHelper.saveNewTask(newTask);
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void onTaskDeleteClick(int id) {
        mDbHelper.deleteTask(id);
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    @Override
    public void onTaskAcceptClick(TaskContract acceptedTask) {
        currentTask = acceptedTask;

        taskName.setText(acceptedTask.name);
        switch (acceptedTask.category){
            case DEFAULT:
                taskCategory.setImageResource(R.drawable.ic_default_grey_500_24dp);
                break;
            case WORK:
                taskCategory.setImageResource(R.drawable.ic_work_red_500_24dp);
                break;
            case SCHOOL:
                taskCategory.setImageResource(R.drawable.ic_school_orange_500_24dp);
                break;
            case EXERCISE:
                taskCategory.setImageResource(R.drawable.ic_fitness_center_teal_500_24dp);
                break;
            case PERSONAL:
                taskCategory.setImageResource(R.drawable.ic_person_green_500_24dp);
                break;
            case RELAX:
                taskCategory.setImageResource(R.drawable.ic_relax_light_green_500_24dp);
                break;
            default:
                taskCategory.setImageResource(R.drawable.ic_default_grey_500_24dp);
                break;
        }
        taskPriority.setRating(acceptedTask.priority);
        String countLabel = getResources().getQuantityString(R.plurals.label_times_left,acceptedTask.repetition,acceptedTask.repetition);
        taskCount.setText(countLabel);
        String deadlineLabel = getResources().getString(R.string.label_due,acceptedTask.deadline);
        taskDeadline.setText(deadlineLabel);


        bottomSheetShadow.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        return new AsyncTaskLoader<Cursor>(MainActivity.this) {
            @Override
            public Cursor loadInBackground() {
                Log.i(TAG, "Getting Cursor");
                return mDbHelper.getTasksCursor();
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
        Log.i(TAG, "onLoadFinished  ");
        mAdapter.addRaw(cursor);
        copyData(cursor);
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
            TaskContract task = TaskSelector.selectTask(mDataCopy);
            Bundle args = new Bundle();
            args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_TAKE);
            args.putSerializable(TaskDialogFragment.ARGS_KEY_TASK, task);
            spawnTaskDialog(args);
        } else if (v == fab) {
            Bundle args = new Bundle();
            args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_NEW);
            spawnTaskDialog(args);
        }else if (v == taskAbort || v == taskDid){
            if(v == taskDid){
                if(currentTask.repetition>1){
                    currentTask.repetition -=1;
                    onTaskSaveClick(currentTask);
                }else{
                    onTaskDeleteClick(currentTask.getId());
                }
            }
            currentTask = null;
            bottomSheetShadow.setVisibility(View.INVISIBLE);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_VIEW);
        args.putSerializable(TaskDialogFragment.ARGS_KEY_TASK, (TaskContract) parent.getAdapter().
                getItem(position)
        );
        spawnTaskDialog(args);
    }

    private void spawnTaskDialog(Bundle args) {

        DialogFragment dialog = new TaskDialogFragment();
        dialog.setArguments(args);
        dialog.setRetainInstance(true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, dialog)
                .addToBackStack(null).commit();
    }

    private void copyData(Cursor cursor) {
        cursor.moveToPosition(-1);
        List<TaskContract> copy = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            TaskCategoryEnum category = TaskCategoryEnum.valueOf(cursor.getString(2));
            int priority = cursor.getInt(3);
            boolean trackable = cursor.getInt(4) == 1;
            int repeat = cursor.getInt(5);
            String deadline = cursor.getString(6);
            TaskContract task = new TaskContract(name,
                    category,
                    priority,
                    trackable,
                    repeat,
                    deadline);
            task.setId(cursor.getInt(0));
            copy.add(task);
        }

        mDataCopy = copy;
    }

    private void onMultiTaskDelete(ArrayList<String> toDelete) {
        mDbHelper.deleteMultiTask(toDelete);
        getSupportLoaderManager().restartLoader(0, null, MainActivity.this).forceLoad();
    }


    private class ModeCallback implements ListView.MultiChoiceModeListener {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.task_select_menu, menu);
            mode.setTitle("Select Items");
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
                                TaskContract selecteditem = mAdapter.getItem(checkedItems.keyAt(i));
                                if(selecteditem != null){
                                    // Remove selected items following the
                                    toDelete.add(String.valueOf(selecteditem.getId()));
                                }else{
                                    Log.w(TAG,String.format("Deleting selected item at position %d but adapter returns null",checkedItems.keyAt(i)));
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
}
