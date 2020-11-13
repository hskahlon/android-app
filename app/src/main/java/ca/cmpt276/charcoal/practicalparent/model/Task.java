package ca.cmpt276.charcoal.practicalparent.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    private String taskName;
    private Child child;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setChild(Child child){
        this.child = child;
    }

    public Child getChild(){
        return child;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("taskName", this.taskName);
            obj.put("Child", this.child);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


}
