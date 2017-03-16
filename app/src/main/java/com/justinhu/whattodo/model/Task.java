package com.justinhu.whattodo.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by justinhu on 2017-02-23.
 */

public class Task implements Comparable, Serializable {
    public static boolean useCategory = false;
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    public static String DEFAULT_DATE = "No Deadline";
    public static String DEFAULT_TITLE = "(No Name)";

    private int id;
    public String name;
    public TaskCategoryEnum category;
    public int priority;
    public boolean trackable;
    public int countDown;
    public int countUp;
    private Date deadlineOrigin = null;
    private String deadline;
    private boolean isSeperator;


    public Task(String name, TaskCategoryEnum category, int priority, boolean trackable, int countDown, int countUp, String deadline) {
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

    public static Task Separator(TaskCategoryEnum category) {
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
    public int compareTo(@NonNull Object o) {
        Task otherTask = (Task) o;

        int dateOrder = this.deadlineOrigin.compareTo(otherTask.deadlineOrigin);
        if (dateOrder != 0) {
            return dateOrder;
        }
        if(useCategory){
            return otherTask.priority * otherTask.category.getLevel() - this.priority * this.category.getLevel();
        }

        return otherTask.priority  - this.priority;

    }

    public String getDeadline() {
        return deadline;
    }

    public void incrementCount() {
        this.countUp += 1;
    }
}



