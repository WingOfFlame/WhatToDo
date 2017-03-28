package com.justinhu.whattodo.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.justinhu.whattodo.R;
import com.justinhu.whattodo.TaskListAdapter;
import com.justinhu.whattodo.db.TaskDbHelper;
import com.justinhu.whattodo.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinhu on 2017-03-26.
 */

public class AllTaskFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    public static final String CURRENT_TASK = "currentTask";
    private static final String TAG = "AllTaskFragment";
    private static final int ONGOING_TASK_NOTIFICATION = 0;

    TaskListOwner owner;
    List<Task> mDataCopy;
    TaskDbHelper mTaskDbHelper;
    String[] filterValue;
    private int lastFilter = 0;

    ListView taskList;
    TaskListAdapter mtaskListAdapter;


    public interface TaskListOwner {
        public void spawnTaskDialog(Bundle args);
    }

    public AllTaskFragment() {

    }


    public static AllTaskFragment newInstance() {
        AllTaskFragment tab = new AllTaskFragment();
        return tab;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            owner = (TaskListOwner) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement TaskListOwner");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        owner = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskDbHelper = TaskDbHelper.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_all, container, false);
        taskList = (ListView) rootView.findViewById(R.id.taskList);
        View emptyView = rootView.findViewById(R.id.list_empty);
        //Empty view is set here
        taskList.setEmptyView(emptyView);
        mtaskListAdapter = new TaskListAdapter(getContext());
        taskList.setAdapter(mtaskListAdapter);
        taskList.setMultiChoiceModeListener(new ModeCallback());
        taskList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this).forceLoad();
        return rootView;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        return new AsyncTaskLoader<Cursor>(getContext()) {
            @Override
            public Cursor loadInBackground() {
                Log.i(TAG, "Getting Cursor");
                return mTaskDbHelper.getTasksCursor();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished");

        copyData(data);
        int added = mtaskListAdapter.addFromList(mDataCopy, lastFilter);
        data.close();
        //oldCursor = cursor;
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
                    deadline);
            task.setId(cursor.getInt(0));
            copy.add(task);*/
        }

        mDataCopy = copy;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "Loader reset");
        //oldCursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        Task selected = (Task) parent.getAdapter().getItem(position);
        args.putBoolean(TaskDialog.ARGS_KEY_OTHER_WORKING, true);
        args.putInt(TaskDialog.ARGS_KEY_MODE, TaskDialog.TASK_DIALOG_MODE_VIEW);
        args.putSerializable(TaskDialog.ARGS_KEY_TASK, selected);
        owner.spawnTaskDialog(args);
    }

    public void update() {
        getLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.task_select_menu, menu);
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

                    mode.finish();
                    break;
                default:
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

    private void onMultiTaskDelete(ArrayList<String> toDelete) {
        mTaskDbHelper.deleteMultiTask(toDelete);
        getLoaderManager().restartLoader(0, null, this).forceLoad();
    }
}
