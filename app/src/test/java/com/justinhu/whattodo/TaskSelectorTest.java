package com.justinhu.whattodo;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskSelectorTest {


    @Test
    public void priority_isCorrect() {
        ArrayList<Task> taskList = new ArrayList<>();
        Task a = new Task("task1", TaskCategoryEnum.WORK, 3, true, 1, "Thu, Feb 23, 2017");
        Task b = new Task("task2", TaskCategoryEnum.PERSONAL, 5, false, 1, Task.DEFAULT_DATE);
        Task c = new Task("task1", TaskCategoryEnum.SCHOOL, 2, true, 77, "Thu, Feb 23, 2017");
        Task d = new Task("task2", TaskCategoryEnum.WORK, 3, false, 1, Task.DEFAULT_DATE);
        taskList.add(a);
        taskList.add(b);
        taskList.add(c);
        taskList.add(d);
        assertEquals(TaskSelector.selectTask(taskList), a);
    }

    @Test
    public void random_isCorrect(){
        ArrayList<Task> taskList = new ArrayList<>();
        Task a = new Task("task1", TaskCategoryEnum.WORK, 2, true, 1, "Thu, Feb 23, 2017");
        Task b = new Task("task2", TaskCategoryEnum.DEFAULT, 3, true, 1, "Thu, Feb 23, 2017");
        Task c = new Task("task3", TaskCategoryEnum.EXERCISE, 4, true, 1, "Thu, Feb 23, 2017");
        Task d = new Task("task4", TaskCategoryEnum.WORK, 3, true, 1, Task.DEFAULT_DATE);
        taskList.add(a);
        taskList.add(b);
        taskList.add(c);
        taskList.add(d);
        for( int i =0; i < 20; i++){
            assertNotEquals(TaskSelector.selectTask(taskList), d);
        }

    }
}
