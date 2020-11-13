package ca.cmpt276.charcoal.practicalparent.model;

import java.util.ArrayList;
import java.util.List;

public class TasksManager {
    List<Task> tasks = new ArrayList<>();

    private static TasksManager instance;
    private TasksManager() {
    }

    public static TasksManager getInstance() {
        if (instance == null) {
            instance = new TasksManager();
        }
        return instance;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task getTaskIdx(int index) {
        return tasks.get(index);
    }

    public int getArraySize() {
        return tasks.size();
    }

    public void remove(int index) {
        tasks.remove(index);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks (List<Task> tasks) {
        this.tasks = tasks;
    }
}
