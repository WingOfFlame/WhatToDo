package com.justinhu.whattodo;

import com.justinhu.whattodo.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskSelector {
    private static Random randomGenerator = new Random();

    public static Task selectTask(List<Task> taskList) {
        Collections.sort(taskList);
        ArrayList<Task> pending = new ArrayList<>();
        Calendar newDate = Calendar.getInstance();
        newDate.add(Calendar.HOUR, -30);

        Date currentTime = newDate.getTime();
        int index = 0;
        while (taskList.get(index).getDeadlineOrigin().compareTo(currentTime) < 0) {
            index++;
        }
        Task first = taskList.get(index++);
        pending.add(first);
        for (int i = index; i < taskList.size(); i++) {
            Task current = taskList.get(i);
            if(current.compareTo(first) == 0){
               pending.add(current);
            }
        }
        int result = randomGenerator.nextInt(pending.size());
        return pending.get(result);
    }

}
