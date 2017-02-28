package com.justinhu.whattodo;

/**
 * Created by justinhu on 2017-02-22.
 */

public enum TaskCategoryEnum {
    WORK(6),
    SCHOOL(5),
    DEFAULT(4),
    EXERCISE(3),
    PERSONAL(2),
    RELAX(1);

    int level;

    TaskCategoryEnum(int i) {
        level = i;
    }

    public int getLevel(){
        return this.level;
    }
}
