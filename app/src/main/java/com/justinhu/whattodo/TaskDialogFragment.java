package com.justinhu.whattodo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by justinhu on 2017-02-21.
 *
 * Thanks to JerabekJakub https://stackoverflow.com/questions/31606871/how-to-achieve-a-full-screen-dialog-as-described-in-material-guidelines/38070414#38070414
 *  http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
 */

public class TaskDialogFragment extends DialogFragment implements View.OnClickListener, TaskCategoryDialogFragment.TaskCategoryDialogListener{

    private static final String TAG = "TaskDialogFragment";

    private int mode;
    private boolean isWorking;
    private boolean otherWorking;
    private Task task;

    private TaskCategoryEnum taskCategory = TaskCategoryEnum.DEFAULT;
    private NewTaskDialogListener listener;

    private Toolbar toolbar;
    private EditText taskname;
    private ImageButton categoryButton;
    private RatingBar priority;
    private Switch trackableSwitch;
    private View trackableValues;
    private EditText countDown;
    private Switch deadlineSwitch;
    private View deadlineValue;
    private TextView deadline;
    private DatePickerDialog deadlinePicker;


    private View takeTaskView;
    private Button acceptButton;
    private Button declineButton;
    private View taskOngoingView;
    private Button taskAbortButton;
    private Button taskDidButton;

    private Menu mMenu;
    private MenuItem saveAction;
    private MenuItem editAction;
    private MenuItem deleteAction;

    public static final String ARGS_KEY_THIS_WORKING = "THIS_WORKING";
    public static final String ARGS_KEY_OTHER_WORKING = "OTHER_WORKING";
    public static final String ARGS_KEY_MODE = "MODE";
    public static final String ARGS_KEY_TASK = "TASK";
    public static final int TASK_DIALOG_MODE_NEW = 0;
    public static final int TASK_DIALOG_MODE_VIEW = 1;
    public static final int TASK_DIALOG_MODE_EDIT = 2;



    //public static final int TASK_DIALOG_MODE_TAKE = 3;

    interface NewTaskDialogListener {
        void onTaskSaveClick(Task newTask);

        void onTaskDeleteClick(int id);

        void onTaskAcceptClick(Task acceptedTask);

        void onTaskDidClick(int id);

        void onTaskAbortClick(int id);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.mode = args.getInt(ARGS_KEY_MODE,TASK_DIALOG_MODE_NEW);
        if(mode != TASK_DIALOG_MODE_NEW){
            task = (Task) args.getSerializable(ARGS_KEY_TASK);
        }
        this.isWorking = args.getBoolean(ARGS_KEY_THIS_WORKING, false);
        this.otherWorking = args.getBoolean(ARGS_KEY_OTHER_WORKING, false);
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.dialog_task, container, false);
        Log.i(TAG, "fit system windows "+  rootView.getFitsSystemWindows());

