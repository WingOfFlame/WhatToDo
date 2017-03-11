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

class Task implements Comparable, Serializable {
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    static String DEFAULT_DATE = "No Deadline";
    static String DEFAULT_TITLE = "(No Name)";

    private int id;
    String name;
    TaskCategoryEnum category;
    int priority;
    boolean trackable;
    int countDown;
    int countUp;
    private Date deadlineOrigin = null;
    private String deadline;
    private boolean isSeperator;


    Task(String name, TaskCategoryEnum category, int priority, boolean trackable, int countDown, int countUp, String deadline) {
        if (name.equals("")) {
            name = DEFAULT_TITLE;
        }
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.trackable = trackable;
        this.countDown = countDown;
        this.countUp = countUp;
        this.deadline = deadline;
        this.isSeperator = false;
        this.id = -1;
        restoreDate();
    }

    @Override
    public String toString() {
        return "Name:"+this.name;
    }

    private Task(TaskCategoryEnum category) {
        this.name = category.name();
        this.category = category;
        this.isSeperator = true;
    }

    static Task Separator(TaskCategoryEnum category) {
        return new Task(category);
    }

    public Date getDeadlineOrigin() {
        return deadlineOrigin;
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
        Task otherTask = (Task) o;

        int dateOrder = this.deadlineOrigin.compareTo(otherTask.deadlineOrigin);
        if (dateOrder != 0) {
            return dateOrder;
        }

        int priorityOrder = otherTask.priority * otherTask.category.getLevel() - this.priority * this.category.getLevel();

        return priorityOrder;

    }

    public String getDeadline() {
        return deadline;
    }

    public void incrementCount() {
        this.countUp += 1;
    }
}



