package eugene.com.todoer;

import eugene.com.todoer.data.Task;

import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    private final ConcurrentHashMap<Integer, Task> tasksMap;

    public Storage() {
        this.tasksMap = new ConcurrentHashMap<>();
    }

    public void addTaskToMap(int id, Task task) {
        tasksMap.put(id, task);
    }

    public ConcurrentHashMap<Integer, Task> getTasksMap() {
        return tasksMap;
    }
}
