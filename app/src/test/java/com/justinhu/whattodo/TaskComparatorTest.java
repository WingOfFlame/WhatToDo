package com.justinhu.whattodo;

import com.justinhu.whattodo.model.Task;
import com.justinhu.whattodo.model.TaskCategoryEnum;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskComparatorTest {
    @Test
    public void deadline_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.WORK, 3, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.WORK, 3, true, 1, "Fri, Feb 24, 2017");
        assertTrue( high.compareTo(low) <0);

    }

    @Test
    public void nodeadline_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.WORK, 3, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.WORK, 3, false, 1, Task.DEFAULT_DATE);
        assertTrue( high.compareTo(low) <0);

    }

    @Test
    public void category_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.WORK, 3, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.SCHOOL, 3, false, 1, "Thu, Feb 23, 2017");
        assertTrue( high.compareTo(low) <0);
    }

    @Test
    public void priority_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.WORK, 5, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.WORK, 3, false, 1, "Thu, Feb 23, 2017");
        assertTrue( high.compareTo(low) <0);

    }

    @Test
    public void categ0ry_and_priority_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.WORK, 2, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.PERSONAL, 5, false, 1, "Thu, Feb 23, 2017");
        assertTrue( high.compareTo(low) <0);

    }

    @Test
    public void deadline_dominate_comparison(){
        Task high = new Task("task1", TaskCategoryEnum.PERSONAL, 1, true, 1, "Thu, Feb 23, 2017");
        Task low = new Task("task2", TaskCategoryEnum.WORK, 5, false, 1, "Fri, Feb 24, 2017");
        assertTrue( high.compareTo(low) <0);

    }
}
