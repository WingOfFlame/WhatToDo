package com.justinhu.whattodo;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by justinhu on 2017-02-23.
 */

public class TaskContract implements Comparable,Serializable{
    public static SimpleDateFormat dateFormatter  = new SimpleDateFormat("E, MMM dd, yyyy", Locale.US);
    private int id;
    public static String DEFAULT_DATE = "No Deadline";
    public String name;
    public TaskCategoryEnum category;
    public int priority;
    public boolean trackable;
    public int repetition;
    private Date deadlineOrigin = null;
    public String deadline;
    private boolean isSeperator;


    public TaskContract(String name, TaskCategoryEnum category, int priority, boolean trackable, int repetition, String deadline) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.trackable = trackable;
        this.repetition = repetition;
        this.deadline = deadline;
        this.isSeperator = false;
        this.id = -1;
    }

    @Override
    public String toString() {
        return "Name:"+this.name;
    }

    private TaskContract(TaskCategoryEnum category){
        this.name = category.name();
        this.category = category;
        this.isSeperator = true;
    }

    public static TaskContract Separator(TaskCategoryEnum category){
        return new TaskContract(category);
    }
    private void restoreDate(){
        try{
            this.deadlineOrigin = dateFormatter.parse(this.deadline);
        } catch (ParseException e) {
            this.deadlineOrigin = new Date(Long.MAX_VALUE);
        }
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public boolean isSeperator(){
        return this.isSeperator;
    }
    @Override
    public int compareTo(Object o) {
       TaskContract otherTask = (TaskContract) o;
        if(this.deadlineOrigin == null){
            this.restoreDate();
        }
        if(otherTask.deadlineOrigin == null){
            otherTask.restoreDate();
        }

        int dateOrder = this.deadlineOrigin.compareTo(otherTask.deadlineOrigin);
        if (dateOrder !=0){
            return dateOrder;
        }

        return otherTask.priority*otherTask.category.getLevel() - this.priority* this.category.getLevel();
    }
}



