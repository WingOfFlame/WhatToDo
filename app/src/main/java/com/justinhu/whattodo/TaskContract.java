package com.justinhu.whattodo;

import android.provider.BaseColumns;

/**
 * Created by justinhu on 2017-02-23.
 */

public class TaskContract {
    public String name;
    public TaskCategoryEnum category;
    public int priority;
    public boolean trackable;
    public int repetition;
    public String deadline;

    public TaskContract(String name, TaskCategoryEnum category, int priority, boolean trackable, int repetition, String deadline) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.trackable = trackable;
        this.repetition = repetition;
        this.deadline = deadline;
    }

}



