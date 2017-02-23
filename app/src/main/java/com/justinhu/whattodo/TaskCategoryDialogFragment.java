package com.justinhu.whattodo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by justinhu on 2017-02-22.
 */

public class TaskCategoryDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button defaultButton;
    private Button workButton;
    private Button schoolButton;
    private Button exerciseButton;
    private Button personalButton;
    private Button relaxButton;

    private TaskCategoryDialogListener listener;
    private TaskCategoryEnum category = TaskCategoryEnum.DEFAULT;

    public interface TaskCategoryDialogListener {
        public void onTaskCategoryClick(TaskCategoryEnum category);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_category, null);

        defaultButton = (Button) view.findViewById(R.id.button_default);
        workButton = (Button) view.findViewById(R.id.button_work);
        schoolButton = (Button) view.findViewById(R.id.button_school);
        exerciseButton = (Button) view.findViewById(R.id.button_exercise);
        personalButton = (Button) view.findViewById(R.id.button_personal);
        relaxButton = (Button) view.findViewById(R.id.button_relax);

        defaultButton.setOnClickListener(this);
        workButton.setOnClickListener(this);
        schoolButton.setOnClickListener(this);
        exerciseButton.setOnClickListener(this);
        personalButton.setOnClickListener(this);
        relaxButton.setOnClickListener(this);

        listener= (TaskCategoryDialogListener) getTargetFragment();

        builder.setView(view);
        builder.setTitle("Category");
        builder.setNegativeButton("USE DEFAUT CATEGORY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        category = TaskCategoryEnum.DEFAULT;
                        listener.onTaskCategoryClick(category);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        if (v == defaultButton){
            category = TaskCategoryEnum.DEFAULT;
        }else if (v == workButton){
            category = TaskCategoryEnum.WORK;
        }else if(v == schoolButton){
            category= TaskCategoryEnum.SCHOOL;
        }else if(v == exerciseButton){
            category = TaskCategoryEnum.EXERCISE;
        }else if(v == personalButton){
            category = TaskCategoryEnum.PERSONAL;
        }else if(v == relaxButton){
            category = TaskCategoryEnum.RELAX;
        }

        listener.onTaskCategoryClick(category);
        dismiss();

    }
}
