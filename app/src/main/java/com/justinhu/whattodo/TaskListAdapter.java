package com.justinhu.whattodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by justinhu on 2017-02-24.
 *
 * http://codetheory.in/android-dividing-listview-sections-group-headers/
 */

public class TaskListAdapter extends ArrayAdapter<TaskContract> {
    private Context mContext;

    // View Type for Separators
    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    // View Type for Regular rows
    private static final int ITEM_VIEW_TYPE_REGULAR = 1;
    // Types of Views that need to be handled
    // -- Separators and Regular rows --
    private static final int ITEM_VIEW_TYPE_COUNT = 2;


    public TaskListAdapter(Context mContext) {
        super(mContext,0);
        this.mContext = mContext;
    }


    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isSection = getItem(position).isSeperator();

        if (isSection) {
            return ITEM_VIEW_TYPE_SEPARATOR;
        }
        else {
            return ITEM_VIEW_TYPE_REGULAR;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaskViewHolder holder;

        TaskContract task = getItem(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                // If its a section ?
                convertView = inflater.inflate(R.layout.task_section_header, parent,false);
                holder = new TaskSeparatorViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.category_name);
            }
            else {
                // Regular row
                convertView = inflater.inflate(R.layout.task_item, parent,false);
                holder = new TaskItemViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.task_name);
                ((TaskItemViewHolder)holder).priority = (RatingBar) convertView.findViewById(R.id.task_priority);
            }
            convertView.setTag(holder);
        }
        else {
            holder = (TaskViewHolder) convertView.getTag();
        }


        holder.populateView(task);


        return convertView;
    }


    static class TaskViewHolder{
        TextView name;

        public void populateView(TaskContract task){
            name.setText(task.name);
        };
    }

    private static class TaskSeparatorViewHolder extends TaskViewHolder{

        @Override
        public void populateView(TaskContract task) {
            super.populateView(task);
            switch (task.category) {
                    case DEFAULT:

                        name.setBackgroundResource(R.color.defaultGrey);
                        break;
                    case WORK:
                        name.setBackgroundResource(R.color.workRed);
                        break;
                    case SCHOOL:
                        name.setBackgroundResource(R.color.schoolOrange);
                        break;
                    case EXERCISE:
                        name.setBackgroundResource(R.color.exercisesTeal);
                        break;
                    case PERSONAL:
                        name.setBackgroundResource(R.color.personalGreen);
                        break;
                    case RELAX:
                        name.setBackgroundResource(R.color.relaxLightGreen);
                        break;
                    default:
                        name.setBackgroundResource(R.color.defaultGrey);
                        break;
                }
        }
    }

    private static class TaskItemViewHolder extends TaskViewHolder{
        RatingBar priority;

        @Override
        public void populateView(TaskContract task) {
            super.populateView(task);
            priority.setRating(task.priority);
        }
    }

}
