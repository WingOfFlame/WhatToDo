package com.justinhu.whattodo.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.justinhu.whattodo.R;
import com.justinhu.whattodo.model.Category;
import com.justinhu.whattodo.model.TaskCategoryEnum;

/**
 * Created by justinhu on 2017-02-22.
 */

public class TaskCategoryDialog extends DialogFragment implements View.OnClickListener {
    private Button defaultButton;
    private Button workButton;
    private Button schoolButton;
    private Button exerciseButton;
    private Button personalButton;
    private Button relaxButton;

    private TaskCategoryDialogListener listener;

    public interface TaskCategoryDialogListener {
        public void onTaskCategoryClick(Category category);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialog_select_category, null);

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
                        listener.onTaskCategoryClick(Category.getDefaultCategory());
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        Category c;
         if (v == workButton){
            c = Category.getCategory(Category.NAME_WORK);
        }else if(v == schoolButton){
            c = Category.getCategory(Category.NAME_STUDY);
        }else if(v == exerciseButton){
            c = Category.getCategory(Category.NAME_WORKOUT);
        }else if(v == personalButton){
            c = Category.getCategory(Category.NAME_PERSONAL);
        }else if(v == relaxButton){
            c = Category.getCategory(Category.NAME_RELAX);
        }else{
            c = Category.getCategory(Category.NAME_DEFAULT);
         }
        listener.onTaskCategoryClick(c);
        dismiss();

    }
}
