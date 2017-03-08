package com.justinhu.whattodo;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by justinhu on 2017-02-23.
 */

class TaskContract implements Comparable,Serializable{
    static SimpleDateFormat dateFormatter  = new SimpleDateFormat("E, MMM dd, yyyy", Locale.US);
    private int id;
    static String DEFAULT_DATE = "No Deadline";
    public String name;
    TaskCategoryEnum category;
    int priority;
    boolean trackable;
    int countDown;
    int countUp;
    private Date deadlineOrigin = null;
    String deadline;
    private boolean isSeperator;


    TaskContract(String name, TaskCategoryEnum category, int priority, boolean trackable, int countDown, int countUp, String deadline) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.trackable = trackable;
        this.countDown = countDown;
        this.countUp = countUp;
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

    static TaskContract Separator(TaskCategoryEnum category){
        return new TaskContract(category);
    }
    private void restoreDate(){
        try{
            this.deadlineOrigin = dateFormatter.parse(this.deadline);
        } catch (ParseException e) {
            this.deadlineOrigin = new Date(Long.MAX_VALUE);
        }
    }

    void setId(int id){
        this.id = id;
    }
    int getId(){
        return this.id;
    }
    boolean isSeperator(){
        return this.isSeperator;
    }

    @Override
    public int compareTo(@NonNull Object o) {
       TaskContract otherTask = (TaskContract) o;
        int priorityOrder = otherTask.priority*otherTask.category.getLevel() - this.priority* this.category.getLevel();
        if(priorityOrder !=0){
            return priorityOrder;
        }

        if(this.trackable != otherTask.trackable){
            if(otherTask.trackable){
                return 1;
            }else{
                return -1;
            }
        }

        if(this.trackable){
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
            return this.countDown - otherTask.countDown;
        }else{
            return this.countUp - otherTask.countUp;
        }
    }
}



