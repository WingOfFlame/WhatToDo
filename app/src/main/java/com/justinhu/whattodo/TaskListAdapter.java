package com.justinhu.whattodo;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by justinhu on 2017-02-24.
 *
 * http://codetheory.in/android-dividing-listview-sections-group-headers/
 */

public class TaskListAdapter extends ArrayAdapter<Task> {
    private Context mContext;
    private static Date currentTime;

    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    private static final int ITEM_VIEW_TYPE_REGULAR = 1;
    private static final int ITEM_VIEW_TYPE_COUNT = 2;

    private static final int LIST_FILTER_ALL = 0;
    private static final int LIST_FILTER_ONTIME = 1;
    private static final int LIST_FILTER_OVERDUE = 2;

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

        Task task = getItem(position);
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
                ((TaskItemViewHolder) holder).deadline = (TextView) convertView.findViewById(R.id.task_deadline);
                ((TaskItemViewHolder) holder).repetition = (TextView) convertView.findViewById(R.id.task_count);
            }
            holder.self = convertView;
            convertView.setTag(holder);
        }
        else {
            holder = (TaskViewHolder) convertView.getTag();
        }


        holder.populateView(this.mContext, task);


        return convertView;
    }

    public void addFromCursor(Cursor cursor) {
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.HOUR, -30);
        currentTime = newDate.getTime();

        List<List<Task>> group = new ArrayList<List<Task>>(6);
        for (int i =0; i <6; i++){
            group.add(new ArrayList<Task>());
        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            TaskCategoryEnum category = TaskCategoryEnum.valueOf(cursor.getString(2));
            int priority = cursor.getInt(3);
            boolean trackable = cursor.getInt(4) == 1;
            int countDown = cursor.getInt(5);
            int countUp = cursor.getInt(6);
            String deadline = cursor.getString(7);
            Task task = new Task(name,
                    category,
                    priority,
                    trackable,
                    countDown,
                    countUp,
                    deadline);
            task.setId(cursor.getInt(0));
            group.get(category.getLevel() - 1).add(task);
        }

        List<Task> flatten = new ArrayList<>();
        for (TaskCategoryEnum category : TaskCategoryEnum.values()) {
            List<Task> list = group.get(category.getLevel() - 1);
            if (!list.isEmpty()) {
                Collections.sort(list);
                flatten.add(Task.Separator(category));
                flatten.addAll(list);
            }
        }
        clear();
        addAll(flatten);
    }


    public int addFromList(List<Task> taskList, int filter) {
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.HOUR, -30);
        currentTime = newDate.getTime();

        List<List<Task>> group = new ArrayList<List<Task>>(6);
        for (int i = 0; i < 6; i++) {
            group.add(new ArrayList<Task>());
        }

        for (Task t : taskList) {
            if (filter == LIST_FILTER_OVERDUE && t.getDeadlineOrigin().compareTo(currentTime) >= 0) {
                continue;
            }
            if (filter == LIST_FILTER_ONTIME && t.getDeadlineOrigin().compareTo(currentTime) < 0) {
                continue;
            }
            group.get(t.category.getLevel() - 1).add(t);
        }
        List<Task> flatten = new ArrayList<>();
        for (TaskCategoryEnum category : TaskCategoryEnum.values()){
            List<Task> list = group.get(category.getLevel() - 1);
            if(!list.isEmpty()){
                Collections.sort(list);
                flatten.add(Task.Separator(category));
                flatten.addAll(list);
            }
        }
        clear();
        addAll(flatten);
        return flatten.size();
    }


    static class TaskViewHolder{
        View self;
        TextView name;

        public void populateView(Context mContext, Task task) {
            name.setText(task.name);
        };
    }

    private static class TaskSeparatorViewHolder extends TaskViewHolder{

        @Override
        public void populateView(Context mContext, Task task) {
            super.populateView(mContext, task);
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
        TextView deadline;
        TextView repetition;

        @Override
        public void populateView(Context mContext, Task task) {
            super.populateView(mContext, task);
            priority.setRating(task.priority);

            if (task.trackable) {
                repetition.setVisibility(View.VISIBLE);
                repetition.setText("Left: " + String.valueOf(task.countDown));
            } else {
                repetition.setVisibility(View.INVISIBLE);
            }

            if (task.getDeadlineOrigin().compareTo(currentTime) < 0) {
                self.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWarn));
                deadline.setText(R.string.label_overdue);
            } else {
                String deadlineLabel = mContext.getResources().getString(R.string.label_due, task.getDeadline());
                deadline.setText(deadlineLabel);
            }
        }
    }

}