        findViews(rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        setHasOptionsMenu(true);

        trackableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    trackableValues.setVisibility(View.VISIBLE);
                }else{
                    trackableValues.setVisibility(View.GONE);
                }
            }
        });
        deadlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deadlineValue.setVisibility(View.VISIBLE);
                } else {
                    deadlineValue.setVisibility(View.GONE);
                }
            }
        });
        deadline.setOnClickListener(this);
        categoryButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
        taskDidButton.setOnClickListener(this);
        taskAbortButton.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        deadlinePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                deadline.setText(Task.dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        deadlinePicker.getDatePicker().setMinDate(System.currentTimeMillis());



        if(task!= null){
            populateViews();
        }else{
            deadline.setText(Task.dateFormatter.format(newCalendar.getTime()));
            trackableSwitch.setChecked(true);
        }

        if (mode == TASK_DIALOG_MODE_NEW) {
            toolbar.setTitle("New Task");
            setViewsEnabled(true);
            takeTaskView.setVisibility(View.GONE);
            taskOngoingView.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("Task Details");
            setViewsEnabled(false);
            if (otherWorking) {
                takeTaskView.setVisibility(View.GONE);
                taskOngoingView.setVisibility(View.GONE);
            } else if (isWorking) {
                taskOngoingView.setVisibility(View.VISIBLE);
                takeTaskView.setVisibility(View.GONE);
            } else {
                taskOngoingView.setVisibility(View.GONE);
                takeTaskView.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }
    private void findViews(View rootView){
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        taskname = (EditText) rootView.findViewById(R.id.taskname);
        categoryButton = (ImageButton) rootView.findViewById(R.id.task_category);
        priority = (RatingBar) rootView.findViewById(R.id.priority);

        trackableSwitch = (Switch) rootView.findViewById(R.id.trackable);
        trackableValues = rootView.findViewById(R.id.view_trackable_value);
        countDown = (EditText)  rootView.findViewById(R.id.task_count);

        deadlineSwitch = (Switch) rootView.findViewById(R.id.has_deadline);
        deadlineValue = rootView.findViewById(R.id.deadline_value);
        deadline = (TextView) rootView.findViewById(R.id.deadline_input);

        takeTaskView = rootView.findViewById(R.id.task_take);
        acceptButton = (Button) rootView.findViewById(R.id.accept);
        declineButton = (Button) rootView.findViewById(R.id.decline);

        taskOngoingView = rootView.findViewById(R.id.task_ongoing);
        taskAbortButton = (Button) rootView.findViewById(R.id.button_abort);
        taskDidButton = (Button) rootView.findViewById(R.id.button_did);
    }

    private void populateViews() {
        taskname.setText(task.name);
        onTaskCategoryClick(task.category);
        priority.setRating(task.priority);
        trackableSwitch.setChecked(task.trackable);
        deadlineSwitch.setChecked(task.getDeadline() != Task.DEFAULT_DATE);
        countDown.setText(String.format (Locale.US,"%d", task.countDown));
        deadline.setText(task.getDeadline());
    }

    private void setViewsEnabled(boolean enabled) {
        taskname.setEnabled(enabled);
        categoryButton.setEnabled(enabled);
        priority.setEnabled(enabled);
        trackableSwitch.setEnabled(enabled);
        countDown.setEnabled(enabled);
        deadline.setEnabled(enabled);
        declineButton.setEnabled(!enabled);
        acceptButton.setEnabled(!enabled);
        taskDidButton.setEnabled(!enabled);
        taskAbortButton.setEnabled(!enabled);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_task, menu);
        saveAction = menu.findItem(R.id.action_save);
        editAction = menu.findItem(R.id.action_edit);
        deleteAction = menu.findItem(R.id.action_delete);
        mMenu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mode == TASK_DIALOG_MODE_NEW ){
            saveAction.setVisible(true);
            editAction.setVisible(false);
            deleteAction.setVisible(false);
        }else if(mode == TASK_DIALOG_MODE_VIEW){
            saveAction.setVisible(false);
            editAction.setVisible(true);
            deleteAction.setVisible(true);
        }else if(mode == TASK_DIALOG_MODE_EDIT){
            toolbar.setTitle("Edit Task");
            saveAction.setVisible(true);
            editAction.setVisible(false);
            deleteAction.setVisible(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Task newTask = new Task(
                    taskname.getText().toString(),
                    taskCategory,
                    (int)priority.getRating(),
                    trackableSwitch.isChecked(),
                    Integer.parseInt(countDown.getText().toString()),
                    0,
                    (deadlineSwitch.isChecked() && trackableSwitch.isChecked()) ? deadline.getText().toString() : Task.DEFAULT_DATE
            );

            if (mode == TASK_DIALOG_MODE_NEW) {
                listener.onTaskSaveClick(newTask);
                dismiss();
                return true;
            }

            if (task == null) {
                Log.e(TAG, "Null task to save at mode:" + mode);
                return false;
            }

            newTask.setId(task.getId());
            newTask.countUp = task.countUp;
            listener.onTaskSaveClick(newTask);
            task = newTask;

            mode = TASK_DIALOG_MODE_VIEW;
            toolbar.setTitle("Task Details");
            setViewsEnabled(false);

            onPrepareOptionsMenu(mMenu);

            return true;
        } else if (id == android.R.id.home) {
            // handle close button click here
            if (mode != TASK_DIALOG_MODE_EDIT) {
                dismiss();
                return true;
            }
            mode = TASK_DIALOG_MODE_VIEW;
            toolbar.setTitle("Task Details");
            setViewsEnabled(false);
            onPrepareOptionsMenu(mMenu);

            return true;
        }else if (id == R.id.action_edit){
            mode = TASK_DIALOG_MODE_EDIT;
            toolbar.setTitle("Edit Task");
            setViewsEnabled(true);
            onPrepareOptionsMenu(mMenu);
            return true;
        }else if(id == R.id.action_delete){
            listener.onTaskDeleteClick(task.getId());
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == deadline) {
            deadlinePicker.show();
        }else if(v == categoryButton){
            DialogFragment dialog = new TaskCategoryDialogFragment();
            dialog.setTargetFragment(TaskDialogFragment.this,0);
            dialog.show(getFragmentManager(), "TaskCategoryDialogFragment");
        }else if(v == declineButton){
            dismiss();
        }else if(v == acceptButton){
            listener.onTaskAcceptClick(task);
            dismiss();
        } else if (v == taskAbortButton) {
            listener.onTaskAbortClick(task.getId());
            dismiss();
        } else if (v == taskDidButton) {
            listener.onTaskDidClick(task.getId());
            dismiss();
        }
    }

    @Override
    public void onTaskCategoryClick(TaskCategoryEnum category) {
        taskCategory = category;
        switch (taskCategory){
            case DEFAULT:
                categoryButton.setImageResource(R.drawable.btn_default_category);
                break;
            case WORK:
                categoryButton.setImageResource(R.drawable.btn_work_category);
                break;
            case SCHOOL:
                categoryButton.setImageResource(R.drawable.btn_school_category);
                break;
            case EXERCISE:
                categoryButton.setImageResource(R.drawable.btn_fitness_category);
                break;
            case PERSONAL:
                categoryButton.setImageResource(R.drawable.btn_person_category);
                break;
            case RELAX:
                categoryButton.setImageResource(R.drawable.btn_relax_category);
                break;
            default:
                categoryButton.setImageResource(R.drawable.btn_default_category);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewTaskDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NewTaskDialogListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
