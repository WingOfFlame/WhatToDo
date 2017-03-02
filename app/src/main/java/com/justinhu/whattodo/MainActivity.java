package com.justinhu.whattodo;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements TaskDialogFragment.NewTaskDialogListener , LoaderManager.LoaderCallbacks<List<TaskContract>>, View.OnClickListener, AdapterView.OnItemClickListener {
    List<TaskContract> mDataCopy;
    TaskDbHelper mDbHelper;
    ListView taskList;
    // This is the Adapter being used to display the list's data
    TaskListAdapter mAdapter;
    Button randomSelect;
    private FragmentManager fragmentManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fragmentManager = getSupportFragmentManager();
        fab.setOnClickListener(this);

        mDbHelper = TaskDbHelper.getInstance(MainActivity.this);
        mAdapter = new TaskListAdapter(this);

        taskList = (ListView) findViewById(R.id.taskList);
        taskList.setAdapter(mAdapter);
        taskList.setMultiChoiceModeListener(new ModeCallback());

        taskList.setOnItemClickListener(this);


        randomSelect = (Button) findViewById(R.id.select);
        randomSelect.setOnClickListener(this);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
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
        }else if(id == R.id.action_save){
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskSaveClick(TaskContract newTask) {
        mDbHelper.saveNewTask(newTask);
        getSupportLoaderManager().restartLoader(0,null,this).forceLoad();
    }

    @Override
    public void onTaskDeleteClick(int id) {
        mDbHelper.deleteTask(id);
        getSupportLoaderManager().restartLoader(0,null,this).forceLoad();
    }

    private void onMultiTaskDelete(ArrayList<String> toDelete) {
        mDbHelper.deleteMultiTask(toDelete);
        getSupportLoaderManager().restartLoader(0, null, MainActivity.this).forceLoad();
    }

    @Override
    public Loader<List<TaskContract>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<TaskContract>>( MainActivity.this )
        {
            @Override
            public List<TaskContract> loadInBackground()
            {
                return mDbHelper.getTasks();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<TaskContract>> loader, List<TaskContract> data) {
        mDataCopy = data;
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TaskContract>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onClick(View v) {
        if (v == randomSelect){
            TaskContract task = TaskSelector.selectTask(mDataCopy);
            Bundle args = new Bundle();
            args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_TAKE);
            args.putSerializable(TaskDialogFragment.ARGS_KEY_TASK, task);
            spawnTaskDialog(args);
        }else if (v == fab){
            Bundle args = new Bundle();
            args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_NEW);
            spawnTaskDialog(args);
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        args.putInt(TaskDialogFragment.ARGS_KEY_MODE, TaskDialogFragment.TASK_DIALOG_MODE_VIEW);
        args.putSerializable(TaskDialogFragment.ARGS_KEY_TASK, (TaskContract) parent.getAdapter().
            getItem(position)
        );
        spawnTaskDialog(args);
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
        public void onDestroyActionMode(ActionMode mode){
            SparseBooleanArray checkedItems = taskList.getCheckedItemPositions();
            if (checkedItems != null) {
                for (int i=0; i<checkedItems.size(); i++) {
                    if(checkedItems.valueAt(i)){
                        taskList.setItemChecked(checkedItems.keyAt(i),false);
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
                                // Remove selected items following the
                                toDelete.add(String.valueOf(selecteditem.getId()));
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
            View view = getViewByPosition(position,taskList);
            if (checked){
                view.setBackgroundColor(Color.LTGRAY);
            }else{
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
        public View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }

    }
}
