package com.justinhu.whattodo.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by justinhu on 2017-03-12.
 */

public class Category implements Comparable ,Serializable {
    public Category(String name, String color, String icon, int priority, int isDefault) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.priority = priority;
        this.isDefault = isDefault;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    public String name;
    public String color;
    public String icon;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    private int priority;
    private int isDefault;

    @Override
    public int compareTo(@NonNull Object o) {
        Category other = (Category) o;
        return other.priority-this.priority;
    }

    public int getIsDefault() {
        return isDefault;
    }
}
