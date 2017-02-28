package com.justinhu.whattodo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by justinhu on 2017-02-24.
 */

public class TaskSelector {
    private static Random randomGenerator = new Random();

    public static TaskContract selectTask(List<TaskContract> taskList){
        Collections.sort(taskList);
        ArrayList<TaskContract> pending = new ArrayList<>();
        TaskContract first = taskList.get(0);
        pending.add(first);
        for (int i = 1; i<taskList.size(); i++){
            TaskContract current = taskList.get(i);
            if(current.compareTo(first) == 0){
               pending.add(current);
            }
        }
        int index = randomGenerator.nextInt(pending.size());
        return pending.get(index);
    }

}
