package com.justinhu.whattodo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskSelectorTest {


    @Test
    public void priority_isCorrect() {
        ArrayList<TaskContract> taskList = new ArrayList<>();
        TaskContract a = new TaskContract("task1",TaskCategoryEnum.WORK, 3,true,1,"Thu, Feb 23, 2017");
        TaskContract b = new TaskContract("task2",TaskCategoryEnum.PERSONAL, 5,false,1,TaskContract.DEFAULT_DATE);
        TaskContract c = new TaskContract("task1",TaskCategoryEnum.SCHOOL, 2,true,77,"Thu, Feb 23, 2017");
        TaskContract d = new TaskContract("task2",TaskCategoryEnum.WORK, 3,false,1,TaskContract.DEFAULT_DATE);
        taskList.add(a);
        taskList.add(b);
        taskList.add(c);
        taskList.add(d);
        assertEquals(TaskSelector.selectTask(taskList), a);
    }

    @Test
    public void random_isCorrect(){
        ArrayList<TaskContract> taskList = new ArrayList<>();
        TaskContract a = new TaskContract("task1",TaskCategoryEnum.WORK, 2,true,1,"Thu, Feb 23, 2017");
        TaskContract b = new TaskContract("task2",TaskCategoryEnum.DEFAULT, 3,true,1,"Thu, Feb 23, 2017");
        TaskContract c = new TaskContract("task3",TaskCategoryEnum.EXERCISE, 4,true,1,"Thu, Feb 23, 2017");
        TaskContract d = new TaskContract("task4",TaskCategoryEnum.WORK, 3,true,1,TaskContract.DEFAULT_DATE);
        taskList.add(a);
        taskList.add(b);
        taskList.add(c);
        taskList.add(d);
        for( int i =0; i < 20; i++){
            assertNotEquals(TaskSelector.selectTask(taskList), d);
        }

    }
}
