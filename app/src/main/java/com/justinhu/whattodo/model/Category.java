package com.justinhu.whattodo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by justinhu on 2017-03-12.
 */

public class Category implements Comparable ,Serializable {
    public static final String NAME_WORK = "string_category_work";
    public static final String NAME_STUDY = "string_category_study";
    public static final String NAME_DEFAULT = "string_category_default";
    public static final String NAME_WORKOUT = "string_category_workout";
    public static final String NAME_PERSONAL = "string_category_personal";
    public static final String NAME_RELAX = "string_category_relax";

    public static Dictionary<String, Category> lookupTable = new Hashtable<String, Category>();
    public String name;
    public String color;
    public String icon;
    private int displayNameId;
    private int iconId;
    private int id;
    private int priority;
    private int isDefault;

    public Category(String name, String color, String icon, int priority, int isDefault) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.priority = priority;
        this.isDefault = isDefault;
    }

    @Nullable
    public static Category getCategory(String name){
        return lookupTable.get(name);
    }

    public static Category getDefaultCategory(){
        return lookupTable.get(NAME_DEFAULT);
    }

    public static void updateLookupTable(List<Category> data) {
        Collections.sort(data);
        for(Category c :data ){
            lookupTable.put(c.name,c);
        }
    }

    public int getDisplayNameId() {
        return displayNameId;
    }

    public void setDisplayNameId(int displayNameId) {
        this.displayNameId = displayNameId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Category other = (Category) o;
        return other.priority-this.priority;
    }

    public int getIsDefault() {
        return isDefault;
    }
}
