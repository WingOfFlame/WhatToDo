package com.justinhu.whattodo.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.justinhu.whattodo.R;
import com.justinhu.whattodo.model.Category;

/**
 * Created by justinhu on 2017-03-12.
 */

public class CategoryDialog extends DialogFragment implements ColorPickerDialogListener, View.OnClickListener {
    private static final String ARGS_KEY_DATA ="DATA" ;
    private EditText name;
    private View colorView;

    private Category data = null;

    public static CategoryDialog newInstance(Category var) {
        CategoryDialog dialog = new CategoryDialog();
        //Bundle args = new Bundle();
        //args.putSerializable(ARGS_KEY_DATA, var);
        //dialog.setArguments(args);
        dialog.setData(var);
        return dialog;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        data = (Category) args.getSerializable(ARGS_KEY_DATA);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_category, null);

        name = (EditText) view.findViewById(R.id.category_name);
        colorView =  view.findViewById(R.id.category_color);

        colorView.setOnClickListener(this);
        builder.setView(view);
        //builder.setTitle("Category");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        int color = Color.TRANSPARENT;
        Drawable background = colorView.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        ColorPickerDialog colorPicker = ColorPickerDialog.newBuilder().setColor(color).setShowAlphaSlider(false).create();
        colorPicker.setColorPickerDialogListener(this);
        colorPicker.show(getActivity().getFragmentManager(), "color-picker-dialog");
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        colorView.setBackgroundColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void setData(Category data) {
        this.data = data;
    }


}
