package ca.cmpt276.charcoal.practicalparent.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Task {
    private String taskName;
    private int childIdx = 0;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getChildIdx() {
        return childIdx;
    }

    public void setChildIdx(int childIdx) {
        this.childIdx = childIdx;
    }

    public void decrementChildIdx(){
        this.childIdx--;
    }

    @NonNull
    @Override
    public String toString() {
        ChildManager manager = ChildManager.getInstance();
        if(manager.getChildren().size() <= 0 ){
            return ("Task: " + taskName + "  Name: No Childern Added" );
        }
        else{
            return ("Task: " + taskName + "  Name: " + manager.getChild(childIdx).getName());
        }

    }

}