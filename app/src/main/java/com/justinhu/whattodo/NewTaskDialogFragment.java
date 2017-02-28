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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by justinhu on 2017-02-21.
 *
 * Thanks to JerabekJakub https://stackoverflow.com/questions/31606871/how-to-achieve-a-full-screen-dialog-as-described-in-material-guidelines/38070414#38070414
 *  http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
 */

public class NewTaskDialogFragment extends DialogFragment implements View.OnClickListener, TaskCategoryDialogFragment.TaskCategoryDialogListener{

    private int mode;

    private TaskContract task;

    private EditText taskname;
    private ImageButton categoryButton;
    private RatingBar priority;
    private Switch trackableSwitch;
    private View trackableOptions;
    private EditText repetition;
    private TextView deadline;
    private DatePickerDialog deadlinePicker;
    private SimpleDateFormat dateFormatter = TaskContract.dateFormatter;
    private TaskCategoryEnum taskCategory = TaskCategoryEnum.DEFAULT;
    private NewTaskDialogListener listener;

    private MenuItem saveAction;
    private MenuItem editAction;

    public static final String ARGS_KEY_MODE = "MODE";
    public static final String ARGS_KEY_TASK = "TASK";
    public static final int TASK_DIALOG_MODE_NEW = 0;
    public static final int TASK_DIALOG_MODE_VIEW = 1;
    public static final int TASK_DIALOG_MODE_EDIT = 2;

    public interface NewTaskDialogListener {
        public void onTaskSaveClick(TaskContract newTask);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.mode = args.getInt(ARGS_KEY_MODE,TASK_DIALOG_MODE_NEW);
        if(mode == TASK_DIALOG_MODE_VIEW||mode == TASK_DIALOG_MODE_EDIT){
            task = (TaskContract) args.getSerializable(ARGS_KEY_TASK);
        }

    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_newtask, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("New Task");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);

        taskname = (EditText) rootView.findViewById(R.id.taskname);
        priority = (RatingBar) rootView.findViewById(R.id.priority);

        trackableSwitch = (Switch) rootView.findViewById(R.id.trackable);
        trackableOptions = rootView.findViewById(R.id.option);
        trackableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    trackableOptions.setVisibility(View.VISIBLE);
                }else{
                    trackableOptions.setVisibility(View.GONE);
                }
            }
        });

        repetition = (EditText)  rootView.findViewById(R.id.repetition);
        deadline = (TextView) rootView.findViewById(R.id.deadline);
        categoryButton = (ImageButton) rootView.findViewById(R.id.taskCategory);

        deadline.setOnClickListener(this);
        categoryButton.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        deadline.setText(TaskContract.DEFAULT_DATE);

        deadlinePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                deadline.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if(task!= null){
            taskname.setText(task.name);
            onTaskCategoryClick(task.category);
            priority.setRating(task.priority);
            trackableOptions.setVisibility( task.trackable?View.VISIBLE:View.GONE);
            trackableSwitch.setChecked(task.trackable);
            repetition.setText(String.format (Locale.US,"%d", task.repetition));
            deadline.setText(task.deadline);
        }else{
            trackableOptions.setVisibility( View.GONE);
            trackableSwitch.setChecked(false);
        }
        if(mode == TASK_DIALOG_MODE_NEW || mode == TASK_DIALOG_MODE_EDIT){
            taskname.setEnabled(true);
            categoryButton.setEnabled(true);
            priority.setEnabled(true);
            trackableSwitch.setEnabled(true);
            repetition.setEnabled(true);
            deadline.setEnabled(true);
        }else if(mode == TASK_DIALOG_MODE_VIEW){
            taskname.setEnabled(false);
            categoryButton.setEnabled(false);
            priority.setEnabled(false);
            trackableSwitch.setEnabled(false);
            repetition.setEnabled(false);
            deadline.setEnabled(false);
        }

        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNeutralButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getContext(),"Cancel is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        return builder.create();*/
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_task, menu);
        saveAction = menu.findItem(R.id.action_save);
        editAction = menu.findItem(R.id.action_edit);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mode == TASK_DIALOG_MODE_NEW || mode == TASK_DIALOG_MODE_EDIT){
            saveAction.setVisible(true);
            editAction.setVisible(false);
        }else if(mode == TASK_DIALOG_MODE_VIEW){
            saveAction.setVisible(false);
            editAction.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            TaskContract newTask = new TaskContract(
                    taskname.getText().toString(),
                    taskCategory,
                    (int)priority.getRating(),
                    trackableSwitch.isChecked(),
                    Integer.parseInt(repetition.getText().toString()),
                    deadline.getText().toString()
            );
            listener.onTaskSaveClick(newTask);
            dismiss();
            return true;
        } else if (id == android.R.id.home) {
            // handle close button click here
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
            dialog.setTargetFragment(NewTaskDialogFragment.this,0);
            dialog.show(getFragmentManager(), "TaskCategoryDialogFragment");
        }
    }

    @Override
    public void onTaskCategoryClick(TaskCategoryEnum category) {
        taskCategory = category;
        switch (taskCategory){
            case DEFAULT:
                categoryButton.setBackgroundResource(R.drawable.ic_default_grey_500_24dp);
                break;
            case WORK:
                categoryButton.setBackgroundResource(R.drawable.ic_work_red_500_24dp);
                break;
            case SCHOOL:
                categoryButton.setBackgroundResource(R.drawable.ic_school_orange_500_24dp);
                break;
            case EXERCISE:
                categoryButton.setBackgroundResource(R.drawable.ic_fitness_center_teal_500_24dp);
                break;
            case PERSONAL:
                categoryButton.setBackgroundResource(R.drawable.ic_person_green_500_24dp);
                break;
            case RELAX:
                categoryButton.setBackgroundResource(R.drawable.ic_relax_light_green_500_24dp);
                break;
            default:
                categoryButton.setBackgroundResource(R.drawable.ic_default_grey_500_24dp);
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
