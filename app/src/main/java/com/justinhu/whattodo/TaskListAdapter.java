package com.justinhu.whattodo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.justinhu.whattodo.model.Category;
import com.justinhu.whattodo.model.Task;

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
    private static int colorWarn = 0;
    private static String deadlineLabel = "";
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
        colorWarn = ContextCompat.getColor(mContext, R.color.colorWarn);
        deadlineLabel = mContext.getResources().getString(R.string.label_due, "*");
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

        if(task != null) {
            //Category c = Category.lookupTable.get(task.category);
            //holder.populateView(c, task);
        }

        return convertView;
    }




    public int addFromList(List<Task> taskList, int filter) {
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.HOUR, -30);
        currentTime = newDate.getTime();
        int length =Category.lookupTable.size();
        List<List<Task>> group = new ArrayList<List<Task>>(length);
        for (int i = 0; i < length; i++) {
            group.add(new ArrayList<Task>());
        }

        for (Task t : taskList) {
            if (filter == LIST_FILTER_OVERDUE && t.getDeadlineOrigin().compareTo(currentTime) >= 0) {
                continue;
            }
            if (filter == LIST_FILTER_ONTIME && t.getDeadlineOrigin().compareTo(currentTime) < 0) {
                continue;
            }
            //Category c = Category.lookupTable.get(t.category);
            //group.get(length - c.getPriority()).add(t);
        }
        List<Task> flatten = new ArrayList<>();

        for (List<Task> list : group){
            if(!list.isEmpty()){
                Collections.sort(list);
                //String categoryName = list.get(0).category;
                //flatten.add(Task.SeparatorInstance(categoryName));
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

        public void populateView(Category c,Task task) {
            name.setText(task.name);
        };
    }

    private static class TaskSeparatorViewHolder extends TaskViewHolder{

        @Override
        public void populateView(Category c, Task task) {
            super.populateView(c,task);
            name.setText(c.getDisplayNameId());
            name.setBackgroundColor(Color.parseColor(c.color));

        }
    }

    private static class TaskItemViewHolder extends TaskViewHolder{
        RatingBar priority;
        TextView deadline;
        TextView repetition;

        @Override
        public void populateView(Category c, Task task) {
            super.populateView(c, task);
            priority.setRating(task.priority);

            if (task.trackable) {
                repetition.setVisibility(View.VISIBLE);
                repetition.setText("Left: " + String.valueOf(task.countDown));
            } else {
                repetition.setVisibility(View.INVISIBLE);
            }

            if (task.getDeadlineOrigin().compareTo(currentTime) < 0) {
                self.setBackgroundColor(colorWarn);
                deadline.setText(R.string.label_overdue);
            } else {
                deadline.setText(deadlineLabel.replace("*",task.getDeadline()));
            }
        }
    }

}
